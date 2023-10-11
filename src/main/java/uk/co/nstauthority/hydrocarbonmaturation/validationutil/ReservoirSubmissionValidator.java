package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

public interface ReservoirSubmissionValidator {

  default boolean isValid(SubmissionForm form,
                         Errors errors,
                         ReservoirSubmissionDetail reservoirSubmissionDetail,
                         ValidationType validationType) {


    switch (validationType) {
      case FULL -> validateFull(form, errors, reservoirSubmissionDetail);
      case PARTIAL -> validatePartial(form, errors, reservoirSubmissionDetail);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }

    return !errors.hasErrors();
  }

  void validatePartial(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail);

  void validateFull(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail);
}