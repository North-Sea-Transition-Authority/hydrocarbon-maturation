package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DiscoveryMixedCaseVolumetricsSubmissionService implements ReservoirSectionSubmissionService {

  private final DiscoveryMixedCaseVolumetricsFormService discoveryMixedCaseVolumetricsFormService;
  private final DiscoveryMixedCaseVolumetricsFormValidator discoveryMixedCaseVolumetricsFormValidator;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public DiscoveryMixedCaseVolumetricsSubmissionService(
      DiscoveryMixedCaseVolumetricsFormService discoveryMixedCaseVolumetricsFormService,
      DiscoveryMixedCaseVolumetricsFormValidator discoveryMixedCaseVolumetricsFormValidator,
      GeneralReservoirInformationService generalReservoirInformationService) {
    this.discoveryMixedCaseVolumetricsFormService = discoveryMixedCaseVolumetricsFormService;
    this.discoveryMixedCaseVolumetricsFormValidator = discoveryMixedCaseVolumetricsFormValidator;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      var form = discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail);
      BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
      var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
      return discoveryMixedCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    }

    return true;
  }
}
