package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import static uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard.simpleSummaryCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class FieldOilCaseVolumetricTotalsSummaryService implements SummarySectionService<SubmissionDetail> {

  public static final String TOTAL_OIL_VOLUMETRICS = "Total oil volumetrics";
  public static final int TOTAL_OIL_VOLUMETRICS_ORDER = 40;
  public static final String TOTAL_VOLUMETRICS_CORRECT =
      "Does the field volumetric information reflect the current state of the field?";
  static final List<SubmissionType> VOLUMETRIC_CORRECT_QUESTION_SUBMISSION_TYPES = List.of(SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP);


  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final OilCaseVolumetricTotalsRepository oilCaseVolumetricTotalsRepository;

  @Autowired
  public FieldOilCaseVolumetricTotalsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      SubmissionDetailService submissionDetailService,
      OilCaseVolumetricTotalsRepository oilCaseVolumetricTotalsRepository) {

    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.oilCaseVolumetricTotalsRepository = oilCaseVolumetricTotalsRepository;
  }

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
            submissionDetail);
    if (!submissionDetail.isFieldSubmission() || !reservoirHydrocarbonCaseService.isOilCaseOnly(
        reservoirSubmissionDetails)) {
      return Optional.empty();
    }
    var summaryItems = List.of(getResourcesAndReservesSummary(submissionDetail));
    return Optional.of(new SummarySection(TOTAL_OIL_VOLUMETRICS_ORDER, summaryItems, ""));
  }

  private SummaryCard getOtherSummary(SubmissionDetail submissionDetail) {
    var listOfInstances = List.of(ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD);
    var tableView = probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            listOfInstances,
            submissionDetail,
            null
        );
    return SummaryCard.probabilitySummaryCard(tableView);
  }

  private SummaryItem getResourcesAndReservesSummary(SubmissionDetail submissionDetail) {
    var listOfInstances = new ArrayList<>(List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    ));

    var withReserves = !submissionDetail.isConceptSelectReportSubmission();
    if (BooleanUtils.isTrue(withReserves)) {
      listOfInstances.addAll(List.of(
              ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
          )
      );
    }

    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD);
    if (BooleanUtils.isTrue(withReserves)) {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD);
    }

    var listOfCards = new ArrayList<SummaryCard>();
    listOfCards.add(getOtherSummary(submissionDetail));

    var tableView =
        probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
            listOfInstances, submissionDetail, null
        );
    listOfCards.add(SummaryCard.probabilitySummaryCard(tableView));

    getInformationCorrectOptionalView(submissionDetail).ifPresent(
        informationCorrectView -> listOfCards.add(simpleSummaryCard(informationCorrectView))
    );

    return SummaryItem.withCards(TOTAL_OIL_VOLUMETRICS, listOfCards);
  }

  private Optional<SummaryDataView> getInformationCorrectOptionalView(SubmissionDetail submissionDetail) {
    var fieldSubmissionCarriedForward = submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(
        submissionDetail);

    if (BooleanUtils.isFalse(fieldSubmissionCarriedForward)
        || !VOLUMETRIC_CORRECT_QUESTION_SUBMISSION_TYPES.contains(submissionDetail.getSubmissionType())) {
      return Optional.empty();
    }

    var fieldVolumetrics = oilCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new OilCaseVolumetricTotal());

    var isInitiallyInPlaceCorrectView = SummaryDataView.newWithKeyValue(
        TOTAL_VOLUMETRICS_CORRECT,
        BooleanUtil.yesNoFromBoolean(fieldVolumetrics.getFieldVolumetricInformationCorrect())
    );

    return Optional.of(isInitiallyInPlaceCorrectView);
  }
}
