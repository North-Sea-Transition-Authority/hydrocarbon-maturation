package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OutlineForMaximumCaseVerifyFormService implements SubmissionFormService {
  private final OutlineForMaximumCaseVerifyFormValidator outlineForMaximumCaseVerifyFormValidator;
  private final OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;

  @Autowired
  public OutlineForMaximumCaseVerifyFormService(
      OutlineForMaximumCaseVerifyFormValidator outlineForMaximumCaseVerifyFormValidator,
      OutlineForMaximumCaseRepository outlineForMaximumCaseRepository) {
    this.outlineForMaximumCaseVerifyFormValidator = outlineForMaximumCaseVerifyFormValidator;
    this.outlineForMaximumCaseRepository = outlineForMaximumCaseRepository;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType,
                                SubmissionDetail submissionDetail) {
    outlineForMaximumCaseVerifyFormValidator
        .validate(form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  OutlineForMaximumCaseVerifyForm getOutlineForMaximumCaseVerifyForm(SubmissionDetail submissionDetail) {
    return outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail)
        .map(outlineForMaximumCase -> {
          var form = new OutlineForMaximumCaseVerifyForm();
          form.setShapeIsCorrect(outlineForMaximumCase.getHasVerifiedShapefile());
          return form;
        })
        .orElseGet(OutlineForMaximumCaseVerifyForm::new);
  }

  boolean getShapeIsCorrect(OutlineForMaximumCaseVerifyForm form) {
    return BooleanUtils.isTrue(form.getShapeIsCorrect());
  }
}
