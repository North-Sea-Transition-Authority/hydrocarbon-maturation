package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class ChanceOfSuccessSummaryService {

  private final ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  @Autowired
  public ChanceOfSuccessSummaryService(ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService) {
    this.chanceOfSuccessPersistenceService = chanceOfSuccessPersistenceService;
  }

  public ChanceOfSuccessSummaryView getChanceOfSuccessSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var chanceOfSuccess = chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail);
    return chanceOfSuccess.map(this::createSummaryViewFromChanceOfSuccess)
        .orElse(ChanceOfSuccessSummaryView.empty());
  }

  private ChanceOfSuccessSummaryView createSummaryViewFromChanceOfSuccess(ChanceOfSuccess chanceOfSuccess) {
    var playSourcePercentage = chanceOfSuccess.getSourcePercentage() != null
        ? chanceOfSuccess.getSourcePercentage().toString() + "%" : "";
    var playSourceComments = chanceOfSuccess.getSourceComments() != null
        ? chanceOfSuccess.getSourceComments() : "";
    var playReservoirPercentage = chanceOfSuccess.getReservoirPercentage() != null
        ? chanceOfSuccess.getReservoirPercentage().toString() + "%" : "";
    var playReservoirComments = chanceOfSuccess.getReservoirComments() != null
        ? chanceOfSuccess.getReservoirComments() : "";
    var playTopSealPercentage = chanceOfSuccess.getTopSealPercentage() != null
        ? chanceOfSuccess.getTopSealPercentage().toString() + "%" : "";
    var playTopSealComments = chanceOfSuccess.getTopSealComments() != null
        ?  chanceOfSuccess.getTopSealComments() : "";

    var totalPlayChanceOfSuccess = chanceOfSuccess.getPlayTotal() != null
        ? chanceOfSuccess.getPlayTotal().toString() + "%" : "";

    var prospectSpecificSourcePercentage = chanceOfSuccess.getProspectSourcePercentage() != null
        ? chanceOfSuccess.getProspectSourcePercentage().toString() + "%" : "";
    var prospectSpecificSourceComments = chanceOfSuccess.getProspectSourceComments() != null
        ? chanceOfSuccess.getProspectSourceComments() : "";
    var prospectSpecificReservoirPercentage = chanceOfSuccess.getProspectReservoirPercentage() != null
        ? chanceOfSuccess.getProspectReservoirPercentage().toString() + "%" : "";
    var prospectSpecificReservoirComments = chanceOfSuccess.getProspectReservoirComments() != null
        ? chanceOfSuccess.getProspectReservoirComments() : "";
    var prospectSpecificTopSealPercentage = chanceOfSuccess.getProspectTopSealPercentage() != null
        ? chanceOfSuccess.getProspectTopSealPercentage().toString() + "%" : "";
    var prospectSpecificTopSealComments = chanceOfSuccess.getProspectTopSealComments() != null
        ? chanceOfSuccess.getProspectTopSealComments() : "";
    var prospectSpecificTrapPercentage = chanceOfSuccess.getProspectTrapPercentage() != null
        ? chanceOfSuccess.getProspectTrapPercentage().toString() + "%" : "";
    var prospectSpecificTrapComments = chanceOfSuccess.getProspectTrapComments() != null
        ? chanceOfSuccess.getProspectTrapComments() : "";
    var prospectSpecificMigrationPercentage = chanceOfSuccess.getProspectMigrationPercentage() != null
        ? chanceOfSuccess.getProspectMigrationPercentage().toString() + "%" : "";
    var prospectSpecificMigrationComments = chanceOfSuccess.getProspectMigrationComments() != null
        ? chanceOfSuccess.getProspectMigrationComments() : "";

    var prospectSpecificChance = chanceOfSuccess.getProspectSpecificTotal() != null
        ? chanceOfSuccess.getProspectSpecificTotal().toString() + "%" : "";

    var overallChanceOfSuccess = chanceOfSuccess.getOverallTotal() != null
        ? chanceOfSuccess.getOverallTotal() + "%" : "";

    var geophysicallyModified = chanceOfSuccess.getGeophysicallyModified() != null
        ? BooleanUtil.yesNoFromBoolean(chanceOfSuccess.getGeophysicallyModified()) : "";
    var geophysicallyModifiedPercentage = chanceOfSuccess.getGeophysicallyModifiedPercentage() != null
        ? chanceOfSuccess.getGeophysicallyModifiedPercentage().toString() + "%" : "";
    var geophysicallyModifiedComments = chanceOfSuccess.getGeophysicallyModifiedComments() != null
        ? chanceOfSuccess.getGeophysicallyModifiedComments() : "";
    var specificWellLocation = chanceOfSuccess.getRelatedToSpecificWellLocation() != null
        ? BooleanUtil.yesNoFromBoolean(chanceOfSuccess.getRelatedToSpecificWellLocation()) : "";
    var wellModifiedPercentage = chanceOfSuccess.getWellModifiedPercentage() != null
        ? chanceOfSuccess.getWellModifiedPercentage().toString() + "%" : "";
    var wellModifiedComment = chanceOfSuccess.getWellModifiedComments() != null
        ? chanceOfSuccess.getWellModifiedComments() : "";

    return new ChanceOfSuccessSummaryView(
        playSourcePercentage,
        playSourceComments,
        playReservoirPercentage,
        playReservoirComments,
        playTopSealPercentage,
        playTopSealComments,
        totalPlayChanceOfSuccess,
        prospectSpecificSourcePercentage,
        prospectSpecificSourceComments,
        prospectSpecificReservoirPercentage,
        prospectSpecificReservoirComments,
        prospectSpecificTopSealPercentage,
        prospectSpecificTopSealComments,
        prospectSpecificTrapPercentage,
        prospectSpecificTrapComments,
        prospectSpecificMigrationPercentage,
        prospectSpecificMigrationComments,
        prospectSpecificChance,
        overallChanceOfSuccess,
        geophysicallyModified,
        geophysicallyModifiedPercentage,
        geophysicallyModifiedComments,
        specificWellLocation,
        wellModifiedPercentage,
        wellModifiedComment);
  }
}
