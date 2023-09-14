package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.ArrayList;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class ReservoirListValidator implements SubmissionValidator {

  @Override
  public boolean isValid(SubmissionForm form,
                         Errors errors,
                         SubmissionDetail submissionDetail,
                         ValidationType validationType) {
    ReservoirListForm reservoirListForm = (ReservoirListForm) form;

    if (reservoirListForm.getReservoirs().stream().allMatch(Objects::isNull)) {
      errors.rejectValue("reservoirs[0]", "reservoirs.required",
          "You must provide at least one reservoir");
    }

    var reservoirList = reservoirListForm.getReservoirs();
    // checks for uniqueness in the list of reservoir names
    for (int i = 0; i < reservoirList.size(); i++) {
      var listCopy = new ArrayList<>(reservoirList);
      listCopy.remove(i);
      if (Objects.nonNull(reservoirList.get(i)) && listCopy.contains(reservoirList.get(i))) {
        errors.rejectValue(
            String.format("reservoirs[%s]", i),
            String.format("reservoirs[%s].invalid", i),
            String.format("All reservoir names must be unique for this %s",
                submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase()));
      }
    }

    return !errors.hasErrors();
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    // not used here
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    // not used here
  }
}
