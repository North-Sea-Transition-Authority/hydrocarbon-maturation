package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class RelatedProspectsFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return RelatedProspectsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    //no fields require partial validation
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var relatedProspectsForm = (RelatedProspectsForm) form;

    ValidationUtils.rejectIfEmpty(errors, "relatedToAnotherProspect", "relatedToAnotherProspect.required",
        "Select whether this prospect is a different view of another prospect in the service");

    if (BooleanUtils.isTrue(relatedProspectsForm.getRelatedToAnotherProspect())) {
      ValidationUtils.rejectIfEmpty(errors, "submissionDetailIdOfRelatedProspect", "submissionDetailIdOfRelatedProspect.required",
          "Select a prospect or prospect group");

      ValidationUtils.rejectIfEmpty(errors, "preferredView", "preferredView.required",
          "Select whether this is the NSTA preferred view of the prospect?");
    }
  }
}
