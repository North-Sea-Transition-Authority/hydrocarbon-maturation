package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.CLOSURE_MECHANISMS;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.FIELD_TRAP_PROPERTIES;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.FIELD_TRAP_PROPERTIES_PROBABILITIES;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.ORIGINAL_HYDROCARBON_COLUMN_HEIGHT;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.QUAD_AND_BLOCK_COI_IS_IN;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.STRUCTURAL_COMPARTMENTS;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.TRAP_TYPE;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.VERTICALLY_SEPARATE_HC_POOLS;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@ExtendWith(MockitoExtension.class)
class FieldTrapPropertiesSummaryServiceTest {

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private FieldTrapPropertiesSummaryService fieldTrapPropertiesSummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void getFieldTrapPropertiesProbabilitySummaryCard_existingProperty_assertDistributions() {
    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD
    );

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, null)
    ).thenReturn(probabilityTableView);

    var trapProperty = new TrapProperty();
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.of(trapProperty));

    var probabilityCard = fieldTrapPropertiesSummaryService.getFieldTrapPropertiesProbabilitySummaryCard(submissionDetail);
    assertThat(probabilityCard)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            FIELD_TRAP_PROPERTIES_PROBABILITIES,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            probabilityTableView
        );

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            listOfInstances, submissionDetail, null);
    verifyNoMoreInteractions(probabilityDistributionSummaryService);

  }

  @Test
  void getFieldTrapPropertiesProbabilitySummaryCard_noExistingProperty_assertEmptySummaryCard() {
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.empty());

    var probabilityCard = fieldTrapPropertiesSummaryService.getFieldTrapPropertiesProbabilitySummaryCard(submissionDetail);
    assertThat(probabilityCard).usingRecursiveComparison()
            .isEqualTo(SummaryCard.emptySummaryCardWithHeading(FIELD_TRAP_PROPERTIES_PROBABILITIES));

    verifyNoInteractions(probabilityDistributionSummaryService);

  }

  @Test
  void getFieldTrapPropertiesSummaryCard_selectTrapType() {
    var trapProperty = createTrapProperty();

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    SummaryDataView propertiesCardData = (SummaryDataView) fieldTrapPropertiesSummaryService
        .getFieldTrapPropertiesSummaryCard(submissionDetail)
        .summaryData();

    assertThat(propertiesCardData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        ).contains(
            Tuple.tuple(TRAP_TYPE, trapProperty.getTrapType().getDisplayName())
        ).doesNotContain(Tuple.tuple(TRAP_TYPE, trapProperty.getOtherTrapType()));
  }

  @Test
  void getFieldTrapPropertiesSummaryCard_otherTrapType() {
    var trapProperty = createTrapProperty();
    trapProperty.setTrapType(TrapType.OTHER);

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    SummaryDataView propertiesCardData = (SummaryDataView) fieldTrapPropertiesSummaryService
        .getFieldTrapPropertiesSummaryCard(submissionDetail)
        .summaryData();

    assertThat(propertiesCardData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        ).contains(
            Tuple.tuple(TRAP_TYPE, trapProperty.getOtherTrapType())
        ).doesNotContain(Tuple.tuple(TRAP_TYPE, trapProperty.getTrapType()));
  }

  @Test
  void getFieldTrapPropertiesSummaryCard_existingTrapProperty_assertProperties() {
    var trapProperty = createTrapProperty();

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    var summaryCard = fieldTrapPropertiesSummaryService
        .getFieldTrapPropertiesSummaryCard(submissionDetail);

    assertThat(summaryCard)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).containsExactly(
            FIELD_TRAP_PROPERTIES,
            SummaryCardType.SIMPLE_SUMMARY
        );

    SummaryDataView propertiesCardData = (SummaryDataView) summaryCard.summaryData();

    assertThat(propertiesCardData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        ).contains(
            Tuple.tuple(QUAD_AND_BLOCK_COI_IS_IN, trapProperty.getCentreOfInterestQuadrantBlock()),
            Tuple.tuple(CLOSURE_MECHANISMS, trapProperty.getClosureMechanisms().toString()),
            Tuple.tuple(VERTICALLY_SEPARATE_HC_POOLS, trapProperty.getVerticallySeparateHcPools().toString()),
            Tuple.tuple(STRUCTURAL_COMPARTMENTS, trapProperty.getStructuralCompartments().toString()),
            Tuple.tuple(ORIGINAL_HYDROCARBON_COLUMN_HEIGHT, NumberUtil.getNumberStringWithUnitsOrEmpty(
                trapProperty.getOriginalHydrocarbonColumnHeight(), "m"))
        );
  }

  @Test
  void getFieldTrapPropertiesSummaryCard_noExistingTrapProperty_assertProperties() {
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = fieldTrapPropertiesSummaryService
        .getFieldTrapPropertiesSummaryCard(submissionDetail);

    assertThat(summaryCard).usingRecursiveComparison()
        .isEqualTo(SummaryCard.emptySummaryCardWithHeading(FIELD_TRAP_PROPERTIES));
  }

  private TrapProperty createTrapProperty() {
    var trapProperty = new TrapProperty();
    trapProperty.setClosureMechanisms("test mechanisms");
    trapProperty.setTrapType(TrapType.COMBINATION);
    trapProperty.setCentreOfInterestQuadrantBlock("1/1");
    trapProperty.setOtherTrapType("test trap property");

    trapProperty.setVerticallySeparateHcPools(BigDecimal.ONE);
    trapProperty.setStructuralCompartments(BigDecimal.ONE);
    trapProperty.setOriginalHydrocarbonColumnHeight(BigDecimal.ONE);

    return trapProperty;
  }

}