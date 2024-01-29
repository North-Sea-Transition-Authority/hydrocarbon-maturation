package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;


import java.time.Instant;
import jakarta.persistence.EntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;

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

    return createSubmissionDetail(submissionType, resource, entityManager);
  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType,
                                                        Resource resource,
                                                        EntityManager entityManager) {

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setVersionNumber(1);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(100));
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setSubmittedTimestamp(Instant.now().minusSeconds(50));
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    submissionDetail.setCreatedByWuaId(1);
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    submission.setResource(resource);
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();
    return submissionDetail;
  }
}
