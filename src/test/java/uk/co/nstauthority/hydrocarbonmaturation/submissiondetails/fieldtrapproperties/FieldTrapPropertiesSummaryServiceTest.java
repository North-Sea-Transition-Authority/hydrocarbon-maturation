package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.CLOSURE_MECHANISMS;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService.ORIGINAL_HYDROCARBON_COLUMN_HEIGHT;
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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
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
  void getSummarySection_notFieldSubmission_thenEmpty() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    assertThat(fieldTrapPropertiesSummaryService.getSummarySection(discoverySubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_assertStructure() {
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.empty());

    var summarySectionOptional = fieldTrapPropertiesSummaryService.getSummarySection(submissionDetail);

    assertThat(summarySectionOptional)
        .isPresent();

    assertThat(summarySectionOptional.get())
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText)
        .containsExactly(
            FieldTrapPropertiesSummaryService.FIELD_TRAP_PROPERTIES_ORDER,
            ""
        );

    var summaryItems = summarySectionOptional.get().summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryItem = summaryItems.get(0);
    assertThat(summaryItem.displayName()).isEqualTo(FieldTrapPropertiesSummaryService.FIELD_TRAP_PROPERTIES);

    // regular fields summary
    var summaryCard1 = summaryItem.summaryCards().get(0);
    assertThat(summaryCard1.summaryCardType()).isEqualTo(SummaryCardType.SIMPLE_SUMMARY);

    // probability distribution summaries
    var summaryCard2 = summaryItem.summaryCards().get(1);
    assertThat(summaryCard2.summaryCardType()).isEqualTo(SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY);
  }

  @Test
  void getSummary_assertProbabilityDistributions() {
    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD
    );

    var summaryViews = List.of(ProbabilityDistributionSummaryView.empty("Test"));
    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, null)
    ).thenReturn(summaryViews);

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.empty());

    var probabilityCard = fieldTrapPropertiesSummaryService.getSummarySection(submissionDetail)
        .get()
        .summaryItems()
        .get(0)
        .summaryCards()
        .get(1);
    assertThat(probabilityCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            summaryViews
        );

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, null);
    verifyNoMoreInteractions(probabilityDistributionSummaryService);

  }

  @Test
  void getSummarySection_selectTrapType() {
    var trapProperty = createTrapProperty();

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    SummaryDataView propertiesCardData = (SummaryDataView) fieldTrapPropertiesSummaryService.getSummarySection(submissionDetail)
        .get()
        .summaryItems()
        .get(0)
        .summaryCards()
        .get(0)
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
  void getSummarySection_otherTrapType() {
    var trapProperty = createTrapProperty();
    trapProperty.setTrapType(TrapType.OTHER);

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    SummaryDataView propertiesCardData = (SummaryDataView) fieldTrapPropertiesSummaryService.getSummarySection(submissionDetail)
        .get()
        .summaryItems()
        .get(0)
        .summaryCards()
        .get(0)
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
  void getSummarySection_assertProperties() {
    var trapProperty = createTrapProperty();

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    SummaryDataView propertiesCardData = (SummaryDataView) fieldTrapPropertiesSummaryService.getSummarySection(submissionDetail)
        .get()
        .summaryItems()
        .get(0)
        .summaryCards()
        .get(0)
        .summaryData();

    assertThat(propertiesCardData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        ).contains(
            Tuple.tuple(CLOSURE_MECHANISMS, trapProperty.getClosureMechanisms().toString()),
            Tuple.tuple(VERTICALLY_SEPARATE_HC_POOLS, trapProperty.getVerticallySeparateHcPools().toString()),
            Tuple.tuple(STRUCTURAL_COMPARTMENTS, trapProperty.getStructuralCompartments().toString()),
            Tuple.tuple(ORIGINAL_HYDROCARBON_COLUMN_HEIGHT, NumberUtil.getNumberStringWithUnitsOrEmpty(
                trapProperty.getOriginalHydrocarbonColumnHeight(), "m"))
        );
  }

  private TrapProperty createTrapProperty() {
    var trapProperty = new TrapProperty();
    trapProperty.setClosureMechanisms("test mechanisms");
    trapProperty.setTrapType(TrapType.COMBINATION);
    trapProperty.setOtherTrapType("test trap property");

    trapProperty.setVerticallySeparateHcPools(BigDecimal.ONE);
    trapProperty.setStructuralCompartments(BigDecimal.ONE);
    trapProperty.setOriginalHydrocarbonColumnHeight(BigDecimal.ONE);

    return trapProperty;
  }

}