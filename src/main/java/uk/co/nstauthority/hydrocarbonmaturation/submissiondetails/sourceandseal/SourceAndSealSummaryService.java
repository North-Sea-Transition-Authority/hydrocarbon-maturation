package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
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

    var sourceGroupName = sourceAndSeal.getSourceGroupName() != null ? sourceAndSeal.getSourceGroupName().getDisplayName() : "";

    var sealGroupName = sourceAndSeal.getTopSealGroupName() != null
        ? sourceAndSeal.getTopSealGroupName().getDisplayName()
        : "";

    return new SourceAndSealSummaryView(
        getFormationName(sourceAndSeal.getSourceFormationName(), sourceAndSeal.getSourceFormationNameManual()),
        getAgeStage(sourceAndSeal.getSourceAgeStage(), sourceAndSeal.getSourceAgeStageManual()),
        sourceGroupName,
        getSourceLithologyDisplay(sourceAndSeal.getSourceLithology(), sourceAndSeal.getSourceLithologyOther()),
        getFormationName(sourceAndSeal.getTopSealFormationName(), sourceAndSeal.getTopSealFormationNameManual()),
        getAgeStage(sourceAndSeal.getTopSealAgeStage(), sourceAndSeal.getTopSealAgeStageManual()),
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

  private String getAgeStage(AgeSeries ageSeries, String manualAgeSeries) {
    if (ageSeries != null) {
      return ageSeries.getDisplayName();
    } else if (manualAgeSeries != null) {
      return manualAgeSeries;
    }
    return "";
  }

  private String getTopSealLithologyDisplay(StandardLithology topSealLithology, String otherLithology) {
    var sealLithology = topSealLithology != null ? topSealLithology.getDisplayName() : "";
    var sealLithologyOther = ObjectUtils.defaultIfNull(otherLithology, "");
    return topSealLithology == StandardLithology.OTHER ? sealLithologyOther : sealLithology;
  }

  private String getSourceLithologyDisplay(StandardLithology sourceLithology, String otherLithology) {
    var sourceLithologyName = sourceLithology != null ? sourceLithology.getDisplayName() : "";
    var sourceLithologyOther = ObjectUtils.defaultIfNull(otherLithology, "");
    return sourceLithology == StandardLithology.OTHER ? sourceLithologyOther : sourceLithologyName;
  }
}
