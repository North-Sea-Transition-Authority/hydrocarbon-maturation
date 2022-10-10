package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.numberinput.DecimalInput;
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
      chanceOfSuccess.setProspectTrapPercentage(BigDecimal.valueOf(4));
      chanceOfSuccess.setProspectTrapComments("prospect trap comments");
      chanceOfSuccess.setProspectMigrationPercentage(BigDecimal.valueOf(5));
      chanceOfSuccess.setProspectMigrationComments("prospect migration comments");
      chanceOfSuccess.setProspectSourcePercentage(BigDecimal.valueOf(6));
      chanceOfSuccess.setProspectSourceComments("prospect source comments");
      chanceOfSuccess.setProspectReservoirPercentage(BigDecimal.valueOf(7));
      chanceOfSuccess.setProspectReservoirComments("prospect reservoir comments");
      chanceOfSuccess.setProspectTopSealPercentage(BigDecimal.valueOf(8));
      chanceOfSuccess.setProspectTopSealComments("prospect top seal comments");
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
    var chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    var prospectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();

    chanceOfSuccessDetailsForm.setSourcePercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(1)));
    chanceOfSuccessDetailsForm.setSourceComments("source comments");
    chanceOfSuccessDetailsForm.setReservoirPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(2)));
    chanceOfSuccessDetailsForm.setReservoirComments("reservoir comments");
    chanceOfSuccessDetailsForm.setTopSealPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(3)));
    chanceOfSuccessDetailsForm.setTopSealComments("top seal comments");

    var prospectSpecificChanceOfSuccessDetails = new ChanceOfSuccessDetailsForm();
    prospectSpecificChanceOfSuccessDetails.setSourcePercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(4)));
    prospectSpecificChanceOfSuccessDetails.setSourceComments("prospect source comments");
    prospectSpecificChanceOfSuccessDetails.setReservoirPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(5)));
    prospectSpecificChanceOfSuccessDetails.setReservoirComments("prospect reservoir comments");
    prospectSpecificChanceOfSuccessDetails.setTopSealPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(6)));
    prospectSpecificChanceOfSuccessDetails.setTopSealComments("top seal comments");
    prospectSpecificChanceOfSuccessForm.setChanceOfSuccessDetailsForm(prospectSpecificChanceOfSuccessDetails);

    prospectSpecificChanceOfSuccessForm.setTrapPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(7)));
    prospectSpecificChanceOfSuccessForm.setTrapComments("prospect trap comments");
    prospectSpecificChanceOfSuccessForm.setMigrationPercentage(DecimalInput.createDecimalInput(BigDecimal.valueOf(8)));
    prospectSpecificChanceOfSuccessForm.setMigrationComments("prospect migration comments");

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
    newProspectSpecificChanceOfSuccessForm.setChanceOfSuccessDetailsForm(createChanceOfSuccessDetailsForm());
    newProspectSpecificChanceOfSuccessForm.setTrapPercentage(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setMigrationPercentage(new DecimalInput());
    newChanceOfSuccessForm.setProspectSpecificChanceOfSuccess(newProspectSpecificChanceOfSuccessForm);

    return newChanceOfSuccessForm;
  }

  static ChanceOfSuccessDetailsForm createChanceOfSuccessDetailsForm() {
    var chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(new DecimalInput());
    chanceOfSuccessDetailsForm.setTopSealPercentage(new DecimalInput());
    chanceOfSuccessDetailsForm.setReservoirPercentage(new DecimalInput());
    return chanceOfSuccessDetailsForm;
  }
}
