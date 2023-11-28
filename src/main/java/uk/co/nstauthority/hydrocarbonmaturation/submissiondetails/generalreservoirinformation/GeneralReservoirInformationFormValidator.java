package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;

@Service
public class GeneralReservoirInformationFormValidator implements ReservoirSubmissionValidator {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public GeneralReservoirInformationFormValidator(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reservoirName", "reservoirName.required",
        "Enter a reservoir name");

    validateUniqueReservoirNames((GeneralReservoirInformationForm) form, errors, reservoirSubmissionDetail);

    validateSectionsAreForHydrocarbonSelected((GeneralReservoirInformationForm) form, errors);
  }

  private void validateSectionsAreForHydrocarbonSelected(GeneralReservoirInformationForm form,
                                                         Errors errors) {
    if (Boolean.TRUE.equals(form.getIsReservoirBeingDevelopedUnderFuturePhase())) {
      var hydrocarbonsDiscovered = form.getHydrocarbonsDiscovered();
      var selectedSections = form.getReservoirSubmissionSections();

      if (areOilCaseSelectionsInconsistent(hydrocarbonsDiscovered, selectedSections)
          || areGasCaseSelectionsInconsistent(hydrocarbonsDiscovered, selectedSections)) {
        errors.rejectValue(
            "reservoirSubmissionSections",
            "reservoirSubmissionSections.invalid",
            "The information that can be provided must be consistent with the hydrocarbons discovered."
        );
      }
    }
  }

  private boolean areOilCaseSelectionsInconsistent(HydrocarbonsDiscovered hydrocarbonsDiscovered,
                                                 Set<ReservoirSubmissionSectionType> selectedSections) {
    return HydrocarbonsDiscovered.OIL_ONLY.equals(hydrocarbonsDiscovered)
        && selectedSections.contains(ReservoirSubmissionSectionType.GAS_PROPERTIES);
  }

  private boolean areGasCaseSelectionsInconsistent(HydrocarbonsDiscovered hydrocarbonsDiscovered,
                                                 Set<ReservoirSubmissionSectionType> selectedSections) {
    return HydrocarbonsDiscovered.GAS_ONLY.equals(hydrocarbonsDiscovered)
        && selectedSections.contains(ReservoirSubmissionSectionType.OIL_PROPERTIES);
  }


  @Override
  public void validateFull(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    GeneralReservoirInformationForm generalReservoirInformationForm = (GeneralReservoirInformationForm) form;
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reservoirName", "reservoirName.required",
        "Enter a reservoir name");

    validateUniqueReservoirNames(generalReservoirInformationForm, errors, reservoirSubmissionDetail);

    if (submissionDetail.isFieldSubmission()
        && (CollectionUtils.isEmpty(generalReservoirInformationForm.getAssociatedDiscoveryReservoirs()))) {
      errors.rejectValue("associatedDiscoveryReservoirSelect", "associatedDiscoveryReservoirSelect.notEmpty",
          "You must select at least one associated discovery reservoir");
    }

    ValidationUtils.rejectIfEmpty(errors, "hydrocarbonsDiscovered",
        "hydrocarbonsDiscovered.required", "Select the hydrocarbons discovered");

    if (submissionDetail.isFieldSubmission() && !submissionDetail.isCessationOfProductionSubmission()) {
      ValidationUtils.rejectIfEmpty(errors,
          "isReservoirBeingDevelopedUnderFuturePhase",
          "isReservoirBeingDevelopedUnderFuturePhase.required",
          "Select if this reservoir is being developed under a future phase of work");
    }

    validateSectionsAreForHydrocarbonSelected((GeneralReservoirInformationForm) form, errors);
  }

  private void validateUniqueReservoirNames(GeneralReservoirInformationForm generalReservoirInformationForm,
                                            Errors errors,
                                            ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var reservoirNames = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail)
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
  }
}
