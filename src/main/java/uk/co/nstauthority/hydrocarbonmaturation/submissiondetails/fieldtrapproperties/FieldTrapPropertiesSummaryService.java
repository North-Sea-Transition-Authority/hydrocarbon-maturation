package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class FieldTrapPropertiesSummaryService implements SummarySectionService<SubmissionDetail> {

  public static final String FIELD_TRAP_PROPERTIES = "Trap properties";
  public static final int FIELD_TRAP_PROPERTIES_ORDER = 20;

  public static final String TRAP_TYPE = "Trap type";
  public static final String CLOSURE_MECHANISMS = "Closure mechanisms";
  public static final String VERTICALLY_SEPARATE_HC_POOLS = "Number of vertically separate HC pools";
  public static final String STRUCTURAL_COMPARTMENTS = "Number of structural compartments";
  public static final String ORIGINAL_HYDROCARBON_COLUMN_HEIGHT = "Original hydrocarbon column height";

  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public FieldTrapPropertiesSummaryService(TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                           ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isFieldSubmission()) {
      return Optional.empty();
    }

    var summaryItems = List.of(getProbabilityDistributionsSummary(submissionDetail));
    return Optional.of(new SummarySection(FIELD_TRAP_PROPERTIES_ORDER, summaryItems, ""));
  }

  private SummaryItem getProbabilityDistributionsSummary(SubmissionDetail submissionDetail) {
    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD
    );

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, null
        );

    return SummaryItem.withCards(FIELD_TRAP_PROPERTIES, List.of(
        getPropertiesSummary(submissionDetail),
        SummaryCard.probabilitySummaryCard(listOfSummaryViews)));
  }

  private SummaryCard getPropertiesSummary(SubmissionDetail submissionDetail) {
    var trapProperty = trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .orElse(new TrapProperty());

    var trapTypeDisplayName = trapProperty.getTrapType() == null ? "" : trapProperty.getTrapType().getDisplayName();
    var trapType = TrapType.OTHER.equals(trapProperty.getTrapType())
        ? trapProperty.getOtherTrapType()
        : trapTypeDisplayName;

    var summaryDataView = SummaryDataView.newWithKeyValue(TRAP_TYPE, trapType);
    summaryDataView.addKeyValue(CLOSURE_MECHANISMS, trapProperty.getClosureMechanisms());
    summaryDataView.addKeyValue(VERTICALLY_SEPARATE_HC_POOLS, trapProperty.getVerticallySeparateHcPools());
    summaryDataView.addKeyValue(STRUCTURAL_COMPARTMENTS, trapProperty.getStructuralCompartments());
    summaryDataView.addKeyValue(ORIGINAL_HYDROCARBON_COLUMN_HEIGHT,
        NumberUtil.getNumberStringWithUnitsOrEmpty(trapProperty.getOriginalHydrocarbonColumnHeight(), "m"));

    return SummaryCard.simpleSummaryCard(summaryDataView);
  }
}
