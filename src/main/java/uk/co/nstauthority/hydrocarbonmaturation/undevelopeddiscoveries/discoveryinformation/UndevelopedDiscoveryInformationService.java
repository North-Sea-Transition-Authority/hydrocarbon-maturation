package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  public List<UndevelopedDiscoveryInformation> getOrderedDiscoveryInformation(UndevelopedDiscovery undevelopedDiscovery) {
    return informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery)
        .stream()
        .sorted(Comparator.comparing(UndevelopedDiscoveryInformation::getInformationDate).reversed())
        .toList();
  }

  private LocalDate getDateFromForm(UndevelopedDiscoveryInformationForm form) {
    return LocalDate.of(
        Integer.parseInt(form.getDiscoveryInformationDateYear()),
        Integer.parseInt(form.getDiscoveryInformationDateMonth()),
        Integer.parseInt(form.getDiscoveryInformationDateDay())
    );
  }
}