package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSeal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealPersistenceService;

@Service
public class PostFieldSourceAndSealDuplicationService implements PostFieldSectionDuplicationService {

  private final SourceAndSealPersistenceService sourceAndSealPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final EntityManager entityManager;

  @Autowired
  public PostFieldSourceAndSealDuplicationService(SourceAndSealPersistenceService sourceAndSealPersistenceService,
                                                  SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                                  EntityManager entityManager) {
    this.sourceAndSealPersistenceService = sourceAndSealPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail fieldSubmissionDetail, SubmissionDetail newDiscoverySubmissionDetail) {
    sourceAndSealPersistenceService.getSourceAndSeal(fieldSubmissionDetail)
            .ifPresent(sourceAndSeal -> {
              var newSourceAndSeal = submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                      newDiscoverySubmissionDetail,
                      sourceAndSeal,
                      SourceAndSeal.class
              );

              newSourceAndSeal.setSealThickness(null);
              newSourceAndSeal.setTopSealFracturePressure(null);
              entityManager.persist(newSourceAndSeal);
            });
  }
}
