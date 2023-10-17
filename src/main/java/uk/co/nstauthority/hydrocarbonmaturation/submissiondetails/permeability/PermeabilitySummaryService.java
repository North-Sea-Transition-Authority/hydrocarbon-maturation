package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class PermeabilitySummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String PERMEABILITY = "Permeability";
  public static final String PERMEABILITY_MIN = "Permeability (min)";
  public static final String PERMEABILITY_MAX = "Permeability (max)";
  public static final String PERMEABILITY_AVERAGE = "Permeability (average)";
  public static final String AVERAGE_METHOD_USED = "Average method used";
  public static final String PERMEABILITY_SOURCE = "Permeability source";
  public static final String PERMEABILITY_COMMENTS = "Permeability comments";
  public static final String KV_KH_AVERAGE = "KV/KH average";
  public static final String PERMEABILITY_DISTRIBUTION = "Permeability distribution";
  public static final String HORIZONTAL_PERMEABILITY_DISTRIBUTION = "Horizontal permeability distribution";
  public static final String VERTICAL_PERMEABILITY_DISTRIBUTION = "Vertical permeability distribution";

  public static final int PERMEABILITY_DISPLAY_ORDER = 20;

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
    final String mdUnits = "mD";
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

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail source) {
    var summaryItems = List.of(getDiscoveryPermeabilityPropertySummary(source));
    return Optional.of(new SummarySection(PERMEABILITY_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getDiscoveryPermeabilityPropertySummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail).ifPresentOrElse(
        permeabilityDetail -> {
          summaryDataView.addKeyValue(PERMEABILITY_MIN,
              NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityMin(), "mD"));
          summaryDataView.addKeyValue(PERMEABILITY_MAX,
              NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityMax(), "mD"));
          summaryDataView.addKeyValue(PERMEABILITY_AVERAGE,
              NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityAverage(), "mD"));
          summaryDataView.addKeyValue(AVERAGE_METHOD_USED,
              permeabilityDetail.getAverageMethodUsed().getDisplayName());

          var permeabilitySource = permeabilityDetail.getPermeabilitySource();
          if (permeabilitySource.equals(PermeabilitySource.OTHER)) {
            summaryDataView.addKeyValue(PERMEABILITY_SOURCE,
                permeabilityDetail.getSourceOther());
          } else {
            summaryDataView.addKeyValue(PERMEABILITY_SOURCE,
                permeabilityDetail.getPermeabilitySource().getDisplayName());
          }
          summaryDataView.addKeyValue(PERMEABILITY_COMMENTS,
              permeabilityDetail.getPermeabilityComments());
          summaryDataView.addKeyValue(KV_KH_AVERAGE,
              NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getKvKhAverage(), "kv/kh"));

        }, () -> {
          summaryDataView.addKeyValue(PERMEABILITY_MIN, "");
          summaryDataView.addKeyValue(PERMEABILITY_MAX, "");
          summaryDataView.addKeyValue(PERMEABILITY_AVERAGE, "");
          summaryDataView.addKeyValue(AVERAGE_METHOD_USED, "");
          summaryDataView.addKeyValue(PERMEABILITY_SOURCE, "");
          summaryDataView.addKeyValue(PERMEABILITY_COMMENTS, "");
          summaryDataView.addKeyValue(KV_KH_AVERAGE, "");
        });

    var summaryCard = SummaryCard.simpleSummaryCard(
        summaryDataView
    );

    return SummaryItem.withCard(PERMEABILITY, summaryCard);
  }
}
