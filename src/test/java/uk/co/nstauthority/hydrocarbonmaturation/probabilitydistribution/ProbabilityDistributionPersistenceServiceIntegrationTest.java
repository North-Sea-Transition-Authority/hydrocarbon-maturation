package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;

@Transactional
@IntegrationTest
class ProbabilityDistributionPersistenceServiceIntegrationTest {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Test
  void copyProbabilityDistribution() {
    var reservoirSubmissionDetail = createReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var fromProbabilityDistribution = createProbabilityDistribution(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    probabilityDistributionPersistenceService.copyProbabilityDistribution(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        submissionDetail
    );
    var newProbabilityDistributionOptional = probabilityDistributionPersistenceService.getProbabilityDistribution(
        submissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    assertThat(newProbabilityDistributionOptional).isPresent();
    var newProbabilityDistribution = newProbabilityDistributionOptional.get();
    RecursiveComparisonConfiguration recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder()
        .withIgnoredFields("reservoirSubmissionDetail", "submissionDetail", "id", "instance", "comments")
        .build();
    assertThat(newProbabilityDistribution)
        .usingRecursiveComparison(recursiveComparisonConfiguration)
        .isEqualTo(fromProbabilityDistribution);
    assertThat(newProbabilityDistribution.getSubmissionDetail()).isEqualTo(submissionDetail);
    assertThat(newProbabilityDistribution.getReservoirSubmissionDetail()).isNull();
    assertThat(newProbabilityDistribution.getInstance()).isEqualTo(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    assertThat(newProbabilityDistribution.getComments()).isEqualTo(
        String.format("Copied from reservoir: %s", reservoirSubmissionDetail.getReservoir().getName())
    );
  }


  private ReservoirSubmissionDetail createReservoirSubmissionDetail() {
    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    var submissionDetail = SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission, SubmissionType.CREATE_DISCOVERY, entityManager);
    return ReservoirSubmissionDetailsIntegrationTestUtils
        .createReservoirSubmissionDetail(submissionDetail, entityManager);
  }

  private ProbabilityDistribution createProbabilityDistribution(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance) {
    var probabilityDistribution = new ProbabilityDistribution();
    probabilityDistribution.setDistributionType(ProbabilityDistributionType.RECTANGULAR);
    probabilityDistribution.setInstance(probabilityDistributionFieldInstance);
    probabilityDistribution.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    probabilityDistribution.setSubmissionDetail(reservoirSubmissionDetail.getSubmissionDetail());
    probabilityDistribution.setP1(BigDecimal.ONE);
    probabilityDistribution.setP10(BigDecimal.TEN);
    probabilityDistribution.setP50(BigDecimal.ZERO);
    probabilityDistribution.setP90(BigDecimal.ONE);
    probabilityDistribution.setP99(BigDecimal.TEN);
    probabilityDistribution.setMinValue(BigDecimal.ZERO);
    probabilityDistribution.setModeValue(BigDecimal.ONE);
    probabilityDistribution.setMeanValue(BigDecimal.TEN);
    probabilityDistribution.setMaxValue(BigDecimal.ZERO);
    probabilityDistribution.setMostLikelyValue(BigDecimal.ONE);
    probabilityDistribution.setConstantValue(BigDecimal.ONE);
    probabilityDistribution.setOneC(BigDecimal.TEN);
    probabilityDistribution.setTwoC(BigDecimal.TEN);
    probabilityDistribution.setThreeC(BigDecimal.ONE);
    entityManager.persist(probabilityDistribution);
    entityManager.flush();
    return probabilityDistribution;
  }
}
