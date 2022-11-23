package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class GeneralDetailsDuplicationService implements SubmissionSectionDuplicationService {
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  GeneralDetailsDuplicationService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail)
        .ifPresent(generalDetail -> {
          var newGeneralDetail = submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
              newSubmissionDetail,
              generalDetail,
              GeneralDetail.class
          );

          quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail).forEach(quadrantAndBlock ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newGeneralDetail, quadrantAndBlock,
                  QuadrantAndBlock.class));
        });
  }
}
