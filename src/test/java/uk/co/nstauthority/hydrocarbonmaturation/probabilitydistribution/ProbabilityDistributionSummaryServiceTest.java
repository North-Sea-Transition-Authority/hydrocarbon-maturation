package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionSummaryServiceTest {

  private static final String COMMENTS = "Comments";

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Nested
  class getSummaryBehaviour {

    @Test
    void whenNoInstances() {
      List<ProbabilityDistributionFieldInstance> instances = List.of();

      assertThat(probabilityDistributionSummaryService.getSummaryBehaviour(instances))
          .usingRecursiveComparison()
          .isEqualTo(new SummaryBehaviour());
    }

    @Test
    void whenInstances_thenCombinesBehaviour() {
      var instances = List.of(
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
      );

      var expectedSummaryBehaviour = SummaryBehaviour.builder()
          .with1C()
          .with2C()
          .with3C()
          .withMean()
          .build();

      assertThat(probabilityDistributionSummaryService.getSummaryBehaviour(instances))
          .usingRecursiveComparison()
          .isEqualTo(expectedSummaryBehaviour);
    }
  }

  @Nested
  class getProbabilityDistributionTableViewFromInstancesAndSubmission {

    @Test
    void whenNoInstances() {
      List<ProbabilityDistributionFieldInstance> instances = List.of();

      assertThat(probabilityDistributionSummaryService.getSummaryBehaviour(instances))
          .usingRecursiveComparison()
          .isEqualTo(new SummaryBehaviour());
    }

    @Nested
    class WhenSubmissionDetail {

      @Test
      void whenEmptySummary() {
        var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

        // WHEN the probability distribution is empty
        when(probabilityDistributionPersistenceService.getProbabilityDistribution(
            submissionDetail, ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT))
            .thenReturn(Optional.empty());

        var generatedTableView = probabilityDistributionSummaryService
            .getProbabilityDistributionTableViewFromInstancesAndSubmission(
                List.of(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT), submissionDetail, null
            );

        // THEN the behaviours of all preset types are combined
        var expectedSummaryBehaviour = SummaryBehaviour.builder()
            .withConstant()
            .withMax()
            .withMean()
            .withMin()
            .withMode()
            .withP10()
            .withP1()
            .withP50()
            .withP90()
            .withP99()
            .build();

        // AND the summary view is empty excluding distribution name
        var expectedSummaryView = new ProbabilityDistributionSummaryView(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getDisplayName(),
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
        );

        assertThat(generatedTableView)
            .usingRecursiveComparison()
            .isEqualTo(new ProbabilityDistributionTableView(
                expectedSummaryBehaviour,
                List.of(expectedSummaryView)
            ));
      }

      @Test
      void whenFullSummary() {
        var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

        var distributionType = ProbabilityDistributionType.LOGNORMAL;

        var productiveAreaProspect = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
        var productiveAreaDistribution = getFullProbabilityDistribution(productiveAreaProspect, distributionType);

        var increasedVolumesFdpa = ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA;
        var increasedVolumesDistribution = getFullProbabilityDistribution(increasedVolumesFdpa, distributionType);

        when(probabilityDistributionPersistenceService.getProbabilityDistribution(
            submissionDetail, productiveAreaProspect))
            .thenReturn(Optional.of(productiveAreaDistribution));
        when(probabilityDistributionPersistenceService.getProbabilityDistribution(
            submissionDetail, increasedVolumesFdpa))
            .thenReturn(Optional.of(increasedVolumesDistribution));

        var generatedTableView = probabilityDistributionSummaryService
            .getProbabilityDistributionTableViewFromInstancesAndSubmission(
                List.of(productiveAreaProspect, increasedVolumesFdpa), submissionDetail, null
            );

        // THEN the behaviours of all preset types are combined
        var expectedSummaryBehaviour = SummaryBehaviour.builder()
            .withConstant()
            .withMax()
            .withMean()
            .withMin()
            .withMode()
            .withP10()
            .withP1()
            .withP50()
            .withP90()
            .withP99()
            .with1P()
            .with2P()
            .with3P()
            .build();

        // AND the summary view contains distribution name, type, units AND values
        var expectedProductiveAreaView = new ProbabilityDistributionSummaryView(
            productiveAreaProspect.getDisplayName(),
            distributionType.getDisplayName(),
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            productiveAreaProspect.getUnits(),
            "14",
            "15",
            "16",
            COMMENTS
        );

        var expectedIncreasedVolumesView = new ProbabilityDistributionSummaryView(
            increasedVolumesFdpa.getDisplayName(),
            distributionType.getDisplayName(),
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            increasedVolumesFdpa.getUnits(),
            "14",
            "15",
            "16",
            COMMENTS
        );

        assertThat(generatedTableView)
            .usingRecursiveComparison()
            .isEqualTo(new ProbabilityDistributionTableView(
                expectedSummaryBehaviour,
                List.of(expectedProductiveAreaView, expectedIncreasedVolumesView)
            ));
      }

      @Test
      void whenOtherDistributionType() {
        var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

        var probabilityDistribution = new ProbabilityDistribution();
        var otherDistributionType = "Other";
        probabilityDistribution.setDistributionType(null);
        probabilityDistribution.setOtherDistributionType(otherDistributionType);
        probabilityDistribution.setInstance(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT);

        when(probabilityDistributionPersistenceService.getProbabilityDistribution(
            submissionDetail, ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT))
            .thenReturn(Optional.of(probabilityDistribution));

        var generatedTableView = probabilityDistributionSummaryService
            .getProbabilityDistributionTableViewFromInstancesAndSubmission(
                List.of(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT), submissionDetail, null
            );

        // THEN the behaviours of all preset types are combined
        var expectedSummaryBehaviour = SummaryBehaviour.builder()
            .withConstant()
            .withMax()
            .withMean()
            .withMin()
            .withMode()
            .withP10()
            .withP1()
            .withP50()
            .withP90()
            .withP99()
            .build();

        // AND the summary view is empty excluding distribution name, type and units
        var expectedSummaryView = new ProbabilityDistributionSummaryView(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getDisplayName(),
            otherDistributionType,
            "", "", "", "", "", "", "", "", "", "", "", "", "",
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getUnits(),
            "", "", "", ""
        );

        assertThat(generatedTableView)
            .usingRecursiveComparison()
            .isEqualTo(new ProbabilityDistributionTableView(
                expectedSummaryBehaviour,
                List.of(expectedSummaryView)
            ));
      }
    }

    @Nested
    class WhenReservoirSubmissionDetail {
      @Test
      void whenEmptySummary() {
        var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
        var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

        // WHEN the probability distribution has no values
        var probabilityDistribution = new ProbabilityDistribution();
        probabilityDistribution.setDistributionType(ProbabilityDistributionType.LOGNORMAL);
        // AND the instance has multiple preset types
        probabilityDistribution.setInstance(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT);

        when(probabilityDistributionPersistenceService.getProbabilityDistribution(
            reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT))
            .thenReturn(Optional.of(probabilityDistribution));

        var generatedTableView = probabilityDistributionSummaryService
            .getProbabilityDistributionTableViewFromInstancesAndSubmission(
                List.of(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT),
                submissionDetail,
                reservoirSubmissionDetail
            );

        // THEN the behaviours of all preset types are combined
        var expectedSummaryBehaviour = SummaryBehaviour.builder()
            .withConstant()
            .withMax()
            .withMean()
            .withMin()
            .withMode()
            .withP10()
            .withP1()
            .withP50()
            .withP90()
            .withP99()
            .build();

        // AND the summary view is empty excluding distribution name, type and units
        var expectedSummaryView = new ProbabilityDistributionSummaryView(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getDisplayName(),
            ProbabilityDistributionType.LOGNORMAL.getDisplayName(),
            "", "", "", "", "", "", "", "", "", "", "", "", "",
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getUnits(),
            "", "", "", ""
        );

        assertThat(generatedTableView)
            .usingRecursiveComparison()
            .isEqualTo(new ProbabilityDistributionTableView(
                expectedSummaryBehaviour,
                List.of(expectedSummaryView)
            ));
      }

      @Test
      void whenFullSummary() {
        var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
        var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

        var instance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT;
        var distributionType = ProbabilityDistributionType.LOGNORMAL;
        var probabilityDistribution = getFullProbabilityDistribution(instance, distributionType);

        when(probabilityDistributionPersistenceService.getProbabilityDistribution(
            reservoirSubmissionDetail, instance))
            .thenReturn(Optional.of(probabilityDistribution));

        var generatedTableView = probabilityDistributionSummaryService
            .getProbabilityDistributionTableViewFromInstancesAndSubmission(
                List.of(instance), submissionDetail, reservoirSubmissionDetail
            );

        // THEN the behaviours of all preset types are combined
        var expectedSummaryBehaviour = SummaryBehaviour.builder()
            .withConstant()
            .withMax()
            .withMean()
            .withMin()
            .withMode()
            .withP10()
            .withP1()
            .withP50()
            .withP90()
            .withP99()
            .build();

        // AND the summary view contains distribution name, type, units AND values
        var expectedSummaryView = new ProbabilityDistributionSummaryView(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getDisplayName(),
            ProbabilityDistributionType.LOGNORMAL.getDisplayName(),
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getUnits(),
            "14",
            "15",
            "16",
            COMMENTS
        );

        assertThat(generatedTableView)
            .usingRecursiveComparison()
            .isEqualTo(new ProbabilityDistributionTableView(
                expectedSummaryBehaviour,
                List.of(expectedSummaryView)
            ));
      }

      @Test
      void whenOtherDistributionType() {
        var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
        var reservoirSubmissionDetail = new ReservoirSubmissionDetail();

        var probabilityDistribution = new ProbabilityDistribution();
        var otherDistributionType = "Other";
        probabilityDistribution.setDistributionType(null);
        probabilityDistribution.setOtherDistributionType(otherDistributionType);
        probabilityDistribution.setInstance(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT);

        when(probabilityDistributionPersistenceService.getProbabilityDistribution(
            reservoirSubmissionDetail, ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT))
            .thenReturn(Optional.of(probabilityDistribution));

        var generatedTableView = probabilityDistributionSummaryService
            .getProbabilityDistributionTableViewFromInstancesAndSubmission(
                List.of(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT),
                submissionDetail,
                reservoirSubmissionDetail
            );

        // THEN the behaviours of all preset types are combined
        var expectedSummaryBehaviour = SummaryBehaviour.builder()
            .withConstant()
            .withMax()
            .withMean()
            .withMin()
            .withMode()
            .withP10()
            .withP1()
            .withP50()
            .withP90()
            .withP99()
            .build();

        // AND the summary view is empty excluding distribution name, type and units
        var expectedSummaryView = new ProbabilityDistributionSummaryView(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getDisplayName(),
            otherDistributionType,
            "", "", "", "", "", "", "", "", "", "", "", "", "",
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getUnits(),
            "", "", "", ""
        );

        assertThat(generatedTableView)
            .usingRecursiveComparison()
            .isEqualTo(new ProbabilityDistributionTableView(
                expectedSummaryBehaviour,
                List.of(expectedSummaryView)
            ));
      }
    }
  }

  private ProbabilityDistribution getFullProbabilityDistribution(ProbabilityDistributionFieldInstance instance,
                                                                 ProbabilityDistributionType distributionType) {
    var probabilityDistribution = new ProbabilityDistribution();
    probabilityDistribution.setDistributionType(distributionType);
    probabilityDistribution.setInstance(instance);
    probabilityDistribution.setMinValue(BigDecimal.ONE);
    probabilityDistribution.setP99(BigDecimal.valueOf(2));
    probabilityDistribution.setP90(BigDecimal.valueOf(3));
    probabilityDistribution.setOneC(BigDecimal.valueOf(4));
    probabilityDistribution.setModeValue(BigDecimal.valueOf(5));
    probabilityDistribution.setP50(BigDecimal.valueOf(6));
    probabilityDistribution.setTwoC(BigDecimal.valueOf(7));
    probabilityDistribution.setMeanValue(BigDecimal.valueOf(8));
    probabilityDistribution.setP10(BigDecimal.valueOf(9));
    probabilityDistribution.setThreeC(BigDecimal.TEN);
    probabilityDistribution.setP1(BigDecimal.valueOf(11));
    probabilityDistribution.setMostLikelyValue(BigDecimal.valueOf(12));
    probabilityDistribution.setMaxValue(BigDecimal.valueOf(13));
    probabilityDistribution.setOneP(BigDecimal.valueOf(14));
    probabilityDistribution.setTwoP(BigDecimal.valueOf(15));
    probabilityDistribution.setThreeP(BigDecimal.valueOf(16));
    probabilityDistribution.setComments(COMMENTS);
    return probabilityDistribution;
  }
}