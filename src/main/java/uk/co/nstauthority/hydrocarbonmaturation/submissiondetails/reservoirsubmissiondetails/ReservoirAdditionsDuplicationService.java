package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class ReservoirAdditionsDuplicationService implements SubmissionSectionDuplicationService {
  private final ReservoirAdditionsRepository reservoirAdditionsRepository;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  ReservoirAdditionsDuplicationService(ReservoirAdditionsRepository reservoirAdditionsRepository,
                                       SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.reservoirAdditionsRepository = reservoirAdditionsRepository;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }


  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    // Only want to retain reservoir-section completed selection for an update
    var isUpdate = newSubmissionDetail.isUpdateToSubmission()
        || SubmissionType.isUpdateType(newSubmissionDetail.getSubmissionType());
    if (isUpdate) {
      reservoirAdditionsRepository.findBySubmissionDetail(oldSubmissionDetail)
          .ifPresent(
              reservoirAdditions -> submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                  newSubmissionDetail, reservoirAdditions,
                  ReservoirAdditions.class)
          );
    }
  }
}
