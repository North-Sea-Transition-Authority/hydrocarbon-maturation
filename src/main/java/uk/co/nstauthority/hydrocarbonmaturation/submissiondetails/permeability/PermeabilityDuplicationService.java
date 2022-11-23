package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class PermeabilityDuplicationService implements SubmissionSectionDuplicationService {
  private final PermeabilityPersistenceService permeabilityPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  PermeabilityDuplicationService(PermeabilityPersistenceService permeabilityPersistenceService,
                                 SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.permeabilityPersistenceService = permeabilityPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    permeabilityPersistenceService.getPermeabilityDetail(oldSubmissionDetail)
        .ifPresent(permeabilityDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, permeabilityDetail,
                PermeabilityDetail.class)
        );
  }
}
