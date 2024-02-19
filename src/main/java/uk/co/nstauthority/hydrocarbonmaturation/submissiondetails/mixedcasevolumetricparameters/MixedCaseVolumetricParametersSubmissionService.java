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
  private final MixedCaseVolumetricParametersFormValidator mixedCaseVolumetricParametersFormValidator;

  @Autowired
  public MixedCaseVolumetricParametersSubmissionService(
      MixedCaseVolumetricParametersFormService mixedCaseVolumetricParametersFormService,
      MixedCaseVolumetricParametersFormValidator mixedCaseVolumetricParametersFormValidator) {
    this.mixedCaseVolumetricParametersFormService = mixedCaseVolumetricParametersFormService;
    this.mixedCaseVolumetricParametersFormValidator = mixedCaseVolumetricParametersFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (!submissionDetail.isProspectSubmission()) {
      return true;
    }
    var form = mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return mixedCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
