package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

class ChanceOfSuccessTestUtil {

  static ChanceOfSuccess createChanceOfSuccess(SubmissionDetail submissionDetail) {
    var chanceOfSuccess = new ChanceOfSuccess();
    chanceOfSuccess.setSubmissionDetail(submissionDetail);
    chanceOfSuccess.setSourcePercentage(BigDecimal.valueOf(1));
    chanceOfSuccess.setSourceComments("source comments");
    chanceOfSuccess.setReservoirPercentage(BigDecimal.valueOf(2));
    chanceOfSuccess.setReservoirComments("reservoir comments");
    chanceOfSuccess.setTopSealPercentage(BigDecimal.valueOf(3));
    chanceOfSuccess.setTopSealComments("top seal comments");
    chanceOfSuccess.setProspectTrapPresence(BigDecimal.valueOf(4));
    chanceOfSuccess.setProspectTrapPresenceComments("prospect trap comments");
    chanceOfSuccess.setProspectTrapEffectiveness(BigDecimal.valueOf(11));
    chanceOfSuccess.setProspectTrapEffectivenessComments("prospect trap effectiveness comments");
    chanceOfSuccess.setProspectSourcePresence(BigDecimal.valueOf(6));
    chanceOfSuccess.setProspectSourcePresenceComments("prospect source comments");
    chanceOfSuccess.setProspectSourceEffectiveness(BigDecimal.valueOf(5));
    chanceOfSuccess.setProspectSourceEffectivenessComments("prospect source effectiveness comments");
    chanceOfSuccess.setProspectReservoirPresence(BigDecimal.valueOf(7));
    chanceOfSuccess.setProspectReservoirPresenceComments("prospect reservoir comments");
    chanceOfSuccess.setProspectReservoirEffectiveness(BigDecimal.valueOf(8));
    chanceOfSuccess.setProspectReservoirEffectivenessComments("prospect reservoir effectiveness comments");
    chanceOfSuccess.setGeophysicallyModified(true);
    chanceOfSuccess.setGeophysicallyModifiedPercentage(BigDecimal.valueOf(9));
    chanceOfSuccess.setGeophysicallyModifiedComments("geophysically modified comments");
    chanceOfSuccess.setRelatedToSpecificWellLocation(true);
    chanceOfSuccess.setWellModifiedPercentage(BigDecimal.valueOf(10));
    chanceOfSuccess.setWellModifiedComments("well modified comments");
    return chanceOfSuccess;
  }

  static ChanceOfSuccessForm createChanceOfSuccessForm() {
    var form = new ChanceOfSuccessForm();
    var chanceOfSuccessDetailsForm = new PlayChanceOfSuccessDetailsForm();
    var prospectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();

    chanceOfSuccessDetailsForm.setSourcePercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(1)));
    chanceOfSuccessDetailsForm.setSourceComments("source comments");
    chanceOfSuccessDetailsForm.setReservoirPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(2)));
    chanceOfSuccessDetailsForm.setReservoirComments("reservoir comments");
    chanceOfSuccessDetailsForm.setTopSealPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(3)));
    chanceOfSuccessDetailsForm.setTopSealComments("top seal comments");

    var prospectSpecificChanceOfSuccessDetails = new PlayChanceOfSuccessDetailsForm();
    prospectSpecificChanceOfSuccessDetails.setSourcePercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(4)));
    prospectSpecificChanceOfSuccessDetails.setSourceComments("prospect source comments");
    prospectSpecificChanceOfSuccessDetails.setReservoirPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(5)));
    prospectSpecificChanceOfSuccessDetails.setReservoirComments("prospect reservoir comments");
    prospectSpecificChanceOfSuccessDetails.setTopSealPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(6)));
    prospectSpecificChanceOfSuccessDetails.setTopSealComments("top seal comments");

    prospectSpecificChanceOfSuccessForm.setTrapPresence(DecimalInput.createDecimalInput(BigDecimal.valueOf(7)));
    prospectSpecificChanceOfSuccessForm.setTrapPresenceComments("prospect trap comments");
    prospectSpecificChanceOfSuccessForm.setTrapEffectiveness(DecimalInput.createDecimalInput(BigDecimal.valueOf(8)));
    prospectSpecificChanceOfSuccessForm.setTrapEffectivenessComments("prospect trap effectiveness comments");
    prospectSpecificChanceOfSuccessForm.setSourcePresence(DecimalInput.createDecimalInput(BigDecimal.valueOf(8)));
    prospectSpecificChanceOfSuccessForm.setSourcePresenceComments("prospect source comments");
    prospectSpecificChanceOfSuccessForm.setSourceEffectiveness(DecimalInput.createDecimalInput(BigDecimal.valueOf(8)));
    prospectSpecificChanceOfSuccessForm.setSourceEffectivenessComments("prospect source effectiveness comments");
    prospectSpecificChanceOfSuccessForm.setReservoirPresence(DecimalInput.createDecimalInput(BigDecimal.valueOf(8)));
    prospectSpecificChanceOfSuccessForm.setReservoirPresenceComments("prospect reservoir comments");
    prospectSpecificChanceOfSuccessForm.setReservoirEffectiveness(DecimalInput.createDecimalInput(BigDecimal.valueOf(8)));
    prospectSpecificChanceOfSuccessForm.setReservoirEffectivenessComments("prospect reservoir effectiveness comments");

    form.setPlayChanceOfSuccess(chanceOfSuccessDetailsForm);
    form.setProspectSpecificChanceOfSuccess(prospectSpecificChanceOfSuccessForm);
    form.setGeophysicallyModified(true);
    form.setGeophysicallyModifiedPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(9)));
    form.setGeophysicallyModifiedComments("geophysically modified comments");
    form.setRelatedToSpecificWellLocation(true);
    form.setWellModifiedPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(10)));
    form.setWellModifiedComments("well modified comments");

    return form;
  }

  static ChanceOfSuccessForm createEmptyChanceOfSuccessForm() {
    var newChanceOfSuccessForm = new ChanceOfSuccessForm();

    newChanceOfSuccessForm.setPlayChanceOfSuccess(createChanceOfSuccessDetailsForm());

    var newProspectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();
    newProspectSpecificChanceOfSuccessForm.setTrapPresence(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setTrapEffectiveness(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setSourcePresence(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setSourceEffectiveness(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setReservoirPresence(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setReservoirEffectiveness(new DecimalInput());
    newChanceOfSuccessForm.setProspectSpecificChanceOfSuccess(newProspectSpecificChanceOfSuccessForm);

    return newChanceOfSuccessForm;
  }

  static PlayChanceOfSuccessDetailsForm createChanceOfSuccessDetailsForm() {
    var chanceOfSuccessDetailsForm = new PlayChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(new DecimalInput());
    chanceOfSuccessDetailsForm.setTopSealPercentage(new DecimalInput());
    chanceOfSuccessDetailsForm.setReservoirPercentage(new DecimalInput());
    return chanceOfSuccessDetailsForm;
  }
}
