package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ProbabilityDistributionFormService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public ProbabilityDistributionFormService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public ProbabilityDistributionForm probabilityDistributionEntityToForm(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionFieldInstance instance
  ) {
    return probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance)
        .map(probabilityDistribution -> {
          var exceedanceForm = getExceedanceFormFromEntity(probabilityDistribution);

          var form = createProbabilityDistributionForm(instance, probabilityDistribution.getDistributionType());
          form.setProbabilityOfExceedanceForm(exceedanceForm);

          return form;
        })
        .orElseGet(() -> createProbabilityDistributionForm(instance));
  }

  private ProbabilityDistributionForm createProbabilityDistributionForm(ProbabilityDistributionFieldInstance instance,
                                                                        ProbabilityDistributionType distributionType) {
    var form = new ProbabilityDistributionForm();

    if (instance.isCustomType()) {
      form.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    } else {
      form.setProbabilityDistributionType(distributionType);
    }

    return form;
  }

  private ProbabilityDistributionForm createProbabilityDistributionForm(ProbabilityDistributionFieldInstance instance) {
    return createProbabilityDistributionForm(instance, null);
  }

  private ProbabilityOfExceedanceForm getExceedanceFormFromEntity(
      ProbabilityDistribution probabilityDistribution
  ) {
    var exceedanceForm = new ProbabilityOfExceedanceForm();
    var min = probabilityDistribution.getMinValue();
    exceedanceForm.setMin(DecimalInput.createDecimalInput(min));
    var p99 = probabilityDistribution.getP99();
    exceedanceForm.setP99(DecimalInput.createDecimalInput(p99));
    var p90 = probabilityDistribution.getP90();
    exceedanceForm.setP90(DecimalInput.createDecimalInput(p90));
    var mode = probabilityDistribution.getModeValue();
    exceedanceForm.setMode(DecimalInput.createDecimalInput(mode));
    var p50 = probabilityDistribution.getP50();
    exceedanceForm.setP50(DecimalInput.createDecimalInput(p50));
    var mean = probabilityDistribution.getMeanValue();
    exceedanceForm.setMean(DecimalInput.createDecimalInput(mean));
    var p10 = probabilityDistribution.getP10();
    exceedanceForm.setP10(DecimalInput.createDecimalInput(p10));
    var p1 = probabilityDistribution.getP1();
    exceedanceForm.setP1(DecimalInput.createDecimalInput(p1));
    var max = probabilityDistribution.getMaxValue();
    exceedanceForm.setMax(DecimalInput.createDecimalInput(max));
    var constant = probabilityDistribution.getConstantValue();
    exceedanceForm.setConstant(DecimalInput.createDecimalInput(constant));
    var mostLikely = probabilityDistribution.getMostLikelyValue();
    exceedanceForm.setMostLikely(DecimalInput.createDecimalInput(mostLikely));
    var comments = probabilityDistribution.getComments();
    exceedanceForm.setComments(comments);
    return exceedanceForm;
  }
}
