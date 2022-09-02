package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ChanceOfSuccessFormService implements SubmissionFormService {

  private final ChanceOfSuccessFormValidator chanceOfSuccessFormValidator;
  private final ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  @Autowired
  public ChanceOfSuccessFormService(ChanceOfSuccessFormValidator chanceOfSuccessFormValidator,
                                    ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService) {
    this.chanceOfSuccessFormValidator = chanceOfSuccessFormValidator;
    this.chanceOfSuccessPersistenceService = chanceOfSuccessPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    chanceOfSuccessFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  ChanceOfSuccessForm getChanceOfSuccessForm(SubmissionDetail submissionDetail) {
    var newChanceOfSuccessForm = createNewChanceOfSuccessForm();

    return chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail)
        .map(this::chanceOfSuccessEntityToForm)
        .orElse(newChanceOfSuccessForm);
  }

  ChanceOfSuccessForm createNewChanceOfSuccessForm() {
    var newChanceOfSuccessForm = new ChanceOfSuccessForm();

    newChanceOfSuccessForm.setPlayChanceOfSuccess(createChanceOfSuccessDetailsForm());

    var newProspectSpecificChanceOfSuccessForm = new ProspectSpecificChanceOfSuccessForm();
    newProspectSpecificChanceOfSuccessForm.setChanceOfSuccessDetailsForm(createChanceOfSuccessDetailsForm());
    newProspectSpecificChanceOfSuccessForm.setTrapPercentage(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setMigrationPercentage(new DecimalInput());
    newChanceOfSuccessForm.setProspectSpecificChanceOfSuccess(newProspectSpecificChanceOfSuccessForm);

    return newChanceOfSuccessForm;
  }

  ChanceOfSuccessDetailsForm createChanceOfSuccessDetailsForm() {
    var chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(new DecimalInput());
    chanceOfSuccessDetailsForm.setTopSealPercentage(new DecimalInput());
    chanceOfSuccessDetailsForm.setReservoirPercentage(new DecimalInput());
    return chanceOfSuccessDetailsForm;
  }

  private ChanceOfSuccessForm chanceOfSuccessEntityToForm(ChanceOfSuccess chanceOfSuccess) {
    var form = new ChanceOfSuccessForm();

    form.setPlayChanceOfSuccess(getPlayChanceOfSuccessFromEntity(chanceOfSuccess));
    form.setProspectSpecificChanceOfSuccess(getProspectSpecificChanceOfSuccessFromEntity(chanceOfSuccess));

    form.setGeophysicallyModified(chanceOfSuccess.getGeophysicallyModified());
    form.setRelatedToSpecificWellLocation(chanceOfSuccess.getRelatedToSpecificWellLocation());

    if (chanceOfSuccess.getGeophysicallyModified() != null && chanceOfSuccess.getGeophysicallyModified()) {
      form.setGeophysicallyModifiedPercentage(chanceOfSuccess.getGeophysicallyModifiedPercentage() != null
          ? new DecimalInput(chanceOfSuccess.getGeophysicallyModifiedPercentage().toString())
          : new DecimalInput());
      form.setGeophysicallyModifiedComments(chanceOfSuccess.getGeophysicallyModifiedComments());
    }

    if (chanceOfSuccess.getRelatedToSpecificWellLocation() != null && chanceOfSuccess.getRelatedToSpecificWellLocation()) {
      form.setWellModifiedPercentage(chanceOfSuccess.getWellModifiedPercentage() != null
          ? new DecimalInput(chanceOfSuccess.getWellModifiedPercentage().toString())
          : new DecimalInput());
      form.setWellModifiedComments(chanceOfSuccess.getWellModifiedComments());
    }

    return form;
  }

  private ChanceOfSuccessDetailsForm getPlayChanceOfSuccessFromEntity(ChanceOfSuccess chanceOfSuccess) {
    ChanceOfSuccessDetailsForm form = new ChanceOfSuccessDetailsForm();
    form.setSourcePercentage(chanceOfSuccess.getSourcePercentage() != null
        ? new DecimalInput(chanceOfSuccess.getSourcePercentage().toString())
        : new DecimalInput());
    form.setSourceComments(chanceOfSuccess.getSourceComments());
    form.setTopSealPercentage(chanceOfSuccess.getTopSealPercentage() != null
        ? new DecimalInput(chanceOfSuccess.getTopSealPercentage().toString())
        : new DecimalInput()
    );
    form.setTopSealComments(chanceOfSuccess.getTopSealComments());
    form.setReservoirPercentage(chanceOfSuccess.getReservoirPercentage() != null
        ? new DecimalInput(chanceOfSuccess.getReservoirPercentage().toString())
        : new DecimalInput());
    form.setReservoirComments(chanceOfSuccess.getReservoirComments());
    return form;
  }

  private ProspectSpecificChanceOfSuccessForm getProspectSpecificChanceOfSuccessFromEntity(ChanceOfSuccess chanceOfSuccess) {
    ProspectSpecificChanceOfSuccessForm form = new ProspectSpecificChanceOfSuccessForm();

    ChanceOfSuccessDetailsForm chanceOfSuccessDetailsForm = new ChanceOfSuccessDetailsForm();
    chanceOfSuccessDetailsForm.setSourcePercentage(
        chanceOfSuccess.getProspectSourcePercentage() != null
        ? new DecimalInput(chanceOfSuccess.getProspectSourcePercentage().toString())
        : new DecimalInput());
    chanceOfSuccessDetailsForm.setSourceComments(chanceOfSuccess.getProspectSourceComments());
    chanceOfSuccessDetailsForm.setTopSealPercentage(
        chanceOfSuccess.getProspectTopSealPercentage() != null
        ? new DecimalInput(chanceOfSuccess.getProspectTopSealPercentage().toString())
        : new DecimalInput()
    );
    chanceOfSuccessDetailsForm.setTopSealComments(chanceOfSuccess.getProspectTopSealComments());
    chanceOfSuccessDetailsForm.setReservoirPercentage(
        chanceOfSuccess.getProspectReservoirPercentage() != null
        ? new DecimalInput(chanceOfSuccess.getProspectReservoirPercentage().toString())
        : new DecimalInput());
    chanceOfSuccessDetailsForm.setReservoirComments(chanceOfSuccess.getProspectReservoirComments());
    form.setChanceOfSuccessDetailsForm(chanceOfSuccessDetailsForm);

    form.setTrapPercentage(chanceOfSuccess.getProspectTrapPercentage() != null
        ? new DecimalInput(chanceOfSuccess.getProspectTrapPercentage().toString())
        : new DecimalInput());
    form.setTrapComments(chanceOfSuccess.getProspectTrapComments());
    form.setMigrationPercentage(chanceOfSuccess.getProspectMigrationPercentage() != null
        ? new DecimalInput(chanceOfSuccess.getProspectMigrationPercentage().toString())
        : new DecimalInput());
    form.setMigrationComments(chanceOfSuccess.getProspectMigrationComments());
    return form;
  }
}
