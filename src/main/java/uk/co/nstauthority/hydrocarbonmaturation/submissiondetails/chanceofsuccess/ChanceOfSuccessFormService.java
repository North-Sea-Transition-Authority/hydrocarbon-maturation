package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.util.Optional;
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
    var newChanceOfSuccessForm = new ChanceOfSuccessForm();
    newChanceOfSuccessForm.setPlayChanceOfSuccess(new ChanceOfSuccessDetails());
    newChanceOfSuccessForm.setProspectSpecificChanceOfSuccess(new ProspectSpecificChanceOfSuccess());

    return chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail)
        .map(this::chanceOfSuccessEntityToForm)
        .orElse(newChanceOfSuccessForm);
  }

  private ChanceOfSuccessForm chanceOfSuccessEntityToForm(ChanceOfSuccess chanceOfSuccess) {
    var form = new ChanceOfSuccessForm();

    form.setPlayChanceOfSuccess(getPlayChanceOfSuccessFromEntity(chanceOfSuccess));
    form.setProspectSpecificChanceOfSuccess(getProspectSpecificChanceOfSuccessFromEntity(chanceOfSuccess));

    form.setGeophysicallyModified(chanceOfSuccess.getGeophysicallyModified());
    form.setRelatedToSpecificWellLocation(chanceOfSuccess.getRelatedToSpecificWellLocation());

    if (chanceOfSuccess.getGeophysicallyModified() != null && chanceOfSuccess.getGeophysicallyModified()) {
      form.setGeophysicallyModifiedPercentage(new DecimalInput(chanceOfSuccess.getGeophysicallyModifiedPercentage()));
      form.setGeophysicallyModifiedComments(chanceOfSuccess.getGeophysicallyModifiedComments());
    }

    if (chanceOfSuccess.getRelatedToSpecificWellLocation() != null && chanceOfSuccess.getRelatedToSpecificWellLocation()) {
      form.setWellModifiedPercentage(new DecimalInput(chanceOfSuccess.getWellModifiedPercentage()));
      form.setWellModifiedComments(chanceOfSuccess.getWellModifiedComments());
    }

    return form;
  }

  private ChanceOfSuccessDetails getPlayChanceOfSuccessFromEntity(ChanceOfSuccess chanceOfSuccess) {
    return Optional.ofNullable(chanceOfSuccess.getPlayChanceOfSuccess())
        .orElse(new ChanceOfSuccessDetails());
  }

  private ProspectSpecificChanceOfSuccess getProspectSpecificChanceOfSuccessFromEntity(ChanceOfSuccess chanceOfSuccess) {
    return Optional.ofNullable(chanceOfSuccess.getProspectSpecificChanceOfSuccess())
        .orElse(new ProspectSpecificChanceOfSuccess());
  }
}
