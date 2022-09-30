package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalInputTestUtil;

public class ProbabilityDistributionTestUtil {

  public static ProbabilityDistributionForm getValidProbabilityDistributionForm(
      ProbabilityDistributionType probabilityDistributionType
  ) {
    var probabilityDistributionForm = new ProbabilityDistributionForm();

    ProbabilityDistributionFieldInstance instance = probabilityDistributionType.equals(ProbabilityDistributionType.CUSTOM)
        ? ProbabilityDistributionFieldInstance.OIL_IN_PLACE
        : ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY;
    probabilityDistributionForm.setProbabilityDistributionFieldInstance(instance);

    probabilityDistributionForm.setProbabilityDistributionType(probabilityDistributionType);
    var probabilityOfExceedanceForm = getValidProbabilityOfExceedanceForm(instance, probabilityDistributionType);

    if (probabilityDistributionType.equals(ProbabilityDistributionType.NORMAL)) {
      probabilityDistributionForm.setNormal(probabilityOfExceedanceForm);
    }

    if (probabilityDistributionType.equals(ProbabilityDistributionType.LOGNORMAL)) {
      probabilityDistributionForm.setLognormal(probabilityOfExceedanceForm);
    }

    if (probabilityDistributionType.equals(ProbabilityDistributionType.BETA)) {
      probabilityDistributionForm.setBeta(probabilityOfExceedanceForm);
    }

    if (probabilityDistributionType.equals(ProbabilityDistributionType.TRIANGULAR)) {
      probabilityDistributionForm.setTriangular(probabilityOfExceedanceForm);
    }

    if (probabilityDistributionType.equals(ProbabilityDistributionType.RECTANGULAR)) {
      probabilityDistributionForm.setRectangular(probabilityOfExceedanceForm);
    }

    if (probabilityDistributionType.equals(ProbabilityDistributionType.CONSTANT)) {
      probabilityDistributionForm.setConstant(probabilityOfExceedanceForm);
    }

    if (probabilityDistributionType.equals(ProbabilityDistributionType.CUSTOM)) {
      probabilityDistributionForm.setCustom(probabilityOfExceedanceForm);
    }

    return probabilityDistributionForm;
  }

