package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class ProbabilityDistributionPersistenceService {

  private final ProbabilityDistributionRepository probabilityDistributionRepository;

  @Autowired
  public ProbabilityDistributionPersistenceService(ProbabilityDistributionRepository probabilityDistributionRepository) {
    this.probabilityDistributionRepository = probabilityDistributionRepository;
  }

  @Transactional
  public void saveProbabilityDistributionWhenNotNull(ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance,
                                                     ProbabilityDistributionForm probabilityDistributionForm,
                                                     SubmissionDetail submissionDetail) {
    if (probabilityDistributionForm.getProbabilityDistributionType() != null) {
      saveProbabilityDistribution(probabilityDistributionFieldInstance, probabilityDistributionForm, submissionDetail,
          null);
    }
  }

  @Transactional
  public void saveProbabilityDistributionWhenNotNull(ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance,
                                                     ProbabilityDistributionForm probabilityDistributionForm,
                                                     SubmissionDetail submissionDetail,
                                                     ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (probabilityDistributionForm.getProbabilityDistributionType() != null) {
      saveProbabilityDistribution(probabilityDistributionFieldInstance, probabilityDistributionForm, submissionDetail,
          reservoirSubmissionDetail);
    }
  }

  private void saveProbabilityDistribution(
      ProbabilityDistributionFieldInstance instance,
      ProbabilityDistributionForm form,
      SubmissionDetail submissionDetail,
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var probabilityDistribution =
        (reservoirSubmissionDetail == null
            ? getProbabilityDistribution(submissionDetail, instance)
            : getProbabilityDistribution(reservoirSubmissionDetail, instance))
        .orElse(new ProbabilityDistribution());

    //Legacy probability distributions may not have a distribution type, so delete those first on save
    if (probabilityDistribution.getDistributionType() == null) {
      probabilityDistributionRepository.delete(probabilityDistribution);
      probabilityDistribution = new ProbabilityDistribution();
    }

    probabilityDistribution.setSubmissionDetail(submissionDetail);
    probabilityDistribution.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    probabilityDistribution.setDistributionType(form.getProbabilityDistributionType());
    probabilityDistribution.setInstance(instance);
    var exceedanceForm = form.getProbabilityOfExceedanceForm();
    probabilityDistribution.setMinValue(exceedanceForm.getMin().createBigDecimalOrNull());
    probabilityDistribution.setP99(exceedanceForm.getP99().createBigDecimalOrNull());
    probabilityDistribution.setP90(exceedanceForm.getP90().createBigDecimalOrNull());
    probabilityDistribution.setOneC(exceedanceForm.getOneC().createBigDecimalOrNull());
    probabilityDistribution.setModeValue(exceedanceForm.getMode().createBigDecimalOrNull());
    probabilityDistribution.setP50(exceedanceForm.getP50().createBigDecimalOrNull());
    probabilityDistribution.setTwoC(exceedanceForm.getTwoC().createBigDecimalOrNull());
    probabilityDistribution.setMeanValue(exceedanceForm.getMean().createBigDecimalOrNull());
    probabilityDistribution.setP10(exceedanceForm.getP10().createBigDecimalOrNull());
    probabilityDistribution.setThreeC(exceedanceForm.getThreeC().createBigDecimalOrNull());
    probabilityDistribution.setP1(exceedanceForm.getP1().createBigDecimalOrNull());
    probabilityDistribution.setMaxValue(exceedanceForm.getMax().createBigDecimalOrNull());
    probabilityDistribution.setMostLikelyValue(exceedanceForm.getMostLikely().createBigDecimalOrNull());
    probabilityDistribution.setConstantValue(exceedanceForm.getConstant().createBigDecimalOrNull());
    probabilityDistribution.setComments(exceedanceForm.getComments());
    probabilityDistributionRepository.save(probabilityDistribution);
  }

  Optional<ProbabilityDistribution> getProbabilityDistribution(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionFieldInstance instance
  ) {
    return probabilityDistributionRepository.findBySubmissionDetailAndInstance(submissionDetail, instance);
  }

  Optional<ProbabilityDistribution> getProbabilityDistribution(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      ProbabilityDistributionFieldInstance instance
  ) {
    return probabilityDistributionRepository.findByReservoirSubmissionDetailAndInstance(reservoirSubmissionDetail, instance);
  }

  public List<ProbabilityDistribution> getAllProbabilityDistributionsForInstances(
      SubmissionDetail submissionDetail,
      Set<ProbabilityDistributionFieldInstance> instances) {
    return probabilityDistributionRepository.findBySubmissionDetailAndInstanceIn(submissionDetail, instances);
  }

  public List<ProbabilityDistribution> getAllProbabilityDistributionsForInstances(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      Set<ProbabilityDistributionFieldInstance> instances) {
    return probabilityDistributionRepository.findByReservoirSubmissionDetailAndInstanceIn(reservoirSubmissionDetail, instances);
  }

  @Transactional
  public void deleteProbabilityDistribution(ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance,
                                            SubmissionDetail submissionDetail) {
    probabilityDistributionRepository.deleteBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance);
  }

  @Transactional
  public void deleteProbabilityDistribution(ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance,
                                            ReservoirSubmissionDetail reservoirSubmissionDetail) {
    probabilityDistributionRepository
        .deleteByReservoirSubmissionDetailAndInstance(reservoirSubmissionDetail, probabilityDistributionFieldInstance);
  }

  @Transactional
  public void deleteProbabilityDistributions(List<ProbabilityDistributionFieldInstance> instances,
                                             ReservoirSubmissionDetail reservoirSubmissionDetail) {
    probabilityDistributionRepository
        .deleteByReservoirSubmissionDetailAndInstanceIn(reservoirSubmissionDetail, instances);
  }
}