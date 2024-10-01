package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;

@Service
public class PostFieldGeneralDetailsDuplicationService implements PostFieldSectionDuplicationService {


  private final EntityManager entityManager;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final FieldPersistenceService fieldPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final FieldStatusService fieldStatusService;

  @Autowired
  public PostFieldGeneralDetailsDuplicationService(EntityManager entityManager,
                                                 GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                                 FieldPersistenceService fieldPersistenceService,
                                                 QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                                 SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                                 FieldStatusService fieldStatusService) {
    this.entityManager = entityManager;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.fieldStatusService = fieldStatusService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail fieldSubmissionDetail, SubmissionDetail newDiscoverySubmissionDetail) {
    var oldGeneralDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(fieldSubmissionDetail);
    oldGeneralDetail.ifPresent(generalDetail -> {
      var newGeneralDetail = new GeneralDetail();
      newGeneralDetail.setSubmissionDetail(newDiscoverySubmissionDetail);
      newGeneralDetail.setPercentageLocatedInUkcs(generalDetail.getPercentageLocatedInUkcs());
      newGeneralDetail.setPercentageLocatedInEngland(generalDetail.getPercentageLocatedInEngland());

      var field = fieldPersistenceService.getFieldByResource(fieldSubmissionDetail.getSubmission().getResource());
      field.ifPresent(f -> {
        newGeneralDetail.setProspectDiscoveryLocatedInUkcs(f.getFieldLocatedInUkcs());
        newGeneralDetail.setProspectDiscoveryLocatedInEngland(f.getFieldLocatedInEngland());

        var fieldStatus = fieldStatusService.getMostRecentFieldStatus(f);
        fieldStatus.ifPresent(status -> {
          newGeneralDetail.setInformationSource(InformationSource.OTHER);

          if (status.equals(FieldStatus.STATUS_000)) {
            newGeneralDetail.setOtherInformationSource("Information imported from shelved field");
          } else if (status.equals(FieldStatus.STATUS_900)) {
            newGeneralDetail.setOtherInformationSource("Information imported from ceased production field");
          }
        });
      });

      entityManager.persist(newGeneralDetail);

      quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail).forEach(quadrantAndBlock ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newGeneralDetail, quadrantAndBlock,
                      QuadrantAndBlock.class));
    });
  }
}
