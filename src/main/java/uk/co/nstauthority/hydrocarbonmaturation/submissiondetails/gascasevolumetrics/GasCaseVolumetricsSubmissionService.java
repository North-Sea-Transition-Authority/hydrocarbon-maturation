package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class GasCaseVolumetricsSubmissionService {

  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    return true;
  }
}
