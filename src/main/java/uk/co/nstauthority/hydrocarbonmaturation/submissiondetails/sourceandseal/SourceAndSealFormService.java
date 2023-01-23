package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class SourceAndSealFormService implements SubmissionFormService {

  private final SourceAndSealFormValidator sourceAndSealFormValidator;
  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @Autowired
  public SourceAndSealFormService(SourceAndSealFormValidator sourceAndSealFormValidator,
                                  SourceAndSealPersistenceService sourceAndSealPersistenceService) {
    this.sourceAndSealFormValidator = sourceAndSealFormValidator;
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    sourceAndSealFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  SourceAndSealForm getSourceAndSealForm(SubmissionDetail submissionDetail) {
    return sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)
        .map(this::sourceAndSealEntityToForm)
        .orElse(new SourceAndSealForm());
  }

  private SourceAndSealForm sourceAndSealEntityToForm(SourceAndSeal sourceAndSeal) {
    var form = new SourceAndSealForm();
    form.setTopSealFormationName(sourceAndSeal.getTopSealFormationName());
    form.setSourceFormationName(sourceAndSeal.getSourceFormationName());
    form.setSourceAgeStage(sourceAndSeal.getSourceAgeStage());
    form.setTopSealAgeStage(sourceAndSeal.getTopSealAgeStage());
    form.setSourceLithology(sourceAndSeal.getSourceLithology());
    form.setTopSealLithology(sourceAndSeal.getTopSealLithology());

    return form;
  }
}
