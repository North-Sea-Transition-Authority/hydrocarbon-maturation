package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWells;

@Service
public class SubmissionDuplicationPersistenceService {

  private final EntityManager entityManager;

  @Autowired
  SubmissionDuplicationPersistenceService(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Transactional
  public <T extends SubmissionDetailChildEntity> T duplicateChildAndReplaceParent(SubmissionDetail parent,
                                                                                  SubmissionDetailChildEntity oldChild,
                                                                                  Class<T> childClass) {
    var newChild = BeanUtils.instantiateClass(childClass);
    BeanUtils.copyProperties(oldChild, newChild);
    newChild.setId(null);
    newChild.setSubmissionDetail(parent);

    entityManager.persist(newChild);
    return newChild;
  }

  @Transactional
  public <T extends GeneralDetailChildEntity> T duplicateChildAndReplaceParent(GeneralDetail parent,
                                                                               GeneralDetailChildEntity oldChild,
                                                                               Class<T> childClass) {
    var newChild = BeanUtils.instantiateClass(childClass);
    BeanUtils.copyProperties(oldChild, newChild);

    newChild.setId(null);
    newChild.setGeneralDetail(parent);

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

  @Transactional
  public <T extends PlannedSanctionedWellsChildEntity> T duplicateChildAndReplaceParent(
      PlannedSanctionedWells parent,
      PlannedSanctionedWellsChildEntity oldChild,
      Class<T> childClass) {

    var newChild = BeanUtils.instantiateClass(childClass);
    BeanUtils.copyProperties(oldChild, newChild);

    newChild.setId(null);
    newChild.setPlannedSanctionedWells(parent);

    entityManager.persist(newChild);

    return newChild;
  }
}
