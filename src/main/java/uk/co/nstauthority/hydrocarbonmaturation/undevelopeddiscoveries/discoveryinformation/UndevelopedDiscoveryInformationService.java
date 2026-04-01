package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
                                                  UndevelopedDiscovery undevelopedDiscovery,
                                                  UUID informationId) {

    var undevelopedDiscoveryInformation = informationId != null
        ? getUndevelopedDiscoveryInformationOrThrow(informationId)
        : new UndevelopedDiscoveryInformation();

    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(undevelopedDiscovery);
    undevelopedDiscoveryInformation.setInformationDate(getDateFromForm(form));

    var volumetrics = form.getVolumetricsForm();
    addVolumetrics(volumetrics, undevelopedDiscoveryInformation);

    var providedDiscoveryComplexity = form.getProvideDiscoveryComplexity();
    undevelopedDiscoveryInformation.setProvidedDiscoveryComplexity(providedDiscoveryComplexity);
    if (BooleanUtils.isTrue(providedDiscoveryComplexity)) {
      var complexityForm = form.getComplexityForm();
      addComplexity(complexityForm, undevelopedDiscoveryInformation);
    }

    calculateAndAddOilEquivalentRecoverableResources(undevelopedDiscoveryInformation);

    var percentageGas = calculatePercentageGas(undevelopedDiscoveryInformation);
    undevelopedDiscoveryInformation.setPercentageGas(percentageGas);

    informationRepository.save(undevelopedDiscoveryInformation);
  }

  @Transactional
  public void deleteInformation(UndevelopedDiscoveryInformation information) {
    information.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);
    informationRepository.save(information);
  }

  public List<UndevelopedDiscoveryInformation> getOrderedDiscoveryInformation(
      UndevelopedDiscovery undevelopedDiscovery) {
    return informationRepository.findAllByUndevelopedDiscoveryAndStatus(
        undevelopedDiscovery, UndevelopedDiscoveryInformationStatus.EXTANT)
        .stream()
        .sorted(Comparator.comparing(UndevelopedDiscoveryInformation::getInformationDate).reversed())
        .toList();
  }

  public List<UndevelopedDiscoveryInformation> getLatestDiscoveryInformation(List<UndevelopedDiscovery> undevelopedDiscoveries) {
    Map<UUID, List<UndevelopedDiscoveryInformation>> infoMap = informationRepository.findAllByUndevelopedDiscoveryInAndStatus(
        undevelopedDiscoveries, UndevelopedDiscoveryInformationStatus.EXTANT
    ).stream()
        .collect(
            Collectors.groupingBy(
                undevelopedDiscoveryInformation -> undevelopedDiscoveryInformation.getUndevelopedDiscovery().getId(),
                Collectors.toList())
        );

    return undevelopedDiscoveries.stream()
        .map(UndevelopedDiscovery::getId)
        .map(d -> infoMap.getOrDefault(d, List.of()))
        .map(this::getLatestInformationFromList)
        .flatMap(Optional::stream)
        .toList();
  }

  private Optional<UndevelopedDiscoveryInformation> getLatestInformationFromList(
      List<UndevelopedDiscoveryInformation> undevelopedDiscoveryInformationList
  ) {
    return undevelopedDiscoveryInformationList
        .stream()
        .max(Comparator.comparing(UndevelopedDiscoveryInformation::getInformationDate));
  }

  public UndevelopedDiscoveryInformation getUndevelopedDiscoveryInformationOrThrow(UUID id) {
    return informationRepository.findById(id).orElseThrow(() -> new RemiEntityNotFoundException(
            "No undeveloped discovery information found for id %s".formatted(id)));
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
    var oilEquivalentOneC = calculateOilEquivalentRecoverableResources(
        information.getOilRecoverableOneC() != null ? information.getOilRecoverableOneC() : BigDecimal.ZERO,
        information.getGasRecoverableOneC() != null ? information.getGasRecoverableOneC() : BigDecimal.ZERO);
    information.setOilEquivalentRecoverableOneC(oilEquivalentOneC);

    var oilEquivalentTwoC = calculateOilEquivalentRecoverableResources(
        information.getOilRecoverableTwoC() != null ? information.getOilRecoverableTwoC() : BigDecimal.ZERO,
        information.getGasRecoverableTwoC() != null ? information.getGasRecoverableTwoC() : BigDecimal.ZERO);
    information.setOilEquivalentRecoverableTwoC(oilEquivalentTwoC);

    var oilEquivalentThreeC = calculateOilEquivalentRecoverableResources(
        information.getOilRecoverableThreeC() != null ? information.getOilRecoverableThreeC() : BigDecimal.ZERO,
        information.getGasRecoverableThreeC() != null ? information.getGasRecoverableThreeC() : BigDecimal.ZERO);
    information.setOilEquivalentRecoverableThreeC(oilEquivalentThreeC);
  }

  /*
   * calculates mmboe subject to the formula mmboe(?C) = (?C oil recoverable) + ((?C gas recoverable)/5.8)
   * @params BigDecimal oilRecoverable, BigDecimal gasRecoverable
   * @return calculated mmboe
   */
  private BigDecimal calculateOilEquivalentRecoverableResources(BigDecimal oilRecoverable, BigDecimal gasRecoverable) {
    var gasDividedByConstant = BigDecimalUtil.isValueZero(gasRecoverable)
        ? gasRecoverable
        : gasRecoverable.divide(GAS_TO_OIL_EQUIVALENT_CONVERSION_FACTOR, MathContext.DECIMAL64);

    return oilRecoverable.add(gasDividedByConstant)
        .setScale(2, RoundingMode.HALF_EVEN);
  }

  /*
   * calculates percentage gas subject to the formula (*100 abstracted): % gas = 1 - ((2C oil recoverable)/(2C mmboe))
   * @params UndevelopedDiscoveryInformation information
   * @return BigDecimal percentage gas
   */
  private BigDecimal calculatePercentageGas(UndevelopedDiscoveryInformation information) {
    var oilRecoverable = information.getOilRecoverableTwoC() != null ? information.getOilRecoverableTwoC() : BigDecimal.ZERO;
    var oilEquivalentRecoverable = information.getOilEquivalentRecoverableTwoC();

    if (BigDecimalUtil.isValueZero(oilEquivalentRecoverable)) {
      // there is no oil or gas
      return BigDecimal.ZERO;
    } else if (BigDecimalUtil.isValueZero(oilRecoverable)) {
      // no oil, must all be gas
      return BigDecimal.valueOf(100);
    } else {
      var oilRecoverableDividedByOilEquivalent = oilRecoverable.divide(oilEquivalentRecoverable, MathContext.DECIMAL64);

      return BigDecimal.ONE.subtract(oilRecoverableDividedByOilEquivalent)
          .multiply(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN);
    }
  }
}