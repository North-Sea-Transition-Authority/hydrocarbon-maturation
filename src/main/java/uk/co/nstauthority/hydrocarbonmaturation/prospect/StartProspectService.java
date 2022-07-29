package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class StartProspectService {

  private final SubmissionCreateService submissionCreateService;

  @Autowired
  StartProspectService(SubmissionCreateService submissionCreateService) {
    this.submissionCreateService = submissionCreateService;
  }

  SubmissionDetail createInitialProspect() {
    return submissionCreateService.createInitialProspect();
  }
}
