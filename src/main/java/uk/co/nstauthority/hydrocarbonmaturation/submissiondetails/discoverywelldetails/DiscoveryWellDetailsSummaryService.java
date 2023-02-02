package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class DiscoveryWellDetailsSummaryService {

  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Autowired
  public DiscoveryWellDetailsSummaryService(
      DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
  }

  public DiscoveryWellDetailsSummaryView getDiscoveryWellDetailSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var discoveryWellDetail = discoveryWellDetailsPersistenceService
        .getDiscoveryWellDetailBySubmissionDetail(submissionDetail);
    return discoveryWellDetail.map(this::createSummaryViewFromDiscoveryWellDetail)
        .orElse(DiscoveryWellDetailsSummaryView.empty());
  }

  private DiscoveryWellDetailsSummaryView createSummaryViewFromDiscoveryWellDetail(
      DiscoveryWellDetail discoveryWellDetail) {

    return new DiscoveryWellDetailsSummaryView(
        ObjectUtils.defaultIfNull(discoveryWellDetail.getDryWellName(), ""),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(discoveryWellDetail.getLatitudeDegrees(), "°"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(discoveryWellDetail.getLatitudeMinutes(), "''"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(discoveryWellDetail.getLatitudeSeconds(), "'"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(discoveryWellDetail.getLongitudeDegrees(), "°"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(discoveryWellDetail.getLongitudeMinutes(), "''"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(discoveryWellDetail.getLongitudeSeconds(), "'"),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(discoveryWellDetail.getLongitudeHemisphere()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(discoveryWellDetail.getDatum()),
        ObjectUtils.defaultIfNull(discoveryWellDetail.getTotalDepthFormation(), ""),
        ObjectUtils.defaultIfNull(discoveryWellDetail.getTotalDepthFormationComments(), ""),
        ObjectUtils.defaultIfNull(discoveryWellDetail.getDataAcquisitionProgramme(), ""),
        ObjectUtils.defaultIfNull(discoveryWellDetail.getGeophysicalUpdateCommentary(), ""),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(discoveryWellDetail.getHydrocarbonsDiscovered()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(discoveryWellDetail.getDiscoveryStatus()),
        ObjectUtils.defaultIfNull(discoveryWellDetail.getChanceOfCommercialityComments(), ""),
        NumberUtil.getNumberStringWithUnitsOrEmpty(discoveryWellDetail.getTotalWellCost(), "£", "million"),
        NumberUtil.getNumberStringWithUnitsOrEmpty(discoveryWellDetail.getSuccessRelatedCosts(), "£", "million")
    );
  }
}
