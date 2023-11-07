package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;

@Service
public class FieldReservoirVolumetricsFormValidator implements ReservoirSubmissionValidator {
  @Override
  public void validatePartial(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fieldVolumetricsForm = (FieldReservoirVolumetricsForm) form;

    var oilInPlace = fieldVolumetricsForm.getOilInPlace();
    ProbabilityDistributionValidationUtil.validateCustomPartial(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fieldVolumetricsForm = (FieldReservoirVolumetricsForm) form;

    var oilInPlace = fieldVolumetricsForm.getOilInPlace();

    ProbabilityDistributionValidationUtil.validateCustomFull(
        oilInPlace,
        errors,
        "oilInPlace",
        "Oil in place",
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
  }
}
