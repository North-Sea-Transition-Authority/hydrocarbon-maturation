package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard.simpleSummaryCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class FieldReservoirVolumetricsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String OIL_VOLUMETRICS = "Oil volumetrics";
  public static final String GAS_VOLUMETRICS = "Gas volumetrics";
  public static final String OIL_AND_GAS_VOLUMETRICS = "Oil and gas volumetrics";
  public static final String INITIALLY_IN_PLACE_CORRECT = "Is the reservoir initially in place information correct?";

  static final List<SubmissionType> INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES = List.of(SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP);

  public static final int FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER = 60;

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final FieldReservoirVolumetricsRepository fieldReservoirVolumetricsRepository;

  @Autowired
  public FieldReservoirVolumetricsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      GeneralReservoirInformationService generalReservoirInformationService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      FieldReservoirVolumetricsRepository fieldReservoirVolumetricsRepository) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.fieldReservoirVolumetricsRepository = fieldReservoirVolumetricsRepository;
  }


  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      return Optional.empty();
    }

    var summaryItems = new ArrayList<SummaryItem>();
    var reservoirHydrocarbons = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());
    if (generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)) {
      summaryItems.add(getOilCaseSummary(reservoirSubmissionDetail));
    } else if (generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)) {
      summaryItems.add(getGasCaseSummary(reservoirSubmissionDetail));
    } else if (generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      summaryItems.add(getCombinedSummary(reservoirSubmissionDetail));
    }
    return Optional.of(new SummarySection(FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getOilCaseSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var listOfInstances = List.of(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD);

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    );

    var listOfCards = new ArrayList<SummaryCard>();
    listOfCards.add(SummaryCard.probabilitySummaryCard(tableView));

    getInitiallyInPlaceViewOptional(reservoirSubmissionDetail).ifPresent(
        isInitiallyInPlaceCorrectView -> listOfCards.add(simpleSummaryCard(isInitiallyInPlaceCorrectView))
    );

    return SummaryItem.withCards(OIL_VOLUMETRICS, listOfCards);
  }

  private SummaryItem getGasCaseSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    List<ProbabilityDistributionFieldInstance> listOfInstances =
        List.of(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD);

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    );

    var listOfCards = new ArrayList<SummaryCard>();
    listOfCards.add(SummaryCard.probabilitySummaryCard(tableView));

    getInitiallyInPlaceViewOptional(reservoirSubmissionDetail).ifPresent(
        isInitiallyInPlaceCorrectView -> listOfCards.add(simpleSummaryCard(isInitiallyInPlaceCorrectView))
    );

    return SummaryItem.withCards(GAS_VOLUMETRICS, listOfCards);
  }

  private SummaryItem getCombinedSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    List<ProbabilityDistributionFieldInstance> listOfInstances =
        List.of(
            ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
            ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
        );

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    );

    var listOfCards = new ArrayList<SummaryCard>();
    listOfCards.add(SummaryCard.probabilitySummaryCard(tableView));

    getInitiallyInPlaceViewOptional(reservoirSubmissionDetail).ifPresent(
        isInitiallyInPlaceCorrectView -> listOfCards.add(simpleSummaryCard(isInitiallyInPlaceCorrectView))
    );

    return SummaryItem.withCards(OIL_AND_GAS_VOLUMETRICS, listOfCards);
  }

  private Optional<SummaryDataView> getInitiallyInPlaceViewOptional(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirCarriedForward = reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
        reservoirSubmissionDetail.getReservoir()
    );

    var submissionType = reservoirSubmissionDetail.getSubmissionDetail().getSubmissionType();
    if (BooleanUtils.isFalse(reservoirCarriedForward)
        || !INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES.contains(submissionType)) {
      return Optional.empty();
    }

    var fieldVolumetrics = fieldReservoirVolumetricsRepository.findByReservoirSubmissionDetail(
            reservoirSubmissionDetail)
        .orElse(new FieldReservoirVolumetrics());

    var isInitiallyInPlaceCorrectView = SummaryDataView.newWithKeyValue(
        INITIALLY_IN_PLACE_CORRECT,
        BooleanUtil.yesNoFromBoolean(fieldVolumetrics.getInitiallyInPlaceCorrect())
    );

    return Optional.of(isInitiallyInPlaceCorrectView);
  }
}
