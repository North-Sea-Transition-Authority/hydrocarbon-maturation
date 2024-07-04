package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

@ExtendWith(MockitoExtension.class)
class FdpaPromotedVolumesSummaryServiceTest {

  @Mock
  private FdpaPromotedVolumesRepository fdpaPromotedVolumesRepository;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private FdpaPromotedVolumesSummaryService summaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_FDPA);
  }

  @Nested
  class getGeneralInformationSummaryCard {

    private static final String COMMENT = "comment";

    @Test
    void fdpaPromotedVolumeExists() {
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(getFdpaPromotedVolume()));

      var generatedCard = summaryService.getGeneralInformationSummaryCard(submissionDetail);
      assertThat(generatedCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .containsExactly(
              FdpaPromotedVolumesSummaryService.GENERAL_INFORMATION_TITLE,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var data = (SummaryDataView) generatedCard.summaryData();
      assertThat(data.keyValues()).containsExactly(
          new SummaryKeyValue(
              FdpaPromotedVolumesSummaryService.HAS_PROMOTED_RESOURCES,
              "Yes"
          ),
          new SummaryKeyValue(
              FdpaPromotedVolumesSummaryService.REASON_PROMOTED_RESOURCES,
              COMMENT
          )
      );
    }

    @Test
    void fdpaPromotedVolumeDoesNotExist() {
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.empty());

      var generatedCard = summaryService.getGeneralInformationSummaryCard(submissionDetail);
      assertThat(generatedCard)
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType
          )
          .containsExactly(
              FdpaPromotedVolumesSummaryService.GENERAL_INFORMATION_TITLE,
              SummaryCardType.SIMPLE_SUMMARY
          );

      var data = (SummaryDataView) generatedCard.summaryData();
      assertThat(data.keyValues()).containsExactly(
          new SummaryKeyValue(
              FdpaPromotedVolumesSummaryService.HAS_PROMOTED_RESOURCES,
              ""
          ),
          new SummaryKeyValue(
              FdpaPromotedVolumesSummaryService.REASON_PROMOTED_RESOURCES,
              ""
          )
      );
    }

    private FdpaPromotedVolume getFdpaPromotedVolume() {
      var fdpaPromotedVolume = new FdpaPromotedVolume();
      fdpaPromotedVolume.setResourcesAreBeingPromoted(Boolean.TRUE);
      fdpaPromotedVolume.setComment(COMMENT);
      return fdpaPromotedVolume;
    }
  }

  @Nested
  class getPromotedResourcesVolumetricsCard {

    private static final List<ProbabilityDistributionFieldInstance> ALL_INSTANCES = List.of(
        ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    private static final List<ProbabilityDistributionFieldInstance> INSTANCES_EXCLUDING_OIL = List.of(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
    );

    private static final ProbabilityDistributionTableView PROBABILITY_DISTRIBUTION_TABLE_VIEW
        = ProbabilityDistributionTableView.empty();

    @Test
    void whenOilCase_includesOilInstance() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(true);
      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          ALL_INSTANCES, submissionDetail, null))
          .thenReturn(PROBABILITY_DISTRIBUTION_TABLE_VIEW);

      assertThat(summaryService.getPromotedResourcesVolumetricsCard(submissionDetail))
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType,
              SummaryCard::summaryData
          )
          .containsExactly(
              FdpaPromotedVolumesSummaryService.VOLUMETRICS_TITLE,
              SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
              PROBABILITY_DISTRIBUTION_TABLE_VIEW
          );
    }

    @Test
    void whenNotOilCase_excludesOilInstance() {
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
          .thenReturn(false);
      when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
          INSTANCES_EXCLUDING_OIL, submissionDetail, null))
          .thenReturn(PROBABILITY_DISTRIBUTION_TABLE_VIEW);

      assertThat(summaryService.getPromotedResourcesVolumetricsCard(submissionDetail))
          .extracting(
              SummaryCard::displayName,
              SummaryCard::summaryCardType,
              SummaryCard::summaryData
          )
          .containsExactly(
              FdpaPromotedVolumesSummaryService.VOLUMETRICS_TITLE,
              SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
              PROBABILITY_DISTRIBUTION_TABLE_VIEW
          );
    }
  }
}