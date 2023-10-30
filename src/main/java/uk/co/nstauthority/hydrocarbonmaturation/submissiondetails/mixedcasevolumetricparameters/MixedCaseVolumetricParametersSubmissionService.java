package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class MixedCaseVolumetricParametersSubmissionService implements ReservoirSectionSubmissionService {

  private final MixedCaseVolumetricParametersFormService mixedCaseVolumetricParametersFormService;

  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public MixedCaseVolumetricParametersSubmissionService(
      MixedCaseVolumetricParametersFormService mixedCaseVolumetricParametersFormService,
      GeneralReservoirInformationService generalReservoirInformationService) {
    this.mixedCaseVolumetricParametersFormService = mixedCaseVolumetricParametersFormService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      return true;
    }
    var form = mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = mixedCaseVolumetricParametersFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
