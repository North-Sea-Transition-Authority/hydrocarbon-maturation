package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionWorkflowService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class StartProspectService {

  private final SubmissionWorkflowService submissionCreateService;

  @Autowired
  StartProspectService(SubmissionWorkflowService submissionCreateService) {
    this.submissionCreateService = submissionCreateService;
  }

  SubmissionDetail createInitialProspect() {
    return submissionCreateService.createInitialProspect();
  }
}
