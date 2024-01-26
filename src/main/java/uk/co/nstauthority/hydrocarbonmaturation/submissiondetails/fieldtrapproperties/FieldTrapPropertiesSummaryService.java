package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class FieldTrapPropertiesSummaryService {

  public static final String FIELD_TRAP_PROPERTIES = "Trap properties";
  public static final String FIELD_TRAP_PROPERTIES_PROBABILITIES = "Trap properties - probability distributions";
  public static final String TRAP_TYPE = "Trap type";
  public static final String QUAD_AND_BLOCK_COI_IS_IN = "Quadrant and block the centre of interest is within";
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

  public SummaryCard getFieldTrapPropertiesProbabilitySummaryCard(SubmissionDetail submissionDetail) {
    var trapProperty = trapPropertiesPersistenceService.getTrapProperty(submissionDetail);

    if (trapProperty.isEmpty())  {
      return SummaryCard.emptySummaryCardWithHeading(FIELD_TRAP_PROPERTIES_PROBABILITIES);
    }

    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD
    );

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, null
        );

    return SummaryCard.probabilitySummaryCardWithHeading(FIELD_TRAP_PROPERTIES_PROBABILITIES, listOfSummaryViews);
  }

  public SummaryCard getFieldTrapPropertiesSummaryCard(SubmissionDetail submissionDetail) {
    return trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .map(trapProperty -> {
          var trapTypeDisplayName = trapProperty.getTrapType() == null ? "" : trapProperty.getTrapType().getDisplayName();
          var trapType = TrapType.OTHER.equals(trapProperty.getTrapType())
              ? trapProperty.getOtherTrapType()
              : trapTypeDisplayName;

          var summaryDataView = SummaryDataView.newWithKeyValue(TRAP_TYPE, trapType);
          summaryDataView.addKeyValue(QUAD_AND_BLOCK_COI_IS_IN, trapProperty.getCentreOfInterestQuadrantBlock());
          summaryDataView.addKeyValue(CLOSURE_MECHANISMS, trapProperty.getClosureMechanisms());
          summaryDataView.addKeyValue(VERTICALLY_SEPARATE_HC_POOLS, trapProperty.getVerticallySeparateHcPools());
          summaryDataView.addKeyValue(STRUCTURAL_COMPARTMENTS, trapProperty.getStructuralCompartments());
          summaryDataView.addKeyValue(ORIGINAL_HYDROCARBON_COLUMN_HEIGHT,
              NumberUtil.getNumberStringWithUnitsOrEmpty(trapProperty.getOriginalHydrocarbonColumnHeight(), "m"));

          return SummaryCard.simpleSummaryCardWithHeading(FIELD_TRAP_PROPERTIES, summaryDataView);

        }).orElse(SummaryCard.emptySummaryCardWithHeading(FIELD_TRAP_PROPERTIES));
  }
}
