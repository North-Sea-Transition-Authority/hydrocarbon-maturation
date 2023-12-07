package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
public class FieldComplexityDuplicationService implements SubmissionSectionDuplicationService {

  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final FieldComplexityRepository fieldComplexityRepository;

  @Autowired
  public FieldComplexityDuplicationService(SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                           FieldComplexityRepository fieldComplexityRepository) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.fieldComplexityRepository = fieldComplexityRepository;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isFieldSubmission()) {
      return;
    }

    fieldComplexityRepository.findBySubmissionDetail(oldSubmissionDetail)
        .ifPresent(fieldComplexity ->
            submissionDuplicationPersistenceService
                .duplicateChildAndReplaceParent(newSubmissionDetail, fieldComplexity, FieldComplexity.class)
        );
  }
}
