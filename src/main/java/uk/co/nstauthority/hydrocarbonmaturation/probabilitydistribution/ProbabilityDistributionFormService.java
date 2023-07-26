package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

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
        .map(probabilityDistribution -> createProbabilityDistributionForm(probabilityDistribution, instance))
        .orElseGet(() -> createProbabilityDistributionForm(instance));
  }

  public ProbabilityDistributionForm probabilityDistributionEntityToForm(
      ReservoirSubmissionDetail reservoirSubmissionDetail,
      ProbabilityDistributionFieldInstance instance
  ) {
    return probabilityDistributionPersistenceService.getProbabilityDistribution(reservoirSubmissionDetail, instance)
        .map(probabilityDistribution -> createProbabilityDistributionForm(probabilityDistribution, instance))
        .orElseGet(() -> createProbabilityDistributionForm(instance));
  }

  private ProbabilityDistributionForm createProbabilityDistributionForm(ProbabilityDistribution probabilityDistribution,
                                                                        ProbabilityDistributionFieldInstance instance) {
    if (probabilityDistribution.getDistributionType() == null) {
      return createProbabilityDistributionForm(instance);
    }
    var exceedanceForm = getExceedanceFormFromEntity(probabilityDistribution);

    var form = createProbabilityDistributionForm(instance, probabilityDistribution.getDistributionType());
    form.setProbabilityOfExceedanceForm(exceedanceForm);

    return form;
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
    if (instance.getPresetTypes().size() == 1) {
      var preset = instance.getPresetTypes().get(0);
      return createProbabilityDistributionForm(instance, preset);
    }
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
    var threeC = probabilityDistribution.getThreeC();
    exceedanceForm.setThreeC(DecimalInput.createDecimalInput(threeC));
    var mode = probabilityDistribution.getModeValue();
    exceedanceForm.setMode(DecimalInput.createDecimalInput(mode));
    var p50 = probabilityDistribution.getP50();
    exceedanceForm.setP50(DecimalInput.createDecimalInput(p50));
    var twoC = probabilityDistribution.getTwoC();
    exceedanceForm.setTwoC(DecimalInput.createDecimalInput(twoC));
    var mean = probabilityDistribution.getMeanValue();
    exceedanceForm.setMean(DecimalInput.createDecimalInput(mean));
    var p10 = probabilityDistribution.getP10();
    exceedanceForm.setP10(DecimalInput.createDecimalInput(p10));
    var oneC = probabilityDistribution.getOneC();
    exceedanceForm.setOneC(DecimalInput.createDecimalInput(oneC));
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
