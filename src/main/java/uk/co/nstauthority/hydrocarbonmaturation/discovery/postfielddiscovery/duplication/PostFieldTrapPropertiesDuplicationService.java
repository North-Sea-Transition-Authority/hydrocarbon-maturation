package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

@Service
public class PostFieldTrapPropertiesDuplicationService implements PostFieldSectionDuplicationService {

  private final EntityManager entityManager;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Autowired
  public PostFieldTrapPropertiesDuplicationService(EntityManager entityManager,
                                                   TrapPropertiesPersistenceService trapPropertiesPersistenceService) {
    this.entityManager = entityManager;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail fieldSubmissionDetail, SubmissionDetail newDiscoverySubmissionDetail) {
    var oldTrapProperty = trapPropertiesPersistenceService.getTrapProperty(fieldSubmissionDetail);
    oldTrapProperty.ifPresent(trapProperty -> {
      var newTrapProperty = new TrapProperty();
      newTrapProperty.setSubmissionDetail(newDiscoverySubmissionDetail);
      newTrapProperty.setTrapType(trapProperty.getTrapType());
      newTrapProperty.setCentreOfInterestQuadrantBlock(trapProperty.getCentreOfInterestQuadrantBlock());

      entityManager.persist(newTrapProperty);
    });
  }
}
