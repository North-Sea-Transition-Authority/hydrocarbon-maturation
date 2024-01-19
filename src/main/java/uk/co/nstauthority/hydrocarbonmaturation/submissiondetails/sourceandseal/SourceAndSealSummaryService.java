package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.util.ArrayList;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class SourceAndSealSummaryService {
  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;

  public static final String SOURCE_AND_SEAL = "Source and seal";

  public static final String SOURCE_AGE_STAGE = "Source age (Stage or Series)";
  public static final String SOURCE_FORMATION_NAME = "Source formation";
  public static final String SOURCE_GROUP_NAME = "Source group name";
  public static final String SOURCE_LITHOLOGY = "Source lithology";
  public static final String TOP_SEAL_FORMATION_NAME = "Top seal formation name";
  public static final String TOP_SEAL_AGE_STAGE = "Top seal age (Stage or Series)";
  public static final String TOP_SEAL_GROUP_NAME = "Top seal group name";
  public static final String TOP_SEAL_LITHOLOGY = "Top seal lithology";
  public static final String SEAL_THICKNESS = "Seal thickness";
  public static final String TOP_SEAL_FRACTURE_PRESSURE = "Top seal fracture pressure";

  public SourceAndSealSummaryService(SourceAndSealPersistenceService sourceAndSealPersistenceService) {
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
  }

  public SourceAndSealSummaryView getSourceAndSealSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var sourceAndSeal = sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail);
    return sourceAndSeal.map(this::createSummaryViewFromSourceAndSeal)
        .orElse(SourceAndSealSummaryView.empty());
  }

  public SummaryCard getFieldSourceAndSealSummaryCard(SubmissionDetail submissionDetail) {
    return sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)
        .map(
            sourceAndSeal -> {
              var summaryDataView = new SummaryDataView(new ArrayList<>());
              // Source fields
              addSourceProperties(sourceAndSeal, summaryDataView);

              // Top seal fields
              addTopSealProperties(sourceAndSeal, summaryDataView);

              return SummaryCard.simpleSummaryCardWithHeading(SOURCE_AND_SEAL, summaryDataView);

            }).orElse(SummaryCard.emptySummaryCardWithHeading(SOURCE_AND_SEAL));
  }

  private static void addTopSealProperties(SourceAndSeal sourceAndSeal, SummaryDataView summaryDataView) {
    String topSealAgeStage = "";
    if (sourceAndSeal.getTopSealAgeStage() != null) {
      topSealAgeStage = sourceAndSeal.getTopSealAgeStage().getDisplayName();
    } else if (sourceAndSeal.getTopSealAgeStageManual() != null) {
      topSealAgeStage = sourceAndSeal.getTopSealAgeStageManual();
    }
    summaryDataView.addKeyValue(TOP_SEAL_AGE_STAGE, topSealAgeStage);

    summaryDataView.addKeyValue(TOP_SEAL_GROUP_NAME,
        sourceAndSeal.getTopSealGroupName() == null
            ? ""
            : sourceAndSeal.getTopSealGroupName().getDisplayName());

    String topSealFormationName = "";
    if (sourceAndSeal.getTopSealFormationName() != null) {
      topSealFormationName = sourceAndSeal.getTopSealFormationName().getDisplayName();
    } else if (sourceAndSeal.getTopSealFormationNameManual() != null) {
      topSealFormationName = sourceAndSeal.getTopSealFormationNameManual();
    }
    summaryDataView.addKeyValue(TOP_SEAL_FORMATION_NAME, topSealFormationName);

    String topSealLithology = "";
    if (StandardLithology.OTHER.equals(sourceAndSeal.getTopSealLithology())) {
      topSealLithology = sourceAndSeal.getTopSealLithologyOther();
    } else if (sourceAndSeal.getTopSealLithology() != null) {
      topSealLithology = sourceAndSeal.getTopSealLithology().getDisplayName();
    }
    summaryDataView.addKeyValue(TOP_SEAL_LITHOLOGY, topSealLithology);

    summaryDataView.addKeyValue(SEAL_THICKNESS,
        NumberUtil.getNumberStringWithUnitsOrEmpty(sourceAndSeal.getSealThickness(), "m"));
    summaryDataView.addKeyValue(TOP_SEAL_FRACTURE_PRESSURE,
        NumberUtil.getNumberStringWithUnitsOrEmpty(sourceAndSeal.getTopSealFracturePressure(), "psi"));
  }

  private static void addSourceProperties(SourceAndSeal sourceAndSeal, SummaryDataView summaryDataView) {
    String sourceAgeStage = "";
    if (sourceAndSeal.getSourceAgeStage() != null) {
      sourceAgeStage = sourceAndSeal.getSourceAgeStage().getDisplayName();
    } else if (sourceAndSeal.getSourceAgeStageManual() != null) {
      sourceAgeStage = sourceAndSeal.getSourceAgeStageManual();
    }
    summaryDataView.addKeyValue(SOURCE_AGE_STAGE, sourceAgeStage);

    summaryDataView.addKeyValue(SOURCE_GROUP_NAME,
        sourceAndSeal.getSourceGroupName() == null
            ? ""
            : sourceAndSeal.getSourceGroupName().getDisplayName());

    String sourceFormationName = "";
    if (sourceAndSeal.getSourceFormationName() != null) {
      sourceFormationName = sourceAndSeal.getSourceFormationName().getDisplayName();
    } else if (sourceAndSeal.getSourceFormationNameManual() != null) {
      sourceFormationName = sourceAndSeal.getSourceFormationNameManual();
    }
    summaryDataView.addKeyValue(SOURCE_FORMATION_NAME, sourceFormationName);

    String sourceLithology = "";
    if (StandardLithology.OTHER.equals(sourceAndSeal.getSourceLithology())) {
      sourceLithology = sourceAndSeal.getSourceLithologyOther();
    } else if (sourceAndSeal.getSourceLithology() != null) {
      sourceLithology = sourceAndSeal.getSourceLithology().getDisplayName();
    }
    summaryDataView.addKeyValue(SOURCE_LITHOLOGY, sourceLithology);
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
