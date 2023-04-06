package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSummaryOptions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;

@Transactional
@IntegrationTest
class SubmissionDuplicationPersistenceServiceTest {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Test
  void duplicateChildAndReplaceParent_SubmissionDetailChild() {
    var oldSubmissionDetail = createSubmissionDetail();
    var oldGeneralDetail = createGeneralDetail(oldSubmissionDetail);

    var newSubmissionDetail = createSubmissionDetail();

    assertThat(oldGeneralDetail.getSubmissionDetail().getId()).isEqualTo(oldSubmissionDetail.getId());

    var newGeneralDetail = submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail,
        oldGeneralDetail, GeneralDetail.class);

    assertThat(newGeneralDetail.getId()).isNotEqualTo(oldGeneralDetail.getId());
    assertThat(newGeneralDetail.getSubmissionDetail().getId()).isEqualTo(newSubmissionDetail.getId());
    assertThat(newGeneralDetail)
        .extracting(
            GeneralDetail::getResourceLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getLicenceApplicationId
        )
        .containsExactly(
            newGeneralDetail.getResourceLocatedInUkcs(),
            newGeneralDetail.getPercentageLocatedInUkcs(),
            newGeneralDetail.getLicenceApplicationId()
        );
  }

  @Test
  void detachChildAndReplaceParent_GeneralDetailChild() {
    var submissionDetail = createSubmissionDetail();
    var oldGeneralDetail = createGeneralDetail(submissionDetail);
    var quadBlock = createQuadBlock(oldGeneralDetail);

    var newGeneralDetail = createGeneralDetail(submissionDetail);

    assertThat(quadBlock.getGeneralDetail().getId()).isEqualTo(oldGeneralDetail.getId());

    var newQuadBlock = submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newGeneralDetail,
        quadBlock, QuadrantAndBlock.class);

    assertThat(newQuadBlock.getId()).isNotEqualTo(quadBlock.getId());
    assertThat(newQuadBlock.getGeneralDetail().getId()).isEqualTo(newGeneralDetail.getId());
    assertThat(newQuadBlock.getQuadrantBlock()).isEqualTo(quadBlock.getQuadrantBlock());
  }

  @Test
  void detachChildAndReplaceParent_DrillStemTestChild() {
    var submissionDetail = createSubmissionDetail();
    var oldDrillStem = new DrillStemTest();
    oldDrillStem.setSubmissionDetail(submissionDetail);
    oldDrillStem.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);
    entityManager.persist(oldDrillStem);
    entityManager.flush();
    var drillStemTestResult = createDrillStemTestResult(oldDrillStem);
    var newDrillStem = new DrillStemTest();
    newDrillStem.setSubmissionDetail(submissionDetail);

    assertThat(drillStemTestResult.getDrillStemTest().getId()).isEqualTo(oldDrillStem.getId());

    var newDrillStemTestResult = submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newDrillStem,
        drillStemTestResult, DrillStemTestResult.class);

    assertThat(newDrillStemTestResult.getId()).isNotEqualTo(drillStemTestResult.getId());
    assertThat(newDrillStemTestResult.getDrillStemTest().getId()).isEqualTo(newDrillStem.getId());
    assertThat(newDrillStemTestResult.getFormationName()).isEqualTo(drillStemTestResult.getFormationName());
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail oldSubmissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(oldSubmissionDetail);
    generalDetail.setResourceLocatedInUkcs(true);
    generalDetail.setPercentageLocatedInUkcs(BigDecimal.TEN);
    generalDetail.setLicenceApplicationId(1);
    entityManager.persist(generalDetail);
    entityManager.flush();
    return generalDetail;
  }

  private SubmissionDetail createSubmissionDetail() {
    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission, SubmissionType.CREATE_PROSPECT, entityManager);
  }

  private QuadrantAndBlock createQuadBlock(GeneralDetail generalDetail) {
    return SubmissionIntegrationTestUtils.createQuadAndBlock(generalDetail, "1/1", entityManager);
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