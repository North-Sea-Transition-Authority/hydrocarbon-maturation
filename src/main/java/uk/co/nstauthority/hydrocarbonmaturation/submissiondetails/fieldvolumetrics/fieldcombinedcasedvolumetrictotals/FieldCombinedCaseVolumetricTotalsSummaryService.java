package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.VolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class FieldCombinedCaseVolumetricTotalsSummaryService implements SummarySectionService<SubmissionDetail> {
  public static final String TOTAL_COMBINED_VOLUMETRICS = "Total oil and gas volumetrics";
  public static final int TOTAL_COMBINED_VOLUMETRICS_ORDER = 20;

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final VolumetricTotalsService volumetricTotalsService;
  private final CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository;

  @Autowired
  public FieldCombinedCaseVolumetricTotalsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      VolumetricTotalsService volumetricTotalsService,
      CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository) {

    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.volumetricTotalsService = volumetricTotalsService;
    this.combinedCaseVolumetricTotalsRepository = combinedCaseVolumetricTotalsRepository;
  }

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    if (!submissionDetail.isFieldSubmission()
        || !volumetricTotalsService.isCombinedCase(reservoirSubmissionDetails)) {
      return Optional.empty();
    }
    var summaryItems = List.of(getResourcesAndReservesSummary(submissionDetail));
    return Optional.of(new SummarySection(TOTAL_COMBINED_VOLUMETRICS_ORDER, summaryItems, ""));
  }

  private SummaryItem getResourcesAndReservesSummary(SubmissionDetail submissionDetail) {
    var listOfInstances = new ArrayList<>(List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
      )
    );

    var combinedCaseVolumetricTotal = combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new CombinedCaseVolumetricTotal());
    if (BooleanUtils.isTrue(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())) {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD
      );
    }

    if (!submissionDetail.isConceptSelectReportSubmission()) {
      listOfInstances.addAll(List.of(
              ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
          )
      );
    }

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, null
        );

    return SummaryItem.withCards(TOTAL_COMBINED_VOLUMETRICS, List.of(
        getOtherSummary(submissionDetail),
        SummaryCard.fieldProbabilitySummaryCard(listOfSummaryViews)));
  }

  private SummaryCard getOtherSummary(SubmissionDetail submissionDetail) {
    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD);
    var listOfSummaryViews = probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances,
            submissionDetail,
            null
        );
    return SummaryCard.probabilitySummaryCard(listOfSummaryViews);
  }
}
