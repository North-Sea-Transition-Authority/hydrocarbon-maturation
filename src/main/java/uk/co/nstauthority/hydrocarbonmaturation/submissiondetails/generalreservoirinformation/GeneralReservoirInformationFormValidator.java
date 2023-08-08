package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralReservoirInformationFormValidator implements ReservoirSubmissionValidator {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public GeneralReservoirInformationFormValidator(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @Override
  public boolean isValid(SubmissionForm form,
                         Errors errors,
                         ReservoirSubmissionDetail reservoirSubmissionDetail,
                         ValidationType validationType) {
    GeneralReservoirInformationForm generalReservoirInformationForm = (GeneralReservoirInformationForm) form;
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reservoirName", "reservoirName.required",
        "Enter a reservoir name");

    var reservoirNames = reservoirSubmissionDetailPersistenceService
        .getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail)
        .stream()
        .map(reservoirSubmissionDetailLambda -> reservoirSubmissionDetailLambda.getReservoir().getName())
        // Filter out the current reservoir name
        .filter(reservoirName -> !reservoirName.equals(reservoirSubmissionDetail.getReservoir().getName()))
        .toList();

    if (reservoirNames.contains(generalReservoirInformationForm.getReservoirName())) {
      errors.rejectValue("reservoirName", "reservoir.name",
          String.format("All reservoir names must be unique for this %s",
              submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase()));
    }

    if (validationType == ValidationType.FULL) {
      ValidationUtils.rejectIfEmpty(errors, "hydrocarbonsDiscovered",
          "hydrocarbonsDiscovered.required", "Select the hydrocarbons discovered");
    }

    return !errors.hasErrors();
  }
}
