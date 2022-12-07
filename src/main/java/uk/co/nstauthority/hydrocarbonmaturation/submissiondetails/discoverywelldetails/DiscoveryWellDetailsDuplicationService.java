package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class DiscoveryWellDetailsDuplicationService implements SubmissionSectionDuplicationService {
  private final DiscoveryWellDetailsPersistenceService discoveryDetailsPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  DiscoveryWellDetailsDuplicationService(DiscoveryWellDetailsPersistenceService discoveryDetailsPersistenceService,
                                         SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.discoveryDetailsPersistenceService = discoveryDetailsPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.getSubmissionType() == SubmissionType.UPDATE_DISCOVERY) {
      discoveryDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(oldSubmissionDetail)
          .ifPresent(discoveryWellDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail,
                discoveryWellDetail,
                DiscoveryWellDetail.class)
          );
    }
  }
}
