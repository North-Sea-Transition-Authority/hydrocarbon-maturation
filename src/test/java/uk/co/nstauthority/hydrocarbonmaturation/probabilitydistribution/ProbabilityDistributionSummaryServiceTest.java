package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionSummaryServiceTest {

  private static final String COMMENTS = "Comments";

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Test
  void getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission_emptySummary() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var probabilityDistribution = new ProbabilityDistribution();
    probabilityDistribution.setDistributionType(ProbabilityDistributionType.LOGNORMAL);
    probabilityDistribution.setInstance(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT);

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(
        submissionDetail, ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT))
        .thenReturn(Optional.of(probabilityDistribution));

    assertThat(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT, submissionDetail))
        .extracting(
            ProbabilityDistributionSummaryView::probabilityDistributionName,
            ProbabilityDistributionSummaryView::probabilityDistributionType,
            ProbabilityDistributionSummaryView::min,
            ProbabilityDistributionSummaryView::p99,
            ProbabilityDistributionSummaryView::p90,
            ProbabilityDistributionSummaryView::mode,
            ProbabilityDistributionSummaryView::p50,
            ProbabilityDistributionSummaryView::meanOrConstant,
            ProbabilityDistributionSummaryView::p10,
            ProbabilityDistributionSummaryView::p1,
            ProbabilityDistributionSummaryView::max,
            ProbabilityDistributionSummaryView::units,
            ProbabilityDistributionSummaryView::comments)
        .contains(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getDisplayName(),
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT.getUnits(),
            "");
  }

  @Test
  void getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission_fullSummary() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var probabilityDistribution = new ProbabilityDistribution();

    probabilityDistribution.setDistributionType(ProbabilityDistributionType.LOGNORMAL);
    probabilityDistribution.setInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT);
    probabilityDistribution.setConstantValue(BigDecimal.ONE);
    probabilityDistribution.setMaxValue(BigDecimal.ONE);
    probabilityDistribution.setMeanValue(BigDecimal.ONE);
    probabilityDistribution.setP1(BigDecimal.ONE);
    probabilityDistribution.setP10(BigDecimal.ONE);
    probabilityDistribution.setP50(BigDecimal.ONE);
    probabilityDistribution.setP90(BigDecimal.ONE);
    probabilityDistribution.setP99(BigDecimal.ONE);
    probabilityDistribution.setModeValue(BigDecimal.ONE);
    probabilityDistribution.setComments(COMMENTS);

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(
        submissionDetail, ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT))
        .thenReturn(Optional.of(probabilityDistribution));

    assertThat(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT, submissionDetail))
        .extracting(
            ProbabilityDistributionSummaryView::probabilityDistributionName,
            ProbabilityDistributionSummaryView::probabilityDistributionType,
            ProbabilityDistributionSummaryView::min,
            ProbabilityDistributionSummaryView::p99,
            ProbabilityDistributionSummaryView::p90,
            ProbabilityDistributionSummaryView::mode,
            ProbabilityDistributionSummaryView::p50,
            ProbabilityDistributionSummaryView::meanOrConstant,
            ProbabilityDistributionSummaryView::p10,
            ProbabilityDistributionSummaryView::p1,
            ProbabilityDistributionSummaryView::max,
            ProbabilityDistributionSummaryView::units,
            ProbabilityDistributionSummaryView::comments)
        .contains(
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT.getDisplayName(),
            ProbabilityDistributionType.LOGNORMAL.getDisplayName(),
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT.getUnits(),
            COMMENTS
        );
  }

  @Test
  void getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission_withOtherDistributionType() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var probabilityDistribution = new ProbabilityDistribution();

    var otherDistributionType = "Other";
    probabilityDistribution.setDistributionType(null);
    probabilityDistribution.setOtherDistributionType(otherDistributionType);
    probabilityDistribution.setInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT);

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(
        submissionDetail, ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT))
        .thenReturn(Optional.of(probabilityDistribution));

    assertThat(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT, submissionDetail))
        .extracting(
            ProbabilityDistributionSummaryView::probabilityDistributionName,
            ProbabilityDistributionSummaryView::probabilityDistributionType)
        .contains(
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT.getDisplayName(),
            otherDistributionType
        );
  }
}