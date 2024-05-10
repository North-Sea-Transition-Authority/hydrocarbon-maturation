package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.complexity.ComplexityUtil;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.util.BigDecimalUtil;

@Service
public class UndevelopedDiscoveryInformationService {

  private final UndevelopedDiscoveryInformationRepository informationRepository;

  private static final BigDecimal GAS_TO_OIL_EQUIVALENT_CONVERSION_FACTOR = BigDecimal.valueOf(5.8);

  @Autowired
  public UndevelopedDiscoveryInformationService(UndevelopedDiscoveryInformationRepository informationRepository) {
    this.informationRepository = informationRepository;
  }

  @Transactional
  public void saveUndevelopedDiscoveryInformation(UndevelopedDiscoveryInformationForm form,
                                                  UndevelopedDiscovery undevelopedDiscovery) {
    var newDate = getDateFromForm(form);
    var undevelopedDiscoveryInformation = getOrderedDiscoveryInformation(undevelopedDiscovery)
        .stream()
        .filter(information -> information.getInformationDate().equals(newDate))
        .findFirst()
        .orElseGet(UndevelopedDiscoveryInformation::new);

    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(undevelopedDiscovery);
    undevelopedDiscoveryInformation.setInformationDate(newDate);

    var volumetrics = form.getVolumetricsForm();
    addVolumetrics(volumetrics, undevelopedDiscoveryInformation);

    var providedDiscoveryComplexity = form.getProvideDiscoveryComplexity();
    undevelopedDiscoveryInformation.setProvidedDiscoveryComplexity(providedDiscoveryComplexity);
    if (BooleanUtils.isTrue(providedDiscoveryComplexity)) {
      var complexityForm = form.getComplexityForm();
      addComplexity(complexityForm, undevelopedDiscoveryInformation);
    }

    calculateAndAddOilEquivalentRecoverableResources(undevelopedDiscoveryInformation);
    calculateAndAddPercentageGas(undevelopedDiscoveryInformation);

    informationRepository.save(undevelopedDiscoveryInformation);
  }

