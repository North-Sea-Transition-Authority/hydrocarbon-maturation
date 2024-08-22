package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceUnits;
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
  public static final String KV_KH_AVERAGE = "%s average".formatted(
      ProbabilityOfExceedanceUnits.KV_KH.getUnitDisplay()
  );

  public static final int PERMEABILITY_DISPLAY_ORDER = 20;
  private static final List<ProbabilityDistributionFieldInstance> PERMEABILITY_FIELD_INSTANCES = List.of(
      ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY,
      ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY,
      ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY
  );

  private final PermeabilityPersistenceService permeabilityPersistenceService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public PermeabilitySummaryService(PermeabilityPersistenceService permeabilityPersistenceService,
                                    ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.permeabilityPersistenceService = permeabilityPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var permeabilityDetail = permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail);
    return permeabilityDetail.map(this::createSummaryViewFromPermeability)
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

    return new PermeabilityDetailSummaryView(
        BooleanUtil.yesNoFromBoolean(permeabilityDetail.getHasPermeability(), ""),
        ObjectUtils.defaultIfNull(permeabilityDetail.getHasPermeability(), true),
        NumberUtil.getNumberStringWithUnitsOrEmpty(
            permeabilityDetail.getPermeabilityMin(),
            ProbabilityOfExceedanceUnits.MD.getUnitDisplay()
        ),
        NumberUtil.getNumberStringWithUnitsOrEmpty(
            permeabilityDetail.getPermeabilityMax(),
            ProbabilityOfExceedanceUnits.MD.getUnitDisplay()
        ),
        NumberUtil.getNumberStringWithUnitsOrEmpty(
            permeabilityDetail.getPermeabilityAverage(),
            ProbabilityOfExceedanceUnits.MD.getUnitDisplay()
        ),
        averageMethodUsed,
        permeabilitySource,
        otherPermeabilitySource,
        ObjectUtils.defaultIfNull(permeabilityDetail.getPermeabilityComments(), ""),
        NumberUtil.getNumberStringWithUnitsOrEmpty(
            permeabilityDetail.getKvKhAverage(),
            ProbabilityOfExceedanceUnits.KV_KH.getUnitDisplay()
        )
    );
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    var summaryItems = new ArrayList<>(List.of(getPermeabilityPropertySummary(reservoirSubmissionDetail)));
    if (reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      summaryItems.add(getPermeabilityFieldProbabilitiesSummary(reservoirSubmissionDetail));
    }
    return Optional.of(new SummarySection(PERMEABILITY_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getPermeabilityPropertySummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail).ifPresentOrElse(
        permeabilityDetail -> {
          if (reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()) {
            summaryDataView.addKeyValue(PERMEABILITY_MIN,
                NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityMin(), "mD"));
            summaryDataView.addKeyValue(PERMEABILITY_MAX,
                NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityMax(), "mD"));
            summaryDataView.addKeyValue(PERMEABILITY_AVERAGE,
                NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getPermeabilityAverage(), "mD"));
            summaryDataView.addKeyValue(AVERAGE_METHOD_USED,
                DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(permeabilityDetail.getAverageMethodUsed()));
          }

          var permeabilitySource = permeabilityDetail.getPermeabilitySource();
          if (permeabilitySource != null) {
            if (permeabilitySource.equals(PermeabilitySource.OTHER)) {
              summaryDataView.addKeyValue(PERMEABILITY_SOURCE,
                  permeabilityDetail.getSourceOther());
            } else {
              summaryDataView.addKeyValue(PERMEABILITY_SOURCE,
                  permeabilityDetail.getPermeabilitySource().getDisplayName());
            }
          } else {
            summaryDataView.addKeyValue(PERMEABILITY_SOURCE, "");
          }
          summaryDataView.addKeyValue(PERMEABILITY_COMMENTS,
              permeabilityDetail.getPermeabilityComments());
          if (reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()) {
            summaryDataView.addKeyValue(KV_KH_AVERAGE,
                NumberUtil.getNumberStringWithUnitsOrEmpty(permeabilityDetail.getKvKhAverage(),
                    ProbabilityOfExceedanceUnits.KV_KH.getUnitDisplay()
                ));
          }
        }, () -> {
          if (reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()) {
            summaryDataView.addKeyValue(PERMEABILITY_MIN, "");
            summaryDataView.addKeyValue(PERMEABILITY_MAX, "");
            summaryDataView.addKeyValue(PERMEABILITY_AVERAGE, "");
            summaryDataView.addKeyValue(AVERAGE_METHOD_USED, "");
          }
          summaryDataView.addKeyValue(PERMEABILITY_SOURCE, "");
          summaryDataView.addKeyValue(PERMEABILITY_COMMENTS, "");
          if (reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()) {
            summaryDataView.addKeyValue(KV_KH_AVERAGE, "");
          }
        });

    var summaryCard = SummaryCard.simpleSummaryCard(
        summaryDataView
    );

    return SummaryItem.withCard(PERMEABILITY, summaryCard);
  }

  private SummaryItem getPermeabilityFieldProbabilitiesSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        PERMEABILITY_FIELD_INSTANCES,
        reservoirSubmissionDetail.getSubmissionDetail(),
        reservoirSubmissionDetail
    );

    return SummaryItem.withCard(PERMEABILITY, SummaryCard.probabilitySummaryCard(tableView));
  }
}
