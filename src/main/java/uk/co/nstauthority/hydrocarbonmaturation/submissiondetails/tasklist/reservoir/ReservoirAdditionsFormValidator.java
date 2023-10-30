package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class ReservoirAdditionsFormValidator implements SubmissionValidator {
  private final List<ReservoirSectionSubmissionService> reservoirSectionSubmissionServices;

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public ReservoirAdditionsFormValidator(
      List<ReservoirSectionSubmissionService> reservoirSectionSubmissionServices,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.reservoirSectionSubmissionServices = reservoirSectionSubmissionServices;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    // any submission
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var reservoirAdditionForm = (ReservoirAdditionForm) form;

    if (reservoirAdditionForm.getReservoirAdditionStatus() == null) {
      // no interaction with form
      ValidationUtils.rejectIfEmpty(
          errors,
          "reservoirAdditionStatus",
          "reservoirAdditionStatus.required",
          "Select if you have any more reservoirs to add"
      );
    } else if (!reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)) {
      // at least one reservoir
      errors.reject(
          "reservoir.required",
          "You must provide at least one reservoir"
      );
    } else if (!areAllReservoirsSubmittable(submissionDetail)) {
      // all sections completed
      errors.reject(
          "reservoirs.notSubmittable",
          "All sections for each reservoir must be completed"
      );
    }
  }

  private boolean areAllReservoirsSubmittable(SubmissionDetail submissionDetail) {
    // get all reservoir submission details
    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);

    // are they all submittable
    return reservoirSubmissionDetails.stream()
        .allMatch(this::areAllSectionsSubmittableForReservoirSubmissionDetail);
  }

  private boolean areAllSectionsSubmittableForReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirSectionSubmissionServices.stream()
        .allMatch(service -> service.isSectionSubmittable(reservoirSubmissionDetail));
  }
}
