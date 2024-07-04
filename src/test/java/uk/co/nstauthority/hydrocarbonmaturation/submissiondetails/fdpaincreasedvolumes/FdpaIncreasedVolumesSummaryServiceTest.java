package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

@ExtendWith(MockitoExtension.class)
class FdpaIncreasedVolumesSummaryServiceTest {

  @Mock
  private FdpaIncreasedVolumesRepository fdpaIncreasedVolumesRepository;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private FdpaIncreasedVolumesSummaryService summaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_FDPA);
  }

  @Nested
  class getGeneralInformationSummaryCard {

    private static final String COMMENT = "comment";

    @Test
    void fdpaIncreasedVolumeExists() {
      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(getFdpaIncreasedVolume()));

      var generatedCard = summaryService.getGeneralInformationSummaryCard(submissionDetail);
      assertThat(generatedCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .containsExactly(
              FdpaIncreasedVolumesSummaryService.GENERAL_INFORMATION_TITLE,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var data = (SummaryDataView) generatedCard.summaryData();
      assertThat(data.keyValues()).containsExactly(
          new SummaryKeyValue(
              FdpaIncreasedVolumesSummaryService.CHANGE_IN_VOLUMETRICS,
              "Yes"
          ),
          new SummaryKeyValue(
              FdpaIncreasedVolumesSummaryService.REASON_CHANGE_IN_VOLUMETRICS,
              COMMENT
          )
      );
    }

    @Test
    void fdpaIncreasedVolumeDoesNotExist() {
      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.empty());

      var generatedCard = summaryService.getGeneralInformationSummaryCard(submissionDetail);
      assertThat(generatedCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .containsExactly(
              FdpaIncreasedVolumesSummaryService.GENERAL_INFORMATION_TITLE,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var data = (SummaryDataView) generatedCard.summaryData();
      assertThat(data.keyValues()).containsExactly(
          new SummaryKeyValue(
              FdpaIncreasedVolumesSummaryService.CHANGE_IN_VOLUMETRICS,
              ""
          ),
          new SummaryKeyValue(
              FdpaIncreasedVolumesSummaryService.REASON_CHANGE_IN_VOLUMETRICS,
              ""
          )
      );
    }

    private FdpaIncreasedVolume getFdpaIncreasedVolume() {
      var fdpaIncreasedVolume = new FdpaIncreasedVolume();
      fdpaIncreasedVolume.setChangeInVolumetrics(Boolean.TRUE);
      fdpaIncreasedVolume.setComment(COMMENT);
      return fdpaIncreasedVolume;
    }
  }

  @Nested
  class getVolumetricsSummaryCards {

    private static final List<ProbabilityDistributionFieldInstance> OIL_PROBABILITY_INSTANCES = List.of(
        ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    );

    private static final List<ProbabilityDistributionFieldInstance> GAS_PROBABILITY_INSTANCES = List.of(
        ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA
    );

    private static final List<ProbabilityDistributionFieldInstance> OIL_OR_COMBINED_FIELD_PROBABILITY_INSTANCES = List.of(
        ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA
    );

    private static final List<ProbabilityDistributionFieldInstance> SHARED_FIELD_PROBABILITY_INSTANCES = List.of(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA
    );

    private static final ProbabilityDistributionTableView PROBABILITY_DISTRIBUTION_TABLE_VIEW
        = ProbabilityDistributionTableView.empty();

    private static final ProbabilityDistributionTableView FIELD_PROBABILITY_DISTRIBUTION_TABLE_VIEW
        = ProbabilityDistributionTableView.empty();


    @Test
    void whenOilCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
          .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);
      mockOilProbabilityDistributionSummaryViews();

      var generatedSummaryCards = summaryService.getVolumetricsSummaryCards(submissionDetail);
      assertGeneratedSummaryCards(generatedSummaryCards);
    }

    @Test
    void whenGasCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
          .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
      mockGasProbabilityDistributionSummaryViews();

      var generatedSummaryCards = summaryService.getVolumetricsSummaryCards(submissionDetail);
      assertGeneratedSummaryCards(generatedSummaryCards);
    }

    @Test
    void whenCombinedCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
          .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
      mockCombinedProbabilityDistributionSummaryViews();

      var generatedSummaryCards = summaryService.getVolumetricsSummaryCards(submissionDetail);
      assertGeneratedSummaryCards(generatedSummaryCards);
    }

    private void assertGeneratedSummaryCards(List<SummaryCard> generatedSummaryCards) {
      assertThat(generatedSummaryCards).hasSize(2);

      var generatedSummaryCardsIterator = generatedSummaryCards.iterator();

      var probabilityCard = generatedSummaryCardsIterator.next();
      assertThat(probabilityCard)
          .extracting(
              SummaryCard::summaryCardType,
              SummaryCard::summaryData
          )
          .containsExactly(
              SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
              PROBABILITY_DISTRIBUTION_TABLE_VIEW
          );

      var fieldProbabilityCard = generatedSummaryCardsIterator.next();
      assertThat(fieldProbabilityCard)
          .extracting(
              SummaryCard::summaryCardType,
              SummaryCard::summaryData
          )
          .containsExactly(
              SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
              FIELD_PROBABILITY_DISTRIBUTION_TABLE_VIEW
          );
    }

    private void mockOilProbabilityDistributionSummaryViews() {
      ArrayList<ProbabilityDistributionFieldInstance> oilCaseFieldInstances = new ArrayList<>();
      oilCaseFieldInstances.addAll(OIL_OR_COMBINED_FIELD_PROBABILITY_INSTANCES);
      oilCaseFieldInstances.addAll(SHARED_FIELD_PROBABILITY_INSTANCES);

      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          OIL_PROBABILITY_INSTANCES, submissionDetail, null))
          .thenReturn(PROBABILITY_DISTRIBUTION_TABLE_VIEW);
      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          oilCaseFieldInstances, submissionDetail, null))
          .thenReturn(FIELD_PROBABILITY_DISTRIBUTION_TABLE_VIEW);
    }

    private void mockGasProbabilityDistributionSummaryViews() {
      ArrayList<ProbabilityDistributionFieldInstance> gasCaseFieldInstances = new ArrayList<>(
          SHARED_FIELD_PROBABILITY_INSTANCES);

      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          GAS_PROBABILITY_INSTANCES, submissionDetail, null))
          .thenReturn(PROBABILITY_DISTRIBUTION_TABLE_VIEW);
      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          gasCaseFieldInstances, submissionDetail, null))
          .thenReturn(FIELD_PROBABILITY_DISTRIBUTION_TABLE_VIEW);
    }

    private void mockCombinedProbabilityDistributionSummaryViews() {
      ArrayList<ProbabilityDistributionFieldInstance> combinedProbabilityInstances = new ArrayList<>();
      combinedProbabilityInstances.addAll(OIL_PROBABILITY_INSTANCES);
      combinedProbabilityInstances.addAll(GAS_PROBABILITY_INSTANCES);

      ArrayList<ProbabilityDistributionFieldInstance> combinedFieldProbabilityInstances = new ArrayList<>();
      combinedFieldProbabilityInstances.addAll(OIL_OR_COMBINED_FIELD_PROBABILITY_INSTANCES);
      combinedFieldProbabilityInstances.addAll(SHARED_FIELD_PROBABILITY_INSTANCES);

      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          combinedProbabilityInstances, submissionDetail, null))
          .thenReturn(PROBABILITY_DISTRIBUTION_TABLE_VIEW);
      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          combinedFieldProbabilityInstances, submissionDetail, null))
          .thenReturn(FIELD_PROBABILITY_DISTRIBUTION_TABLE_VIEW);
    }
  }
}