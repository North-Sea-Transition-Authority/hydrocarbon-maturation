package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OutlineForMaximumCaseSubmissionService implements SectionSubmissionService {

  private final OutlineForMaximumCaseUploadFormService outlineForMaximumCaseUploadFormService;
  private final OutlineForMaximumCaseVerifyFormService outlineForMaximumCaseVerifyFormService;

  @Autowired
  public OutlineForMaximumCaseSubmissionService(OutlineForMaximumCaseUploadFormService outlineForMaximumCaseUploadFormService,
                                                OutlineForMaximumCaseVerifyFormService outlineForMaximumCaseVerifyFormService) {
    this.outlineForMaximumCaseUploadFormService = outlineForMaximumCaseUploadFormService;
    this.outlineForMaximumCaseVerifyFormService = outlineForMaximumCaseVerifyFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    return uploadFormDoesNotHaveErrors(submissionDetail, user) && verifyFormDoesNotHaveErrors(submissionDetail, user);
  }

  private boolean uploadFormDoesNotHaveErrors(SubmissionDetail submissionDetail, User user) {
    var uploadForm = outlineForMaximumCaseUploadFormService.getOutlineForMaximumCaseUploadForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(uploadForm, "form");
    bindingResult = outlineForMaximumCaseUploadFormService
        .validate(uploadForm, bindingResult, ValidationType.FULL, submissionDetail, user);

    return !bindingResult.hasErrors();
  }

  private boolean verifyFormDoesNotHaveErrors(SubmissionDetail submissionDetail, User user) {
    var verifyForm = outlineForMaximumCaseVerifyFormService.getOutlineForMaximumCaseVerifyForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(verifyForm, "form");
    bindingResult = outlineForMaximumCaseVerifyFormService
        .validate(verifyForm, bindingResult, ValidationType.FULL, submissionDetail, user);

    return !bindingResult.hasErrors();
  }
}
