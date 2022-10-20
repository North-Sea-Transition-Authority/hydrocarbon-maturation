package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class SourceAndSealSummaryService {
  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @Autowired
  public SourceAndSealSummaryService(SourceAndSealPersistenceService sourceAndSealPersistenceService) {
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
  }

  public SourceAndSealSummaryView getSourceAndSealSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var sourceAndSeal = sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail);
    return sourceAndSeal.map(this::createSummaryViewFromSourceAndSeal)
        .orElse(SourceAndSealSummaryView.empty());
  }

  private SourceAndSealSummaryView createSummaryViewFromSourceAndSeal(SourceAndSeal sourceAndSeal) {
    var sourceFormationName = ObjectUtils.defaultIfNull(sourceAndSeal.getSourceFormationName(), "").toString();
    var sourceAgeStage = ObjectUtils.defaultIfNull(sourceAndSeal.getSourceAgeStage(), "").toString();
    var sourceLithology = ObjectUtils.defaultIfNull(sourceAndSeal.getSourceLithology(), "").toString();
    var sealFormationName = ObjectUtils.defaultIfNull(sourceAndSeal.getTopSealFormationName(), "").toString();
    var sealAgeStage = ObjectUtils.defaultIfNull(sourceAndSeal.getTopSealAgeStage(), "").toString();
    var sealLithology = ObjectUtils.defaultIfNull(sourceAndSeal.getTopSealLithology(), "").toString();

    return new SourceAndSealSummaryView(
        sourceFormationName,
        sourceAgeStage,
        sourceLithology,
        sealFormationName,
        sealAgeStage,
        sealLithology);
  }
}
