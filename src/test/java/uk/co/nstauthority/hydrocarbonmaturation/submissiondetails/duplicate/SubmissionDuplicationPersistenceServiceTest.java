package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;

@Transactional
@IntegrationTest
class SubmissionDuplicationPersistenceServiceTest {

  @Autowired
  private TestEntityManager testEntityManager;

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
            GeneralDetail::getResourceName,
            GeneralDetail::getOnshoreOffshore,
            GeneralDetail::getResourceLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getLicenceApplicationId
        )
        .containsExactly(
            newGeneralDetail.getResourceName(),
            newGeneralDetail.getOnshoreOffshore(),
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

  private GeneralDetail createGeneralDetail(SubmissionDetail oldSubmissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(oldSubmissionDetail);
    generalDetail.setResourceName("prospect-name");
    generalDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    generalDetail.setResourceLocatedInUkcs(true);
    generalDetail.setPercentageLocatedInUkcs(BigDecimal.TEN);
    generalDetail.setLicenceApplicationId(1);
    testEntityManager.persistAndFlush(generalDetail);
    return generalDetail;
  }

  private SubmissionDetail createSubmissionDetail() {
    var submissionDetail = new SubmissionDetail();
    testEntityManager.persistAndFlush(submissionDetail);
    return submissionDetail;
  }

  private QuadrantAndBlock createQuadBlock(GeneralDetail generalDetail) {
    var quadrantAndBlock = new QuadrantAndBlock();
    quadrantAndBlock.setGeneralDetail(generalDetail);
    testEntityManager.persistAndFlush(quadrantAndBlock);
    return quadrantAndBlock;
  }
}