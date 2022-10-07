package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.submission.ProspectSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class MixedCaseVolumetricsSubmissionService implements ProspectSectionSubmissionService {

  private final MixedCaseVolumetricsFormService mixedCaseVolumetricsFormService;

  @Autowired
  public MixedCaseVolumetricsSubmissionService(MixedCaseVolumetricsFormService mixedCaseVolumetricsFormService) {
    this.mixedCaseVolumetricsFormService = mixedCaseVolumetricsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = mixedCaseVolumetricsFormService.getMixedCaseVolumetricsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = mixedCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
