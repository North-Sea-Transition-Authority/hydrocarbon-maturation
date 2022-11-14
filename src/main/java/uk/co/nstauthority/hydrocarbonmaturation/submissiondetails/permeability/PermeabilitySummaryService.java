package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class PermeabilitySummaryService {

  private final PermeabilityPersistenceService permeabilityPersistenceService;

  @Autowired
  public PermeabilitySummaryService(PermeabilityPersistenceService permeabilityPersistenceService) {
    this.permeabilityPersistenceService = permeabilityPersistenceService;
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var permeabiliyDetail = permeabilityPersistenceService.getPermeabilityDetail(submissionDetail);
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
    var kvKhAverageMethodUsed = permeabilityDetail.getKvKhAverageMethodUsed() != null
        ? permeabilityDetail.getKvKhAverageMethodUsed().getDisplayName()
        : "";
    var kvKhAverageMethodUsedOther = "";
    if (permeabilityDetail.getKvKhAverageMethodUsed() == PermeabilitySource.OTHER) {
      kvKhAverageMethodUsedOther = permeabilityDetail.getKvKhAverageMethodUsedOther();
    }
    var kvKhSource = permeabilityDetail.getKvKhSource() != null
        ? permeabilityDetail.getKvKhSource().getDisplayName()
        : "";
    var otherKvKhSource = "";
    if (permeabilityDetail.getKvKhSource() == PermeabilitySource.OTHER) {
      otherKvKhSource = ObjectUtils.defaultIfNull(permeabilityDetail.getKvKhSourceOther(), "");
    }
    return new PermeabilityDetailSummaryView(
        BooleanUtil.yesNoFromBoolean(permeabilityDetail.getHasPermeability(), ""),
        permeabilityDetail.getHasPermeability(),
        ObjectUtils.defaultIfNull(permeabilityDetail.getPermeabilityMin(), "").toString(),
        ObjectUtils.defaultIfNull(permeabilityDetail.getPermeabilityMax(), "").toString(),
        ObjectUtils.defaultIfNull(permeabilityDetail.getPermeabilityAverage(), "").toString(),
        averageMethodUsed,
        permeabilitySource,
        otherPermeabilitySource,
        ObjectUtils.defaultIfNull(permeabilityDetail.getPermeabilityComments(), ""),
        ObjectUtils.defaultIfNull(permeabilityDetail.getKvKhMin(), "").toString(),
        ObjectUtils.defaultIfNull(permeabilityDetail.getKvKhMax(), "").toString(),
        ObjectUtils.defaultIfNull(permeabilityDetail.getKvKhAverage(), "").toString(),
        kvKhAverageMethodUsed,
        kvKhAverageMethodUsedOther,
        kvKhSource,
        otherKvKhSource,
        ObjectUtils.defaultIfNull(permeabilityDetail.getKvKhComments(), "")
    );
  }
}
