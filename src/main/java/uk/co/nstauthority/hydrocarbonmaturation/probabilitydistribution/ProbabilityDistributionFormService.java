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
    var probabilityDistribution = probabilityDistributionPersistenceService.getProbabilityDistribution(
        submissionDetail, instance
    );

    if (probabilityDistribution.isEmpty()) {
      return new ProbabilityDistributionForm();
    }

    var form = new ProbabilityDistributionForm();
    var distributionForm = probabilityDistribution.get();
    var distributionType = distributionForm.getDistributionType();
    form.setProbabilityDistributionType(distributionType);

    var exceedanceForm = getExceedanceFormFromEntity(distributionForm);
    if (distributionType.equals(ProbabilityDistributionType.NORMAL)) {
      form.setNormal(exceedanceForm);
    }
    if (distributionType.equals(ProbabilityDistributionType.LOGNORMAL)) {
      form.setLognormal(exceedanceForm);
    }
    if (distributionType.equals(ProbabilityDistributionType.BETA)) {
      form.setBeta(exceedanceForm);
    }
    if (distributionType.equals(ProbabilityDistributionType.TRIANGULAR)) {
      form.setTriangular(exceedanceForm);
    }
    if (distributionType.equals(ProbabilityDistributionType.RECTANGULAR)) {
      form.setRectangular(exceedanceForm);
    }
    if (distributionType.equals(ProbabilityDistributionType.CONSTANT)) {
      form.setConstant(exceedanceForm);
    }

    return form;
  }

  private ProbabilityOfExceedanceForm getExceedanceFormFromEntity(
      ProbabilityDistribution probabilityDistribution
  ) {
    var exceedanceForm = new ProbabilityOfExceedanceForm();
    var min = probabilityDistribution.getMinValue();
    exceedanceForm.setMin(
        min == null ? new DecimalInput() : new DecimalInput(min.toString())
    );
    var p99 = probabilityDistribution.getP99();
    exceedanceForm.setP99(
        p99 == null ? new DecimalInput() : new DecimalInput(p99.toString())
    );
    var p90 = probabilityDistribution.getP90();
    exceedanceForm.setP90(
        p90 == null ? new DecimalInput() : new DecimalInput(p90.toString())
    );
    var mode = probabilityDistribution.getModeValue();
    exceedanceForm.setMode(
        mode == null ? new DecimalInput() : new DecimalInput(mode.toString())
    );
    var p50 = probabilityDistribution.getP50();
    exceedanceForm.setP50(
        p50 == null ? new DecimalInput() : new DecimalInput(p50.toString())
    );
    var mean = probabilityDistribution.getMeanValue();
    exceedanceForm.setMean(
        mean == null ? new DecimalInput() : new DecimalInput(mean.toString())
    );
    var p10 = probabilityDistribution.getP10();
    exceedanceForm.setP10(
        p10 == null ? new DecimalInput() : new DecimalInput(p10.toString())
    );
    var p1 = probabilityDistribution.getP1();
    exceedanceForm.setP1(
        p1 == null ? new DecimalInput() : new DecimalInput(p1.toString())
    );
    var max = probabilityDistribution.getMaxValue();
    exceedanceForm.setMax(
        max == null ? new DecimalInput() : new DecimalInput(max.toString())
    );
    var constant = probabilityDistribution.getConstantValue();
    exceedanceForm.setConstant(
        constant == null ? new DecimalInput() : new DecimalInput(constant.toString())
    );
    return exceedanceForm;
  }

}
