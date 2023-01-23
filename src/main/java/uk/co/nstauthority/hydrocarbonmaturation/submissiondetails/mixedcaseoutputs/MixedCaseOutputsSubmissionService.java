package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class MixedCaseOutputsSubmissionService implements SectionSubmissionService {

  private final MixedCaseOutputsFormService mixedCaseOutputsFormService;

  @Autowired
  public MixedCaseOutputsSubmissionService(MixedCaseOutputsFormService mixedCaseOutputsFormService) {
    this.mixedCaseOutputsFormService = mixedCaseOutputsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = mixedCaseOutputsFormService.getMixedCaseOutputsForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = mixedCaseOutputsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
