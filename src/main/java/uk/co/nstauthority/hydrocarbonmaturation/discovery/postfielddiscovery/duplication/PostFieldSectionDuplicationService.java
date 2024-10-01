package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import jakarta.transaction.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface PostFieldSectionDuplicationService {

  @Transactional
  void duplicateSubmissionSection(SubmissionDetail fieldSubmissionDetail, SubmissionDetail newDiscoverySubmissionDetail);

}
