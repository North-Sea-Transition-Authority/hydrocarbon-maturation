package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
public class CorrectionService {

  private final SubmissionDetailRepository submissionDetailRepository;

  @Autowired
  public CorrectionService(SubmissionDetailRepository submissionDetailRepository) {
    this.submissionDetailRepository = submissionDetailRepository;
  }


  /** Can start a correction on a resource if there are no existing draft corrections for it.
   * @param resource the resource we want to start a correction on
   * @return true if a correction can be started
   */
  public boolean canStartCorrection(Resource resource, boolean isInResponsibleTeam) {
    if (!isInResponsibleTeam) {
      return false;
    }
    return !submissionDetailRepository.existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
        Set.of(SubmissionDetailStatus.DRAFT),
        resource
    );
  }
}
