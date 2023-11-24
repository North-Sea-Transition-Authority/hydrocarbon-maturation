package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class SourceAndSealFormService {

  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @Autowired
  public SourceAndSealFormService(SourceAndSealPersistenceService sourceAndSealPersistenceService) {
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
  }

  SourceAndSealForm getSourceAndSealForm(SubmissionDetail submissionDetail) {
    return sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)
        .map(this::sourceAndSealEntityToForm)
        .orElse(new SourceAndSealForm());
  }

  private SourceAndSealForm sourceAndSealEntityToForm(SourceAndSeal sourceAndSeal) {
    var form = new SourceAndSealForm();
    if (sourceAndSeal.getSourceFormationName() != null) {
      form.setSourceFormationName(sourceAndSeal.getSourceFormationName().name());
    } else if (sourceAndSeal.getSourceFormationNameManual() != null) {
      form.setSourceFormationName(
          SearchSelectorService.getValueWithManualEntryPrefix(sourceAndSeal.getSourceFormationNameManual())
      );
    }

    form.setSourceAgeStage(sourceAndSeal.getSourceAgeStage());
    form.setSourceGroupName(sourceAndSeal.getSourceGroupName());
    form.setSourceLithology(sourceAndSeal.getSourceLithology());
    form.setSourceLithologyOther(sourceAndSeal.getSourceLithologyOther());

    if (sourceAndSeal.getTopSealFormationName() != null) {
      form.setTopSealFormationName(sourceAndSeal.getTopSealFormationName().name());
    } else if (sourceAndSeal.getTopSealFormationNameManual() != null) {
      form.setTopSealFormationName(
          SearchSelectorService.getValueWithManualEntryPrefix(sourceAndSeal.getTopSealFormationNameManual())
      );
    }

    form.setTopSealAgeStage(sourceAndSeal.getTopSealAgeStage());
    form.setTopSealGroupName(sourceAndSeal.getTopSealGroupName());
    form.setTopSealLithology(sourceAndSeal.getTopSealLithology());
    form.setTopSealLithologyOther(sourceAndSeal.getTopSealLithologyOther());

    if (sourceAndSeal.getSubmissionDetail() != null && sourceAndSeal.getSubmissionDetail().isFieldSubmission()) {
      form.setSealThickness(DecimalInput.createDecimalInput(sourceAndSeal.getSealThickness()));
      form.setTopSealFracturePressure(DecimalInput.createDecimalInput(sourceAndSeal.getTopSealFracturePressure()));
    }

    return form;
  }
}
