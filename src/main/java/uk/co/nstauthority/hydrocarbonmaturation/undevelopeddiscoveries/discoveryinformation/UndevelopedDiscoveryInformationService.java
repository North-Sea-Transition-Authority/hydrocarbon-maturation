package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;

@Service
public class UndevelopedDiscoveryInformationService {

  private final UndevelopedDiscoveryInformationRepository informationRepository;


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

    undevelopedDiscoveryInformation.setUndevelopedDiscovery(undevelopedDiscovery);

    undevelopedDiscoveryInformation.setInformationDate(newDate);

    // volumetrics
    var volumetrics = form.getVolumetricsForm();

    undevelopedDiscoveryInformation.setGasInPlaceP50(volumetrics.getGasInPlace().createBigDecimalOrNull());

    var gasRecoverable = volumetrics.getGasRecoverable();
    undevelopedDiscoveryInformation.setGasRecoverableOneC(gasRecoverable.getOneC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setGasRecoverableTwoC(gasRecoverable.getTwoC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setGasRecoverableThreeC(gasRecoverable.getThreeC().createBigDecimalOrNull());

    undevelopedDiscoveryInformation.setOilInPlaceP50(volumetrics.getOilInPlace().createBigDecimalOrNull());

    var oilRecoverable = volumetrics.getOilRecoverable();
    undevelopedDiscoveryInformation.setOilRecoverableOneC(oilRecoverable.getOneC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setOilRecoverableTwoC(oilRecoverable.getTwoC().createBigDecimalOrNull());
    undevelopedDiscoveryInformation.setOilRecoverableThreeC(oilRecoverable.getThreeC().createBigDecimalOrNull());

    undevelopedDiscoveryInformation.setDifferenceReason(form.getVolumetricsForm().getVolumetricDifferenceReason());

    // complexity
    var providedDiscoveryComplexity = form.getProvideDiscoveryComplexity();
    undevelopedDiscoveryInformation.setProvidedDiscoveryComplexity(providedDiscoveryComplexity);
    if (BooleanUtils.isTrue(providedDiscoveryComplexity)) {
      var complexityForm = form.getComplexityForm();
      undevelopedDiscoveryInformation.setStructuralComplexityRange(complexityForm.getStructuralComplexityRange());
      undevelopedDiscoveryInformation.setArealDensityRange(complexityForm.getArealDensityRange());
      undevelopedDiscoveryInformation.setVerticalHeterogeneityRange(complexityForm.getVerticalHeterogeneityRange());
      undevelopedDiscoveryInformation.setViscosityRange(complexityForm.getViscosityRange());
    }

    informationRepository.save(undevelopedDiscoveryInformation);
  }

  public List<UndevelopedDiscoveryInformation> getOrderedDiscoveryInformation(
      UndevelopedDiscovery undevelopedDiscovery) {
    return informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery)
        .stream()
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
}