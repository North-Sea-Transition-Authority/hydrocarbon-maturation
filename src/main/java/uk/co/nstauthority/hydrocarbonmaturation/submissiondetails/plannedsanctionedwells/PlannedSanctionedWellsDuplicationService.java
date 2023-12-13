package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Service
class PlannedSanctionedWellsDuplicationService implements SubmissionSectionDuplicationService {

  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  private final SubmissionUpdateRepository submissionUpdateRepository;
  private final PswFaultRepository pswFaultRepository;
  private final HoleCompletionTypesRepository holeCompletionTypesRepository;
  private final PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  private final ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;

  @Autowired
  public PlannedSanctionedWellsDuplicationService(
      SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
      PlannedSanctionedWellsRepository plannedSanctionedWellsRepository,
      SubmissionUpdateRepository submissionUpdateRepository, PswFaultRepository pswFaultRepository,
      HoleCompletionTypesRepository holeCompletionTypesRepository,
      PlannedCompletionTypesRepository plannedCompletionTypesRepository,
      ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.plannedSanctionedWellsRepository = plannedSanctionedWellsRepository;
    this.submissionUpdateRepository = submissionUpdateRepository;
    this.pswFaultRepository = pswFaultRepository;
    this.holeCompletionTypesRepository = holeCompletionTypesRepository;
    this.plannedCompletionTypesRepository = plannedCompletionTypesRepository;
    this.artificialLiftMechanismsRepository = artificialLiftMechanismsRepository;
  }

  @Override
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail)  {
    var isNotUpdate = !submissionHasAnUnresolvedUpdate(oldSubmissionDetail.getSubmission());
    var isNotFdpOrFdpa = !(oldSubmissionDetail.isFieldDevelopmentPlanSubmission()
        || oldSubmissionDetail.isFieldDevelopmentPlanAddendumSubmission());

    if (isNotUpdate || isNotFdpOrFdpa) {
      return;
    }

    plannedSanctionedWellsRepository.findBySubmissionDetail(oldSubmissionDetail)
        .ifPresent(oldPlannedSanctionedWells -> {
              var newPlannedSanctionedWell = submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                  newSubmissionDetail,
                  oldPlannedSanctionedWells,
                  PlannedSanctionedWells.class
              );

              duplicatePswFaults(oldPlannedSanctionedWells, newPlannedSanctionedWell);
              duplicateHoleCompletionType(oldPlannedSanctionedWells, newPlannedSanctionedWell);
              duplicatePlannedCompletionType(oldPlannedSanctionedWells, newPlannedSanctionedWell);
              duplicateArtificialLiftMechanisms(oldPlannedSanctionedWells, newPlannedSanctionedWell);
            }
        );
  }

  private void duplicatePswFaults(PlannedSanctionedWells oldPlannedSanctionedWells,
                                  PlannedSanctionedWells newPlannedSanctionedWell) {

    pswFaultRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells)
        .forEach(pswFaults ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newPlannedSanctionedWell,
                pswFaults,
                PswFaults.class
            )
        );
  }

  private void duplicateHoleCompletionType(PlannedSanctionedWells oldPlannedSanctionedWells,
                                           PlannedSanctionedWells newPlannedSanctionedWell) {
    holeCompletionTypesRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells)
        .forEach(holeCompletionTypes ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newPlannedSanctionedWell,
                holeCompletionTypes,
                HoleCompletionTypes.class
            )
        );
  }

  private void duplicatePlannedCompletionType(PlannedSanctionedWells oldPlannedSanctionedWells,
                                              PlannedSanctionedWells newPlannedSanctionedWell) {
    plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells)
        .forEach(plannedCompletionTypes ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newPlannedSanctionedWell,
                plannedCompletionTypes,
                PlannedCompletionTypes.class
            )
        );
  }


  private void duplicateArtificialLiftMechanisms(PlannedSanctionedWells oldPlannedSanctionedWells,
                                              PlannedSanctionedWells newPlannedSanctionedWell) {
    artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(oldPlannedSanctionedWells)
        .forEach(artificialLiftMechanisms ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newPlannedSanctionedWell,
                artificialLiftMechanisms,
                ArtificialLiftMechanisms.class
            )
        );
  }


  private boolean submissionHasAnUnresolvedUpdate(Submission submission) {
    return submissionUpdateRepository.existsBySubmissionAndResolvedFalse(submission);
  }
}
