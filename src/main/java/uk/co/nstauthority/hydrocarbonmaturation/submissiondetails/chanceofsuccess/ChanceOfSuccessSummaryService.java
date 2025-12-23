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

    var prospectSpecificTrapPresence = chanceOfSuccess.getProspectTrapPresence() != null
        ? chanceOfSuccess.getProspectTrapPresence().toString() + "%" : "";
    var prospectSpecificTrapPresenceComments = chanceOfSuccess.getProspectTrapPresenceComments() != null
        ? chanceOfSuccess.getProspectTrapPresenceComments() : "";
    var prospectSpecificTrapEffectiveness = chanceOfSuccess.getProspectTrapEffectiveness() != null
        ? chanceOfSuccess.getProspectTrapEffectiveness().toString() + "%" : "";
    var prospectSpecificTrapEffectivenessComments = chanceOfSuccess.getProspectTrapEffectivenessComments() != null
        ? chanceOfSuccess.getProspectTrapEffectivenessComments() : "";
    var prospectSpecificSourcePresence = chanceOfSuccess.getProspectSourcePresence() != null
        ? chanceOfSuccess.getProspectSourcePresence().toString() + "%" : "";
    var prospectSpecificSourcePresenceComments = chanceOfSuccess.getProspectSourcePresenceComments() != null
        ? chanceOfSuccess.getProspectSourcePresenceComments() : "";
    var prospectSpecificSourceEffectiveness = chanceOfSuccess.getProspectSourceEffectiveness() != null
        ? chanceOfSuccess.getProspectSourceEffectiveness().toString() + "%" : "";
    var prospectSpecificSourceEffectivenessComments = chanceOfSuccess.getProspectSourceEffectivenessComments() != null
        ? chanceOfSuccess.getProspectSourceEffectivenessComments() : "";
    var prospectSpecificReservoirPresence = chanceOfSuccess.getProspectReservoirPresence() != null
        ? chanceOfSuccess.getProspectReservoirPresence().toString() + "%" : "";
    var prospectSpecificReservoirPresenceComments = chanceOfSuccess.getProspectReservoirPresenceComments() != null
        ? chanceOfSuccess.getProspectReservoirPresenceComments() : "";
    var prospectSpecificReservoirEffectiveness = chanceOfSuccess.getProspectReservoirEffectiveness() != null
        ? chanceOfSuccess.getProspectReservoirEffectiveness().toString() + "%" : "";
    var prospectSpecificReservoirEffectivenessComments = chanceOfSuccess.getProspectReservoirEffectivenessComments() != null
        ? chanceOfSuccess.getProspectReservoirEffectivenessComments() : "";

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
        prospectSpecificTrapPresence,
        prospectSpecificTrapPresenceComments,
        prospectSpecificTrapEffectiveness,
        prospectSpecificTrapEffectivenessComments,
        prospectSpecificSourcePresence,
        prospectSpecificSourcePresenceComments,
        prospectSpecificSourceEffectiveness,
        prospectSpecificSourceEffectivenessComments,
        prospectSpecificReservoirPresence,
        prospectSpecificReservoirPresenceComments,
        prospectSpecificReservoirEffectiveness,
        prospectSpecificReservoirEffectivenessComments,
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