  @Transactional
  public void deleteInformation(UndevelopedDiscoveryInformation information) {
    information.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);
    informationRepository.save(information);
  }

  public List<UndevelopedDiscoveryInformation> getOrderedDiscoveryInformation(
      UndevelopedDiscovery undevelopedDiscovery) {
    return informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery)
        .stream()
        .filter(this::isExtant)
        .sorted(Comparator.comparing(UndevelopedDiscoveryInformation::getInformationDate).reversed())
        .toList();
  }

  public UndevelopedDiscoveryInformation getUndevelopedDiscoveryInformationOrThrow(UUID uuid) {
    return informationRepository.findById(uuid)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No undeveloped discovery information found for id %s".formatted(uuid)));
  }

  public UndevelopedDiscoveryInformationForm entityToForm(
      UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var form = new UndevelopedDiscoveryInformationForm();

    var date = undevelopedDiscoveryInformation.getInformationDate();
    form.setDiscoveryInformationDateDay(String.valueOf(date.getDayOfMonth()));
    form.setDiscoveryInformationDateMonth(String.valueOf(date.getMonthValue()));
    form.setDiscoveryInformationDateYear(String.valueOf(date.getYear()));

    var volumetrics = form.getVolumetricsForm();
    volumetrics.setOilInPlace(DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getOilInPlaceP50()));
    volumetrics.setGasInPlace(DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getGasInPlaceP50()));
    volumetrics.setVolumetricDifferenceReason(undevelopedDiscoveryInformation.getDifferenceReason());

    var oilRecoverable = volumetrics.getOilRecoverable();
    oilRecoverable.setOneC(DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getOilRecoverableOneC()));
    oilRecoverable.setTwoC(DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getOilRecoverableTwoC()));
    oilRecoverable.setThreeC(
        DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getOilRecoverableThreeC()));

    var gasRecoverable = volumetrics.getGasRecoverable();
    gasRecoverable.setOneC(DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getGasRecoverableOneC()));
    gasRecoverable.setTwoC(DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getGasRecoverableTwoC()));
    gasRecoverable.setThreeC(
        DecimalInput.createDecimalInput(undevelopedDiscoveryInformation.getGasRecoverableThreeC()));

    form.setProvideDiscoveryComplexity(undevelopedDiscoveryInformation.getProvidedDiscoveryComplexity());
    if (BooleanUtils.isTrue(form.getProvideDiscoveryComplexity())) {
      var complexity = form.getComplexityForm();
      complexity.setViscosityRange(undevelopedDiscoveryInformation.getViscosityRange());
      complexity.setStructuralComplexityRange(undevelopedDiscoveryInformation.getStructuralComplexityRange());
      complexity.setArealDensityRange(undevelopedDiscoveryInformation.getArealDensityRange());
      complexity.setVerticalHeterogeneityRange(undevelopedDiscoveryInformation.getVerticalHeterogeneityRange());
    }

    return form;
  }

  private LocalDate getDateFromForm(UndevelopedDiscoveryInformationForm form) {
    return LocalDate.of(
        Integer.parseInt(form.getDiscoveryInformationDateYear()),
        Integer.parseInt(form.getDiscoveryInformationDateMonth()),
        Integer.parseInt(form.getDiscoveryInformationDateDay())
    );
  }

  private boolean isExtant(UndevelopedDiscoveryInformation information) {
    return information.getStatus().equals(UndevelopedDiscoveryInformationStatus.EXTANT);
  }

  private void addVolumetrics(UndevelopedDiscoveryVolumetricsForm form,
                              UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    undevelopedDiscoveryInformation.setGasInPlaceP50(form.getGasInPlace().createBigDecimalOrNull());

    var gasRecoverable = form.getGasRecoverable();
    undevelopedDiscoveryInformation.setGasRecoverableOneC(gasRecoverable.getOneC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setGasRecoverableTwoC(gasRecoverable.getTwoC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setGasRecoverableThreeC(gasRecoverable.getThreeC().createBigDecimalOrNull());

    undevelopedDiscoveryInformation.setOilInPlaceP50(form.getOilInPlace().createBigDecimalOrNull());

    var oilRecoverable = form.getOilRecoverable();
    undevelopedDiscoveryInformation.setOilRecoverableOneC(oilRecoverable.getOneC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setOilRecoverableTwoC(oilRecoverable.getTwoC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setOilRecoverableThreeC(oilRecoverable.getThreeC().createBigDecimalOrNull());

    undevelopedDiscoveryInformation.setDifferenceReason(form.getVolumetricDifferenceReason());
  }

  private void addComplexity(UndevelopedDiscoveryComplexityForm complexityForm,
                             UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    undevelopedDiscoveryInformation.setStructuralComplexityRange(complexityForm.getStructuralComplexityRange());
    undevelopedDiscoveryInformation.setArealDensityRange(complexityForm.getArealDensityRange());
    undevelopedDiscoveryInformation.setVerticalHeterogeneityRange(complexityForm.getVerticalHeterogeneityRange());
    undevelopedDiscoveryInformation.setViscosityRange(complexityForm.getViscosityRange());

    var calculatedComplexity = ComplexityUtil.calculateComplexity(complexityForm);
    undevelopedDiscoveryInformation.setCalculatedComplexity(calculatedComplexity);
  }

  private void calculateAndAddOilEquivalentRecoverableResources(UndevelopedDiscoveryInformation information) {
    var oilEquivalentOneC = calculateOilEquivalentRecoverableResources(information.getOilRecoverableOneC(),
        information.getGasRecoverableOneC());
    information.setOilEquivalentRecoverableOneC(oilEquivalentOneC);

    var oilEquivalentTwoC = calculateOilEquivalentRecoverableResources(information.getOilRecoverableTwoC(),
        information.getGasRecoverableTwoC());
    information.setOilEquivalentRecoverableTwoC(oilEquivalentTwoC);

    var oilEquivalentThreeC = calculateOilEquivalentRecoverableResources(information.getOilRecoverableThreeC(),
        information.getGasRecoverableThreeC());
    information.setOilEquivalentRecoverableThreeC(oilEquivalentThreeC);
  }

  /*
   * calculates mmboe subject to the formula mmboe(?C) = (?C oil recoverable) + ((?C gas recoverable)/5.8)
   * @params BigDecimal oilRecoverable, BigDecimal gasRecoverable
   * @return calculated mmboe, or zero (i.e. there is no oil/gas recoverable)
   */
  private BigDecimal calculateOilEquivalentRecoverableResources(BigDecimal oilRecoverable, BigDecimal gasRecoverable) {
    oilRecoverable = BigDecimalUtil.getValueOrZero(oilRecoverable);
    gasRecoverable = BigDecimalUtil.getValueOrZero(gasRecoverable);

    var gasDividedByConstant = BigDecimalUtil.isValueZero(gasRecoverable)
        ? gasRecoverable
        : gasRecoverable.divide(GAS_TO_OIL_EQUIVALENT_CONVERSION_FACTOR, MathContext.DECIMAL64);

    return oilRecoverable.add(gasDividedByConstant)
        .setScale(2, RoundingMode.HALF_EVEN);
  }

  /*
   * calculates percentage gas subject to the formula (*100 abstracted): % gas = 1 - ((2C oil recoverable)/(2C mmboe))
   * @params UndevelopedDiscoveryInformation information
   * @return void, method sets the value on the given entity
   */
  private void calculateAndAddPercentageGas(UndevelopedDiscoveryInformation information) {
    var oilRecoverable = BigDecimalUtil.getValueOrZero(information.getOilRecoverableTwoC());
    var oilEquivalentRecoverable = BigDecimalUtil.getValueOrZero(information.getOilEquivalentRecoverableTwoC());

    if (!BigDecimalUtil.isValueZero(oilEquivalentRecoverable) && !BigDecimalUtil.isValueZero(oilRecoverable)) {
      var oilRecoverableDividedByOilEquivalent = oilRecoverable.divide(oilEquivalentRecoverable, MathContext.DECIMAL64);

      var percentageGas = BigDecimal.ONE
          .subtract(oilRecoverableDividedByOilEquivalent)
          .multiply(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN);

      information.setPercentageGas(percentageGas);
    }
  }
}