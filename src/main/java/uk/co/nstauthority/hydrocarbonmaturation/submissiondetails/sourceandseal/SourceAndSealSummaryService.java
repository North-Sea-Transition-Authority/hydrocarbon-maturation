package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;

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

    var sourceAgeStage = sourceAndSeal.getSourceAgeStage() != null ? sourceAndSeal.getSourceAgeStage().getDisplayName() : "";
    var sourceGroupName = sourceAndSeal.getSourceGroupName() != null ? sourceAndSeal.getSourceGroupName().getDisplayName() : "";
    var sourceLithology = ObjectUtils.defaultIfNull(sourceAndSeal.getSourceLithology(), "");

    var sealAgeStage = sourceAndSeal.getTopSealAgeStage() != null ? sourceAndSeal.getTopSealAgeStage().getDisplayName() : "";
    var sealGroupName = sourceAndSeal.getTopSealGroupName() != null
        ? sourceAndSeal.getTopSealGroupName().getDisplayName()
        : "";

    return new SourceAndSealSummaryView(
        getFormationName(sourceAndSeal.getSourceFormationName(), sourceAndSeal.getSourceFormationNameManual()),
        sourceAgeStage,
        sourceGroupName,
        sourceLithology,
        getFormationName(sourceAndSeal.getTopSealFormationName(), sourceAndSeal.getTopSealFormationNameManual()),
        sealAgeStage,
        sealGroupName,
        getTopSealLithologyDisplay(sourceAndSeal.getTopSealLithology(), sourceAndSeal.getTopSealLithologyOther())
    );
  }

  private String getFormationName(Formation formation, String manualFormation) {
    String formationName = "";
    if (formation != null) {
      formationName = formation.getDisplayName();
    } else if (manualFormation != null) {
      formationName = manualFormation;
    }
    return formationName;
  }

  private String getTopSealLithologyDisplay(TopSealLithology topSealLithology, String otherLithology) {
    var sealLithology = topSealLithology != null ? topSealLithology.getDisplayName() : "";
    var sealLithologyOther = ObjectUtils.defaultIfNull(otherLithology, "");
    return topSealLithology == TopSealLithology.OTHER ? sealLithologyOther : sealLithology;
  }
}
