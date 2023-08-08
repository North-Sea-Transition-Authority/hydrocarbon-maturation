package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

public interface ReservoirSubmissionValidator {

  boolean isValid(SubmissionForm form,
                  Errors errors,
                  ReservoirSubmissionDetail reservoirSubmissionDetail,
                  ValidationType validationType);
}