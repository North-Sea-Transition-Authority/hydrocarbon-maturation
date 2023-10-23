package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSummaryOptions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;

@Transactional
@IntegrationTest
class ReservoirDuplicationPersistenceServiceTest {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Test
  void duplicateChildAndReplaceParent_ReservoirSubmissionDetailChild() {
    var oldReservoirSubmissionDetail = createReservoirSubmissionDetail();
    var oldOilCaseOutput = createOilCaseOutput(oldReservoirSubmissionDetail);

    var newReservoirSubmissionDetail = createReservoirSubmissionDetail();

    assertThat(oldOilCaseOutput.getReservoirSubmissionDetail().getId()).isEqualTo(oldReservoirSubmissionDetail.getId());

    var newOilCaseOutput = reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
        oldOilCaseOutput, OilCaseOutput.class);

    assertThat(newOilCaseOutput.getId()).isNotEqualTo(oldOilCaseOutput.getId());
    assertThat(newOilCaseOutput.getReservoirSubmissionDetail().getId()).isEqualTo(newReservoirSubmissionDetail.getId());
    assertThat(newOilCaseOutput.getHasAssociatedGasRecoverableResources()).isEqualTo(oldOilCaseOutput.getHasAssociatedGasRecoverableResources());
  }

  @Test
  void detachChildAndReplaceParent_DrillStemTestChild() {
    var reservoirSubmissionDetail = createReservoirSubmissionDetail();
    var oldDrillStem = new DrillStemTest();
    oldDrillStem.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    oldDrillStem.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);
    entityManager.persist(oldDrillStem);
    entityManager.flush();
    var drillStemTestResult = createDrillStemTestResult(oldDrillStem);
    var newDrillStem = new DrillStemTest();
    newDrillStem.setReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(drillStemTestResult.getDrillStemTest().getId()).isEqualTo(oldDrillStem.getId());

    var newDrillStemTestResult = reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newDrillStem,
        drillStemTestResult, DrillStemTestResult.class);

    assertThat(newDrillStemTestResult.getId()).isNotEqualTo(drillStemTestResult.getId());
    assertThat(newDrillStemTestResult.getDrillStemTest().getId()).isEqualTo(newDrillStem.getId());
    assertThat(newDrillStemTestResult.getFormationName()).isEqualTo(drillStemTestResult.getFormationName());
  }

  private OilCaseOutput createOilCaseOutput(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var oilCaseOutput = new OilCaseOutput();
    oilCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    oilCaseOutput.setHasAssociatedGasRecoverableResources(false);
    entityManager.persist(oilCaseOutput);
    entityManager.flush();

    return oilCaseOutput;
  }

  private ReservoirSubmissionDetail createReservoirSubmissionDetail() {
    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    var submissionDetail = SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission, SubmissionType.CREATE_PROSPECT, entityManager);
    return ReservoirSubmissionDetailsIntegrationTestUtils
        .createReservoirSubmissionDetail(submissionDetail, entityManager);
  }

  private DrillStemTestResult createDrillStemTestResult(DrillStemTest drillStemTest) {
    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setDrillStemTest(drillStemTest);
    drillStemTestResult.setFormationName("goodName");
    entityManager.persist(drillStemTestResult);
    entityManager.flush();
    return drillStemTestResult;
  }
}