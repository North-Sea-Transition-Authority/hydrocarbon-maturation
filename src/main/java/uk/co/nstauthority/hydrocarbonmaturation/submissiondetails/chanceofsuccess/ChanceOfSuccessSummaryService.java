package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    var totalPlayChanceOfSuccess = "";
    BigDecimal totalPlayChanceOfSuccessDecimal = BigDecimal.ZERO;

    if (chanceOfSuccess.getSourcePercentage() != null
        && chanceOfSuccess.getReservoirPercentage() != null
        && chanceOfSuccess.getTopSealPercentage() != null) {
      totalPlayChanceOfSuccessDecimal = chanceOfSuccess.getSourcePercentage().divide(BigDecimal.valueOf(100))
          .multiply(chanceOfSuccess.getReservoirPercentage().divide(BigDecimal.valueOf(100)))
          .multiply(chanceOfSuccess.getTopSealPercentage().divide(BigDecimal.valueOf(100)));
      totalPlayChanceOfSuccess = totalPlayChanceOfSuccessDecimal.multiply(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN) + "%";
    }

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

    var prospectSpecificChance = "";
    BigDecimal prospectSpecificChanceDecimal = BigDecimal.ZERO;

    if (chanceOfSuccess.getProspectSourcePercentage() != null
        && chanceOfSuccess.getProspectReservoirPercentage() != null
        && chanceOfSuccess.getProspectTopSealPercentage() != null
        && chanceOfSuccess.getProspectTrapPercentage() != null
        && chanceOfSuccess.getProspectMigrationPercentage() != null) {
      prospectSpecificChanceDecimal = chanceOfSuccess.getProspectSourcePercentage().divide(BigDecimal.valueOf(100))
          .multiply(chanceOfSuccess.getProspectReservoirPercentage().divide(BigDecimal.valueOf(100)))
          .multiply(chanceOfSuccess.getProspectTopSealPercentage().divide(BigDecimal.valueOf(100)))
          .multiply(chanceOfSuccess.getProspectTrapPercentage().divide(BigDecimal.valueOf(100)))
          .multiply(chanceOfSuccess.getProspectMigrationPercentage().divide(BigDecimal.valueOf(100)));

      prospectSpecificChance = prospectSpecificChanceDecimal
          .multiply(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN) + "%";
    }

    var overallChanceOfSuccess = "";
    if (!prospectSpecificChance.isEmpty() && !totalPlayChanceOfSuccess.isEmpty()) {
      overallChanceOfSuccess = prospectSpecificChanceDecimal.multiply(totalPlayChanceOfSuccessDecimal)
          .multiply(BigDecimal.valueOf(100))
          .setScale(2, RoundingMode.HALF_EVEN) + "%";
    }

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
