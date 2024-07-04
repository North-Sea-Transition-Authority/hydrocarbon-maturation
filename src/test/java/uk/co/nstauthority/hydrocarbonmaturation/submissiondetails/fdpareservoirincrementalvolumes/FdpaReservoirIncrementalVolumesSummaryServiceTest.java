package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class FdpaReservoirIncrementalVolumesSummaryServiceTest {

  static final String COMMENT = "SOME COMMENT";
  static final ProbabilityDistributionTableView TABLE_VIEW = ProbabilityDistributionTableView.empty();

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Mock
  private FdpaReservoirIncrementalVolumesRepository incrementalVolumesRepository;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private FdpaReservoirIncrementalVolumesSummaryService summaryService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_FDPA);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = "CREATE_FDPA", mode = EnumSource.Mode.EXCLUDE)
  void getSummarySection_notFdpa_thenEmpty(SubmissionType submissionType) {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        submissionType);

    assertThat(summaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_oilCase() {
    var incrementalVolume = new FdpaReservoirIncrementalVolume();
    incrementalVolume.setChangeToInPlaceVolumes(true);
    incrementalVolume.setComment(COMMENT);

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
        .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);
    when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(incrementalVolume));
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA),
        reservoirSubmissionDetail.getSubmissionDetail(), reservoirSubmissionDetail))
        .thenReturn(TABLE_VIEW);

    var summarySection = summaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection)
        .isNotEmpty()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        )
        .containsExactly(
            FdpaReservoirIncrementalVolumesSummaryService.FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    var summaryItems = summarySection.get()
        .summaryItems();
    assertThat(summaryItems)
        .hasSize(1);

    var summaryItem = summaryItems.get(0);
    assertThat(summaryItem.displayName())
        .isEqualTo(FdpaReservoirIncrementalVolumesSummaryService.SUMMARY_HEADING);
    assertThat(summaryItem.summaryCards())
        .hasSize(2);

    var expectedGeneralDataView = SummaryDataView
        .newWithKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.CHANGE_TO_IN_PLACE_QUESTION,
            "Yes"
        )
        .addKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.REASON_FOR_CHANGE,
            COMMENT
        );
    var generalInformationCard = summaryItem.summaryCards().get(0);
    assertThat(generalInformationCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            expectedGeneralDataView
        );

    var probabilityCard = summaryItem.summaryCards().get(1);
    assertThat(probabilityCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            TABLE_VIEW
        );
  }

  @Test
  void getSummarySection_gasCase() {
    var incrementalVolume = new FdpaReservoirIncrementalVolume();
    incrementalVolume.setChangeToInPlaceVolumes(true);
    incrementalVolume.setComment(COMMENT);

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
    when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(incrementalVolume));
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA),
        reservoirSubmissionDetail.getSubmissionDetail(), reservoirSubmissionDetail))
        .thenReturn(TABLE_VIEW);

    var summarySection = summaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection)
        .isNotEmpty()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        )
        .containsExactly(
            FdpaReservoirIncrementalVolumesSummaryService.FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    var summaryItems = summarySection.get()
        .summaryItems();
    assertThat(summaryItems)
        .hasSize(1);

    var summaryItem = summaryItems.get(0);
    assertThat(summaryItem.displayName())
        .isEqualTo(FdpaReservoirIncrementalVolumesSummaryService.SUMMARY_HEADING);
    assertThat(summaryItem.summaryCards())
        .hasSize(2);

    var expectedGeneralDataView = SummaryDataView
        .newWithKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.CHANGE_TO_IN_PLACE_QUESTION,
            "Yes"
        )
        .addKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.REASON_FOR_CHANGE,
            COMMENT
        );
    var generalInformationCard = summaryItem.summaryCards().get(0);
    assertThat(generalInformationCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            expectedGeneralDataView
        );

    var probabilityCard = summaryItem.summaryCards().get(1);
    assertThat(probabilityCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            TABLE_VIEW
        );
  }

  @Test
  void getSummarySection_combinedCase() {
    var incrementalVolume = new FdpaReservoirIncrementalVolume();
    incrementalVolume.setChangeToInPlaceVolumes(true);
    incrementalVolume.setComment(COMMENT);

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
        .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(incrementalVolume));
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(
            ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
            ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
        ),
        reservoirSubmissionDetail.getSubmissionDetail(), reservoirSubmissionDetail))
        .thenReturn(TABLE_VIEW);

    var summarySection = summaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection)
        .isNotEmpty()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        )
        .containsExactly(
            FdpaReservoirIncrementalVolumesSummaryService.FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    var summaryItems = summarySection.get()
        .summaryItems();
    assertThat(summaryItems)
        .hasSize(1);

    var summaryItem = summaryItems.get(0);
    assertThat(summaryItem.displayName())
        .isEqualTo(FdpaReservoirIncrementalVolumesSummaryService.SUMMARY_HEADING);
    assertThat(summaryItem.summaryCards())
        .hasSize(2);

    var expectedGeneralDataView = SummaryDataView
        .newWithKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.CHANGE_TO_IN_PLACE_QUESTION,
            "Yes"
        )
        .addKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.REASON_FOR_CHANGE,
            COMMENT
        );
    var generalInformationCard = summaryItem.summaryCards().get(0);
    assertThat(generalInformationCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            expectedGeneralDataView
        );

    var probabilityCard = summaryItem.summaryCards().get(1);
    assertThat(probabilityCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            TABLE_VIEW
        );
  }

  @Test
  void getSummarySection_noChangeToInPlaceVolumes() {
    var incrementalVolume = new FdpaReservoirIncrementalVolume();
    incrementalVolume.setChangeToInPlaceVolumes(false);
    incrementalVolume.setComment(COMMENT);

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
        .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);
    when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(incrementalVolume));

    var summarySection = summaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection)
        .isNotEmpty()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        )
        .containsExactly(
            FdpaReservoirIncrementalVolumesSummaryService.FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    var summaryItems = summarySection.get()
        .summaryItems();
    assertThat(summaryItems)
        .hasSize(1);

    var summaryItem = summaryItems.get(0);
    assertThat(summaryItem.displayName())
        .isEqualTo(FdpaReservoirIncrementalVolumesSummaryService.SUMMARY_HEADING);
    assertThat(summaryItem.summaryCards())
        .hasSize(1);

    var expectedGeneralDataView = SummaryDataView
        .newWithKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.CHANGE_TO_IN_PLACE_QUESTION,
            "No"
        )
        .addKeyValue(
            FdpaReservoirIncrementalVolumesSummaryService.REASON_FOR_CHANGE,
            COMMENT
        );
    var generalInformationCard = summaryItem.summaryCards().get(0);
    assertThat(generalInformationCard)
        .extracting(
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            SummaryCardType.SIMPLE_SUMMARY,
            expectedGeneralDataView
        );
  }
}