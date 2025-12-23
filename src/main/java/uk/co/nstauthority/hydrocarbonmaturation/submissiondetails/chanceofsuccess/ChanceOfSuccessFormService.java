package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
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
  public BindingResult validate(SubmissionForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                SubmissionDetail submissionDetail,
                                User user) {
    chanceOfSuccessFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(validationType, submissionDetail));
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
    newProspectSpecificChanceOfSuccessForm.setTrapPresence(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setTrapEffectiveness(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setSourcePresence(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setSourceEffectiveness(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setReservoirPresence(new DecimalInput());
    newProspectSpecificChanceOfSuccessForm.setReservoirEffectiveness(new DecimalInput());
    newChanceOfSuccessForm.setProspectSpecificChanceOfSuccess(newProspectSpecificChanceOfSuccessForm);

    return newChanceOfSuccessForm;
  }

  PlayChanceOfSuccessDetailsForm createChanceOfSuccessDetailsForm() {
    var chanceOfSuccessDetailsForm = new PlayChanceOfSuccessDetailsForm();
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

  private PlayChanceOfSuccessDetailsForm getPlayChanceOfSuccessFromEntity(ChanceOfSuccess chanceOfSuccess) {
    PlayChanceOfSuccessDetailsForm form = new PlayChanceOfSuccessDetailsForm();
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

    form.setTrapPresence(chanceOfSuccess.getProspectTrapPresence() != null
        ? new DecimalInput(chanceOfSuccess.getProspectTrapPresence().toString())
        : new DecimalInput());
    form.setTrapPresenceComments(chanceOfSuccess.getProspectTrapPresenceComments());
    form.setTrapEffectiveness(chanceOfSuccess.getProspectTrapEffectiveness() != null
        ? new DecimalInput(chanceOfSuccess.getProspectTrapEffectiveness().toString())
        : new DecimalInput());
    form.setTrapEffectivenessComments(chanceOfSuccess.getProspectTrapEffectivenessComments());
    form.setSourcePresence(
        chanceOfSuccess.getProspectSourcePresence() != null
        ? new DecimalInput(chanceOfSuccess.getProspectSourcePresence().toString())
        : new DecimalInput());
    form.setSourcePresenceComments(chanceOfSuccess.getProspectSourcePresenceComments());
    form.setSourceEffectiveness(
        chanceOfSuccess.getProspectSourceEffectiveness() != null
            ? new DecimalInput(chanceOfSuccess.getProspectSourceEffectiveness().toString())
            : new DecimalInput());
    form.setSourceEffectivenessComments(chanceOfSuccess.getProspectSourceEffectivenessComments());
    form.setReservoirPresence(
        chanceOfSuccess.getProspectReservoirPresence() != null
        ? new DecimalInput(chanceOfSuccess.getProspectReservoirPresence().toString())
        : new DecimalInput());
    form.setReservoirPresenceComments(chanceOfSuccess.getProspectReservoirPresenceComments());
    form.setReservoirEffectiveness(
        chanceOfSuccess.getProspectReservoirEffectiveness() != null
            ? new DecimalInput(chanceOfSuccess.getProspectReservoirEffectiveness().toString())
            : new DecimalInput());
    form.setReservoirEffectivenessComments(chanceOfSuccess.getProspectReservoirEffectivenessComments());
    return form;
  }
}
