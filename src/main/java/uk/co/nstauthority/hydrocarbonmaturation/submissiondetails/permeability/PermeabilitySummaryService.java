package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class PermeabilitySummaryService {

  private final PermeabilityPersistenceService permeabilityPersistenceService;

  @Autowired
  public PermeabilitySummaryService(PermeabilityPersistenceService permeabilityPersistenceService) {
    this.permeabilityPersistenceService = permeabilityPersistenceService;
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var permeabiliyDetail = permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail);
    return permeabiliyDetail.map(this::createSummaryViewFromPermeability)
        .orElse(PermeabilityDetailSummaryView.empty());
  }

  private PermeabilityDetailSummaryView createSummaryViewFromPermeability(PermeabilityDetail permeabilityDetail) {
    var averageMethodUsed = permeabilityDetail.getAverageMethodUsed() != null
        ? permeabilityDetail.getAverageMethodUsed().getDisplayName()
        : "";
    var permeabilitySource = permeabilityDetail.getPermeabilitySource() != null
        ? permeabilityDetail.getPermeabilitySource().getDisplayName()
        : "";
    var otherPermeabilitySource = "";
    if (permeabilityDetail.getPermeabilitySource() == PermeabilitySource.OTHER) {
      otherPermeabilitySource = ObjectUtils.defaultIfNull(permeabilityDetail.getSourceOther(), "");
    }

    final String kvKhUnits = "kv/kh";
    final String mdUnits = "md";
    return new PermeabilityDetailSummaryView(
        BooleanUtil.yesNoFromBoolean(permeabilityDetail.getHasPermeability(), ""),
        ObjectUtils.defaultIfNull(permeabilityDetail.getHasPermeability(), true),
        NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityMin(), mdUnits),
        NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityMax(), mdUnits),
        NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityAverage(), mdUnits),
        averageMethodUsed,
        permeabilitySource,
        otherPermeabilitySource,
        ObjectUtils.defaultIfNull(permeabilityDetail.getPermeabilityComments(), ""),
        NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getKvKhAverage(), kvKhUnits)
    );
  }
}
