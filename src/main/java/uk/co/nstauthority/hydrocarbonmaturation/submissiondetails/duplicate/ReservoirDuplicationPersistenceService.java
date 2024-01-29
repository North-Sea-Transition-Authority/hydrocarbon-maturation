package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class ReservoirDuplicationPersistenceService {

  private final EntityManager entityManager;

  @Autowired
  ReservoirDuplicationPersistenceService(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Transactional
  public <T extends ReservoirSubmissionDetailChildEntity> T duplicateChildAndReplaceParent(
      ReservoirSubmissionDetail parent,
      ReservoirSubmissionDetailChildEntity oldChild,
      Class<T> childClass) {
    var newChild = BeanUtils.instantiateClass(childClass);
    BeanUtils.copyProperties(oldChild, newChild);
    newChild.setId(null);
    newChild.setReservoirSubmissionDetail(parent);

    entityManager.persist(newChild);
    return newChild;
  }

  @Transactional
  public <T extends DrillStemTestChildEntity> T duplicateChildAndReplaceParent(DrillStemTest parent,
                                                                               DrillStemTestChildEntity oldChild,
                                                                               Class<T> childClass) {
    var newChild = BeanUtils.instantiateClass(childClass);
    BeanUtils.copyProperties(oldChild, newChild);

    newChild.setId(null);
    newChild.setDrillStemTest(parent);

    entityManager.persist(newChild);

    return newChild;
  }
}
