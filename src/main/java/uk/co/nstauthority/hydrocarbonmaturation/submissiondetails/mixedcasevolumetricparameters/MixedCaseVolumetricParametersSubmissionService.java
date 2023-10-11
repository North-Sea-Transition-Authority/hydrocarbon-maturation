package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class MixedCaseVolumetricParametersSubmissionService implements ReservoirSectionSubmissionService {

  private final MixedCaseVolumetricParametersFormService mixedCaseVolumetricParametersFormService;

  @Autowired
  public MixedCaseVolumetricParametersSubmissionService(
      MixedCaseVolumetricParametersFormService mixedCaseVolumetricParametersFormService) {
    this.mixedCaseVolumetricParametersFormService = mixedCaseVolumetricParametersFormService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    bindingResult = mixedCaseVolumetricParametersFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
