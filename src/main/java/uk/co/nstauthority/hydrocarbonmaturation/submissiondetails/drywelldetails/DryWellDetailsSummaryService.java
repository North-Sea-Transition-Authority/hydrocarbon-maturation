package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

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
    var totalDepthFormation = dryWellDetail.getTotalDepthFormation() != null
        ? DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getTotalDepthFormation())
        : ObjectUtils.defaultIfNull(dryWellDetail.getTotalDepthFormationManual(), "");

    return new DryWellDetailsSummaryView(
        ObjectUtils.defaultIfNull(dryWellDetail.getWellReference(), ""),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(dryWellDetail.getTopReservoirLatitudeDegrees(), "°"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(dryWellDetail.getTopReservoirLatitudeMinutes(), "'"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(dryWellDetail.getTopReservoirLatitudeSeconds(), "\""),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(dryWellDetail.getTopReservoirLongitudeDegrees(), "°"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(dryWellDetail.getTopReservoirLongitudeMinutes(), "'"),
        NumberUtil.getNumberStringWithUnitsWithoutSpaceOrEmpty(dryWellDetail.getTopReservoirLongitudeSeconds(), "\""),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getTopReservoirLongitudeHemisphere()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getTopReservoirDatum()),
        totalDepthFormation,
        ObjectUtils.defaultIfNull(dryWellDetail.getTotalDepthFormationComments(), ""),
        ObjectUtils.defaultIfNull(dryWellDetail.getDataAcquisitionProgramme(), ""),
        ObjectUtils.defaultIfNull(dryWellDetail.getGeophysicalUpdateCommentary(), ""),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getDryHoleType()),
        ObjectUtils.defaultIfNull(dryWellDetail.getDryHoleDescription(), ""),
        ObjectUtils.defaultIfNull(dryWellDetail.getDryHoleScope(), ""),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getSourceFailureCategory()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getReservoirFailureCategory()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getSealFailureCategory()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getTrapFailureCategory()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(dryWellDetail.getMigrationFailureCategory()),
        ObjectUtils.defaultIfNull(dryWellDetail.getDryHoleComments(), ""),
        NumberUtil.getNumberStringWithUnitsOrEmpty(dryWellDetail.getDryHoleCost(), "£", "million"));
  }
}
