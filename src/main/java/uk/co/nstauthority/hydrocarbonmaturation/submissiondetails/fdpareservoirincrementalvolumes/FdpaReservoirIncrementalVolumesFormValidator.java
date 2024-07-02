package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;

@Service
public class FdpaReservoirIncrementalVolumesFormValidator implements ReservoirSubmissionValidator {

  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  public FdpaReservoirIncrementalVolumesFormValidator(
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService) {
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fdpaReservoirIncrementalForm = (FdpaReservoirIncrementalVolumesForm) form;
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(
        List.of(reservoirSubmissionDetail)
    );

    if (Boolean.TRUE.equals(fdpaReservoirIncrementalForm.getChangeToInPlaceVolumes())) {
      validateProbabilityDistributionsPartial(fdpaReservoirIncrementalForm, errors, hydrocarbonsDiscovered);
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fdpaReservoirIncrementalForm = (FdpaReservoirIncrementalVolumesForm) form;
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(
        List.of(reservoirSubmissionDetail)
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "changeToInPlaceVolumes",
        "changeToInPlaceVolumes.required",
        "Select yes if there is a change to the in place volumes"
    );

    if (BooleanUtils.isTrue(fdpaReservoirIncrementalForm.getChangeToInPlaceVolumes())) {
      validateProbabilityDistributionsFull(fdpaReservoirIncrementalForm, errors, hydrocarbonsDiscovered);

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "comment",
          "comment.required",
          "Provide comments on why the volumes are changing"
      );
    }
  }

  private void validateProbabilityDistributionsPartial(FdpaReservoirIncrementalVolumesForm form,
                                                       Errors errors, HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.OIL_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          form.getIncrementalIncreaseOil(),
          errors,
          "incrementalIncreaseOil",
          "Incremental increase to the oil initially in place as a result of this FDPA",
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA
      );
    }

    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.GAS_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          form.getIncrementalIncreaseGas(),
          errors,
          "incrementalIncreaseGas",
          "Incremental increase to the gas initially in place as a result of this FDPA",
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
      );
    }
  }

  private void validateProbabilityDistributionsFull(FdpaReservoirIncrementalVolumesForm form,
                                                    Errors errors, HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.OIL_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          form.getIncrementalIncreaseOil(),
          errors,
          "incrementalIncreaseOil",
          "Incremental increase to the oil initially in place as a result of this FDPA",
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA
      );
    }

    if (hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.GAS_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS)) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          form.getIncrementalIncreaseGas(),
          errors,
          "incrementalIncreaseGas",
          "Incremental increase to the gas initially in place as a result of this FDPA",
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
      );
    }
  }
}
