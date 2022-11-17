package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class DryWellDetailsSummaryService {

  private final DryWellDetailsPersistenceService dryWellDetailsPersistenceService;

  @Autowired
  public DryWellDetailsSummaryService(DryWellDetailsPersistenceService dryWellDetailsPersistenceService) {
    this.dryWellDetailsPersistenceService = dryWellDetailsPersistenceService;
  }

  public DryWellDetailsSummaryView getDryWellDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var dryWellDetail = dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail);
    return dryWellDetail.map(this::createSummaryViewFromDryWellDetail)
        .orElse(DryWellDetailsSummaryView.empty());
  }

  private DryWellDetailsSummaryView createSummaryViewFromDryWellDetail(DryWellDetail dryWellDetail) {
    var dryWell = ObjectUtils.defaultIfNull(dryWellDetail.getDryWellName(), "");

    var longitudeDegrees = dryWellDetail.getLongitudeDegrees() != null
        ? dryWellDetail.getLongitudeDegrees().toString() + "°"
        : "";
    var longitudeMinutes = dryWellDetail.getLongitudeMinutes() != null
        ? dryWellDetail.getLongitudeMinutes().toString() + "''"
        : "";
    var longitudeSeconds = dryWellDetail.getLongitudeSeconds() != null
        ? dryWellDetail.getLongitudeSeconds().toString() + "'"
        : "";
    var latitudeDegrees = dryWellDetail.getLatitudeDegrees() != null
        ? dryWellDetail.getLatitudeDegrees().toString() + "°"
        : "";
    var latitudeMinutes = dryWellDetail.getLatitudeMinutes() != null
        ? dryWellDetail.getLatitudeMinutes().toString() + "''"
        : "";
    var latitudeSeconds = dryWellDetail.getLatitudeSeconds() != null
        ? dryWellDetail.getLatitudeSeconds().toString() + "'"
        : "";
    var longitudeHemisphere = dryWellDetail.getLongitudeHemisphere() != null
        ? dryWellDetail.getLongitudeHemisphere().getDisplayName()
        : "";
    var datum = dryWellDetail.getDatum() != null
        ? dryWellDetail.getDatum().getDisplayName()
        : "";
    var totalDepthFormation = ObjectUtils.defaultIfNull(dryWellDetail.getTotalDepthFormation(), "");
    var totalDepthFormationComments = ObjectUtils.defaultIfNull(dryWellDetail.getTotalDepthFormationComments(), "");
    var dataAcquisitionProgramme = ObjectUtils.defaultIfNull(dryWellDetail.getDataAcquisitionProgramme(), "");
    var geophysicalUpdate = ObjectUtils.defaultIfNull(dryWellDetail.getGeophysicalUpdateCommentary(), "");
    var dryHoleType = dryWellDetail.getDryHoleType() != null
        ? dryWellDetail.getDryHoleType().getDisplayName()
        : "";
    var dryHoleDescription = ObjectUtils.defaultIfNull(dryWellDetail.getDryHoleDescription(), "");
    var dryHoleScope = ObjectUtils.defaultIfNull(dryWellDetail.getDryHoleScope(), "");
    var dryHoleComments = ObjectUtils.defaultIfNull(dryWellDetail.getDryHoleComments(), "");
    var dryHoleCost = dryWellDetail.getDryHoleCost() != null
        ? "£" + dryWellDetail.getDryHoleCost() + " million"
        : "";

    return new DryWellDetailsSummaryView(dryWell, latitudeDegrees, latitudeMinutes, latitudeSeconds, longitudeDegrees,
        longitudeMinutes, longitudeSeconds, longitudeHemisphere, datum, totalDepthFormation, totalDepthFormationComments,
        dataAcquisitionProgramme, geophysicalUpdate, dryHoleType, dryHoleDescription, dryHoleScope, dryHoleComments,
        dryHoleCost);
  }
}
