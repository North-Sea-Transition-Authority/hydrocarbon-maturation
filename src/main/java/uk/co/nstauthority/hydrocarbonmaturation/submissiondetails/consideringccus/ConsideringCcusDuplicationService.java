package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
public class ConsideringCcusDuplicationService implements SubmissionSectionDuplicationService {

  private final ConsideringCcusDetailRepository consideringCcusDetailRepository;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  public ConsideringCcusDuplicationService(ConsideringCcusDetailRepository consideringCcusDetailRepository,
                                           SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.consideringCcusDetailRepository = consideringCcusDetailRepository;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.isFieldSubmission()) {
      consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(oldSubmissionDetail)
          .ifPresent(consideringCcusDetail ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, consideringCcusDetail,
                  ConsideringCcusDetail.class)
          );
    }
  }
}