  public static ProbabilityDistributionForm getValidNormalProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.NORMAL);
  }

  public static ProbabilityDistributionForm getValidLognormalProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.LOGNORMAL);
  }

  public static ProbabilityDistributionForm getValidBetaProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.BETA);
  }

  public static ProbabilityDistributionForm getValidTriangularProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.TRIANGULAR);
  }

  public static ProbabilityDistributionForm getValidRectangularProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.RECTANGULAR);
  }

  public static ProbabilityDistributionForm getValidConstantProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.CONSTANT);
  }

  public static ProbabilityDistributionForm getValidCustomProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.CUSTOM);
  }

  public static ProbabilityDistributionForm getFormWithExceedance(
      ProbabilityDistributionType probabilityDistributionType,
      ProbabilityOfExceedanceForm probabilityOfExceedanceForm
  ) {
    switch (probabilityDistributionType) {
      case NORMAL -> {
        var validForm = getValidNormalProbabilityDistributionForm();
        validForm.setNormal(probabilityOfExceedanceForm);
        return validForm;
      }
      case LOGNORMAL -> {
        var validForm = getValidLognormalProbabilityDistributionForm();
        validForm.setLognormal(probabilityOfExceedanceForm);
        return validForm;
      }
      case BETA -> {
        var validForm = getValidBetaProbabilityDistributionForm();
        validForm.setBeta(probabilityOfExceedanceForm);
        return validForm;
      }
      case TRIANGULAR -> {
        var validForm = getValidTriangularProbabilityDistributionForm();
        validForm.setTriangular(probabilityOfExceedanceForm);
        return validForm;
      }
      case RECTANGULAR -> {
        var validForm = getValidRectangularProbabilityDistributionForm();
        validForm.setRectangular(probabilityOfExceedanceForm);
        return validForm;
      }
      case CONSTANT -> {
        var validForm = getValidConstantProbabilityDistributionForm();
        validForm.setConstant(probabilityOfExceedanceForm);
        return validForm;
      }
      case CUSTOM -> {
        var validForm = getValidCustomProbabilityDistributionForm();
        validForm.setCustom(probabilityOfExceedanceForm);
        return validForm;
      }
      default -> throw new ProbabilityDistributionException("Invalid probability distribution type provided");
    }
  }

  public static ProbabilityOfExceedanceForm getInvalidProbabilityOfExceedanceForm(
  ) {
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setMin(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP99(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP90(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setMode(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP50(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setMean(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP10(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP1(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setMax(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setConstant(new DecimalInput("abc"));
    return probabilityOfExceedanceForm;
  }

  public static ProbabilityDistribution getProbabilityDistribution(
      SubmissionDetail submissionDetail,
      ProbabilityDistributionType probabilityDistributionType,
      ProbabilityDistributionFieldInstance instance
      ) {
    var probabilityDistribution = new ProbabilityDistribution();
    probabilityDistribution.setSubmissionDetail(submissionDetail);
    probabilityDistribution.setDistributionType(probabilityDistributionType);
    probabilityDistribution.setInstance(instance);
    probabilityDistribution.setMinValue(BigDecimal.ONE);
    probabilityDistribution.setP99(BigDecimal.ONE);
    probabilityDistribution.setP90(BigDecimal.ONE);
    probabilityDistribution.setModeValue(BigDecimal.ONE);
    probabilityDistribution.setP50(BigDecimal.ONE);
    probabilityDistribution.setMeanValue(BigDecimal.ONE);
    probabilityDistribution.setP10(BigDecimal.ONE);
    probabilityDistribution.setP1(BigDecimal.ONE);
    probabilityDistribution.setMaxValue(BigDecimal.ONE);
    probabilityDistribution.setConstantValue(BigDecimal.ONE);
    return probabilityDistribution;
  }

  private static ProbabilityOfExceedanceForm getValidProbabilityOfExceedanceForm(
      ProbabilityDistributionFieldInstance instance, ProbabilityDistributionType probabilityDistributionType
  ) {
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    var behaviour = ProbabilityDistributionTypeBehaviourUtil.getBehaviour(instance, probabilityDistributionType);
    if (behaviour.isMinAvailable()) {
      probabilityOfExceedanceForm.setMin(DecimalInputTestUtil.getDecimalInput(1));
    }
    if (behaviour.isP99Available()) {
      probabilityOfExceedanceForm.setP99(DecimalInputTestUtil.getDecimalInput(2));
    }
    if (behaviour.isP90Available()) {
      probabilityOfExceedanceForm.setP90(DecimalInputTestUtil.getDecimalInput(3));
    }
    if (behaviour.isModeAvailable()) {
      probabilityOfExceedanceForm.setMode(DecimalInputTestUtil.getDecimalInput(4));
    }
    if (behaviour.isP50Available()) {
      probabilityOfExceedanceForm.setP50(DecimalInputTestUtil.getDecimalInput(5));
    }
    if (behaviour.isMeanAvailable()) {
      probabilityOfExceedanceForm.setMean(DecimalInputTestUtil.getDecimalInput(6));
    }
    if (behaviour.isP10Available()) {
      probabilityOfExceedanceForm.setP10(DecimalInputTestUtil.getDecimalInput(7));
    }
    if (behaviour.isP1Available()) {
      probabilityOfExceedanceForm.setP1(DecimalInputTestUtil.getDecimalInput(8));
    }
    if (behaviour.isMaxAvailable()) {
      probabilityOfExceedanceForm.setMax(DecimalInputTestUtil.getDecimalInput(9));
    }
    if (behaviour.isConstantAvailable()) {
      probabilityOfExceedanceForm.setConstant(DecimalInputTestUtil.getDecimalInput(10));
    }
    return probabilityOfExceedanceForm;
  }
}
