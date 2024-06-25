package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class FdpaReservoirIncrementalVolumesSummaryService
    implements SummarySectionService<ReservoirSubmissionDetail> {

  static final int FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER = 70;
  static final String SUMMARY_HEADING = "FDPA incremental in place volumes";
  static final String CHANGE_TO_IN_PLACE_QUESTION
      = "Is there a change to the in place volumes on this reservoir as a result of this FDPA?";
  static final String REASON_FOR_CHANGE = "Why are the volumes changing as a result of this FDPA?";

  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final FdpaReservoirIncrementalVolumesRepository incrementalVolumesRepository;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public FdpaReservoirIncrementalVolumesSummaryService(
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
      FdpaReservoirIncrementalVolumesRepository incrementalVolumesRepository,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.incrementalVolumesRepository = incrementalVolumesRepository;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isFieldDevelopmentPlanAddendumSubmission()) {
      return Optional.empty();
    }

    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(
        List.of(reservoirSubmissionDetail)
    );

    return switch (hydrocarbonsDiscovered) {
      case GAS_ONLY -> populateSummarySection(getGasSummaryItem(reservoirSubmissionDetail));
      case OIL_ONLY -> populateSummarySection(getOilSummaryItem(reservoirSubmissionDetail));
      case MIXED_OIL_AND_GAS -> populateSummarySection(getMixedSummaryItem(reservoirSubmissionDetail));
    };
  }

  private Optional<SummarySection> populateSummarySection(SummaryItem casedSummaryItem) {
    return Optional.of(new SummarySection(
        FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER,
        List.of(casedSummaryItem),
        ""
    ));
  }

  private SummaryItem getGasSummaryItem(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var instances = List.of(
        ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
    );

    return getSummaryItemFromInstances(instances, reservoirSubmissionDetail);
  }

  private SummaryItem getOilSummaryItem(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var instances = List.of(
        ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA
    );

    return getSummaryItemFromInstances(instances, reservoirSubmissionDetail);
  }

  private SummaryItem getMixedSummaryItem(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var instances = List.of(
        ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
        ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
    );

    return getSummaryItemFromInstances(instances, reservoirSubmissionDetail);
  }

  private SummaryCard getBasicInformationCard(
      FdpaReservoirIncrementalVolume incrementalVolume) {
    var dataView = SummaryDataView
        .newWithKeyValue(
            CHANGE_TO_IN_PLACE_QUESTION,
            BooleanUtil.yesNoFromBoolean(incrementalVolume.getChangeToInPlaceVolumes())
        )
        .addKeyValue(
            REASON_FOR_CHANGE,
            incrementalVolume.getComment()
        );

    return SummaryCard.simpleSummaryCard(dataView);
  }

  private SummaryItem getSummaryItemFromInstances(List<ProbabilityDistributionFieldInstance> instances,
                                                  ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            instances, reservoirSubmissionDetail.getSubmissionDetail(), reservoirSubmissionDetail
        );

    var incrementalVolume = incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .orElse(new FdpaReservoirIncrementalVolume());

    var cards = new ArrayList<SummaryCard>();
    cards.add(getBasicInformationCard(incrementalVolume));

    if (BooleanUtils.isTrue(incrementalVolume.getChangeToInPlaceVolumes())) {
      cards.add(SummaryCard.probabilitySummaryCard(listOfSummaryViews));
    }

    return SummaryItem.withCards(SUMMARY_HEADING, cards);
  }
}
