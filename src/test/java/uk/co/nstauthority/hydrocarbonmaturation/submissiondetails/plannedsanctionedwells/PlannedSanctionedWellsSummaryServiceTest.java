package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

@ExtendWith(MockitoExtension.class)
class PlannedSanctionedWellsSummaryServiceTest {
  @Mock
  private PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  @Mock
  private PswFaultRepository pswFaultRepository;
  @Mock
  private HoleCompletionTypesRepository holeCompletionTypesRepository;
  @Mock
  private PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  @Mock
  private ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;
  @InjectMocks
  private PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void getPlannedSanctionedWellsCard_noExisting() {
    when(plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail);
    assertThat(summaryCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::displayName
        ).containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            PlannedSanctionedWellsSummaryService.PLANNED_SANCTIONED_WELLS
        );

    var summaryDataView = ((SummaryDataView) summaryCard.summaryData()).keyValues();
    assertThat(summaryDataView)
        .extracting(SummaryKeyValue::key)
        .containsExactly(
            PlannedSanctionedWellsSummaryService.TOTAL_CAPEX,
            PlannedSanctionedWellsSummaryService.TOTAL_NUMBER_OF_PLANNED_SANCTIONED_WELLS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_INJECTORS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_PRODUCERS,
            PlannedSanctionedWellsSummaryService.NUMBER_OF_MONITORS,
            PlannedSanctionedWellsSummaryService.ANY_HORIZONTAL_WELLS,
            PlannedSanctionedWellsSummaryService.ANY_HIGH_PRESSURE_TEMPERATURE_WELLS,
            PlannedSanctionedWellsSummaryService.ANY_WELLS_WITHIN_NARROW_OR_TIGHT_PPFG_WINDOW,
            PlannedSanctionedWellsSummaryService.ANY_WELLBORE_STABILITY_CHALLENGES,
            PlannedSanctionedWellsSummaryService.EXPECTED_FAULTS,
            PlannedSanctionedWellsSummaryService.ANY_WELLS_EXPECTED_TO_CROSS_FAULTS,
            PlannedSanctionedWellsSummaryService.ANY_EXPECTED_LOSSES_FROM_DRILLING,
            PlannedSanctionedWellsSummaryService.ANY_EXPECTED_DRILLING_CHALLENGES,
            PlannedSanctionedWellsSummaryService.STIMULATION_TYPE_REQUIRED,
            PlannedSanctionedWellsSummaryService.IS_EXPECTING_FORMATION_DAMAGE,
            PlannedSanctionedWellsSummaryService.ANY_HEAVY_OIL,
            PlannedSanctionedWellsSummaryService.ANY_SPECIALISED_DRILLING_EQUIPMENT_REQUIRED
        );

    assertThat(summaryDataView)
        .extracting(SummaryKeyValue::value)
        .containsOnly("");
  }
}