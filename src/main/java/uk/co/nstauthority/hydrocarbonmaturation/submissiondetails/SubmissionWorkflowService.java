package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@Service
public class SubmissionWorkflowService {

  private final ResourceService resourceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public SubmissionWorkflowService(ResourceService resourceService, SubmissionDetailService submissionDetailService) {
    this.resourceService = resourceService;
    this.submissionDetailService = submissionDetailService;
  }

  @Transactional
  public SubmissionDetail createInitialProspect() {
    var resource = resourceService.createResource(ResourceType.PROSPECT);
    return submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.CREATE_PROSPECT);
  }

  @Transactional
  public void completeSubmission(UUID submissionDetailId) {
    SubmissionDetail submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    submissionDetailService.completeSubmission(submissionDetail);
  }


}
