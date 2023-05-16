package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OutlineForMaximumCaseSubmissionService implements SectionSubmissionService {

  private final OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService;

  @Autowired
  public OutlineForMaximumCaseSubmissionService(OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService) {
    this.outlineForMaximumCaseFormService = outlineForMaximumCaseFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = outlineForMaximumCaseFormService.getOutlineForMaximumCaseForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = outlineForMaximumCaseFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    return !bindingResult.hasErrors();
  }
}
