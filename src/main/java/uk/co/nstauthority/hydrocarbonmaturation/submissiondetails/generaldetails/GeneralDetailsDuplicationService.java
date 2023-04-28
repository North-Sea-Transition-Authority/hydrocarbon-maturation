package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class GeneralDetailsDuplicationService implements SubmissionSectionDuplicationService {
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final LicencePersistenceService licencePersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final EntityManager entityManager;

  @Autowired
  GeneralDetailsDuplicationService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                   LicencePersistenceService licencePersistenceService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                   EntityManager entityManager) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.entityManager = entityManager;
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

          var isUpdateSubmissionType = newSubmissionDetail.getSubmissionType() == SubmissionType.PROSPECT_UPDATE
              || newSubmissionDetail.getSubmissionType() == SubmissionType.UPDATE_DISCOVERY;
          var isDiscoveryFromProspect = newSubmissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT;

          if (!newSubmissionDetail.hasSameSubmissionType(oldSubmissionDetail)
              && (isUpdateSubmissionType || isDiscoveryFromProspect)) {
            preventCopyingForward(newGeneralDetail);
          }

          quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail).forEach(quadrantAndBlock ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newGeneralDetail, quadrantAndBlock,
                  QuadrantAndBlock.class));
          licencePersistenceService.getLicencesByGeneralDetail(generalDetail).forEach(licence ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newGeneralDetail, licence,
                  Licence.class));
        });
  }

  @Transactional
  void preventCopyingForward(GeneralDetail generalDetail) {
    generalDetail.setInformationSource(null);
    generalDetail.setOtherInformationSource(null);
    generalDetail.setLicenceApplicationReference(null);
    generalDetail.setLicenceApplicationId(null);
    generalDetail.setLicenceApplicationInfoSource(null);
    entityManager.persist(generalDetail);
  }
}
