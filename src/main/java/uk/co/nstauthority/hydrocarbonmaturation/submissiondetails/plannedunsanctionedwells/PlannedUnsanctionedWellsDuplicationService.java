package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Service
public class PlannedUnsanctionedWellsDuplicationService implements SubmissionSectionDuplicationService {

  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository;
  private final SubmissionUpdateRepository submissionUpdateRepository;

  @Autowired
  public PlannedUnsanctionedWellsDuplicationService(
      SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
      PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository,
      SubmissionUpdateRepository submissionUpdateRepository) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.plannedUnsanctionedWellsRepository = plannedUnsanctionedWellsRepository;
    this.submissionUpdateRepository = submissionUpdateRepository;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    var isNotUpdate = !submissionHasAnUnresolvedUpdate(oldSubmissionDetail.getSubmission());
    var isNotFdpOrFdpa = !(oldSubmissionDetail.isFieldDevelopmentPlanSubmission()
        || oldSubmissionDetail.isFieldDevelopmentPlanAddendumSubmission());

    if (isNotUpdate || isNotFdpOrFdpa) {
      return;
    }

    plannedUnsanctionedWellsRepository.findBySubmissionDetail(oldSubmissionDetail)
        .ifPresent(plannedUnsanctionedWells ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newSubmissionDetail,
                plannedUnsanctionedWells,
                PlannedUnsanctionedWells.class
            )
        );
  }

  private boolean submissionHasAnUnresolvedUpdate(Submission submission) {
    return submissionUpdateRepository.existsBySubmissionAndResolvedFalse(submission);
  }
}
