package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ProbabilityDistributionPersistenceService {

  private final ProbabilityDistributionRepository probabilityDistributionRepository;

  @Autowired
  public ProbabilityDistributionPersistenceService(ProbabilityDistributionRepository probabilityDistributionRepository) {
    this.probabilityDistributionRepository = probabilityDistributionRepository;
  }

  @Transactional
  public void saveProbabilityDistribution(
      ProbabilityDistributionFieldInstance instance,
      ProbabilityDistributionForm form,
      SubmissionDetail submissionDetail
  ) {
    var probabilityDistribution = getProbabilityDistribution(submissionDetail, instance)
        .orElse(new ProbabilityDistribution());
    probabilityDistribution.setSubmissionDetail(submissionDetail);
    probabilityDistribution.setDistributionType(form.getProbabilityDistributionType());
    probabilityDistribution.setInstance(instance);
    var exceedanceForm = form.getProbabilityOfExceedanceForm();
    probabilityDistribution.setMinValue(exceedanceForm.getMin().createBigDecimalOrNull());
    probabilityDistribution.setP99(exceedanceForm.getP99().createBigDecimalOrNull());
    probabilityDistribution.setP90(exceedanceForm.getP90().createBigDecimalOrNull());
    probabilityDistribution.setModeValue(exceedanceForm.getMode().createBigDecimalOrNull());
    probabilityDistribution.setP50(exceedanceForm.getP50().createBigDecimalOrNull());
    probabilityDistribution.setMeanValue(exceedanceForm.getMean().createBigDecimalOrNull());
    probabilityDistribution.setP10(exceedanceForm.getP10().createBigDecimalOrNull());
    probabilityDistribution.setP1(exceedanceForm.getP1().createBigDecimalOrNull());
    probabilityDistribution.setMaxValue(exceedanceForm.getMax().createBigDecimalOrNull());
    probabilityDistribution.setConstantValue(exceedanceForm.getConstant().createBigDecimalOrNull());

    probabilityDistributionRepository.save(probabilityDistribution);
  }

  Optional<ProbabilityDistribution> getProbabilityDistribution(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionFieldInstance instance
  ) {
    return probabilityDistributionRepository.findBySubmissionDetailAndInstance(submissionDetail, instance);
  }

  public List<ProbabilityDistribution> getAllProbabilityDistributionsForInstances(
      SubmissionDetail submissionDetail,
      Set<ProbabilityDistributionFieldInstance> instances) {
    return probabilityDistributionRepository.findBySubmissionDetailAndInstanceIn(submissionDetail, instances);
  }

  public void deleteProbabilityDistribution(ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance,
                                            SubmissionDetail submissionDetail) {
    probabilityDistributionRepository.deleteBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance);
  }
}
