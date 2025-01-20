package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;


import jakarta.persistence.EntityManager;
import java.time.Instant;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionType;

public class SubmissionDetailIntegrationTestUtils {

  private SubmissionDetailIntegrationTestUtils() {
    throw new AssertionError();
  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType, EntityManager entityManager) {
    var resourceType = ResourceType.PROSPECT;
    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      resourceType = ResourceType.FIELD;
    }

    var resource = ResourceIntegrationTestUtils.buildResource(resourceType, entityManager);

    return createSubmissionDetail(submissionType, resource, SubmissionDetailStatus.DRAFT, entityManager);
  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType,
                                                        Resource resource,
                                                        SubmissionDetailStatus submissionDetailStatus,
                                                        EntityManager entityManager) {

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setVersionNumber(1);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(100));
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setSubmittedTimestamp(Instant.now().minusSeconds(50));
    submissionDetail.setAcceptedTimestamp(Instant.now().minusSeconds(50));
    submissionDetail.setStatus(submissionDetailStatus);
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    submissionDetail.setCreatedByWuaId(1);
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    submission.setResource(resource);
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();
    return submissionDetail;
  }

  public static SubmissionDetail createCorrectionSubmissionDetail(SubmissionDetail submissionDetail,
                                                                  Resource resource,
                                                                  EntityManager entityManager) {

    submissionDetail.setVersionNumber(submissionDetail.getVersionNumber() + 1);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(40));
    submissionDetail.setCorrection(true);
    submissionDetail.setCorrectionTimestamp(Instant.now().minusSeconds(20));
    submissionDetail.setCorrectionType(CorrectionType.SUBMISSION_CORRECTION);
    submissionDetail.setCorrectionByWuaId(1);
    submissionDetail.setVersionNumber(2);
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    submission.setResource(resource);
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();
    return submissionDetail;
  }

  public static SubmissionDetail createLegacySubmissionDetail(SubmissionDetail submissionDetail,
                                                              Resource resource,
                                                              EntityManager entityManager) {
    submissionDetail.setLegacyResDataId(1);
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    submission.setResource(resource);
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();
    return submissionDetail;
  }
}
