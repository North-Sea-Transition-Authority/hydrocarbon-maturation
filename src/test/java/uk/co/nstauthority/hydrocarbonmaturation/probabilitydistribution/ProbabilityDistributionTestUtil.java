package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalInputTestUtil;

public class ProbabilityDistributionTestUtil {

  public static ProbabilityDistributionForm getValidProbabilityDistributionForm(
      ProbabilityDistributionType probabilityDistributionType
  ) {
    var instance = probabilityDistributionType == ProbabilityDistributionType.CUSTOM
        ? ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT
        : ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT;

    var behaviour = ProbabilityDistributionTypeBehaviourUtil.getBehaviour(instance, probabilityDistributionType);

    return getValidProbabilityDistributionForm(probabilityDistributionType, behaviour);
  }

  private static ProbabilityDistributionForm getValidProbabilityDistributionForm(
      ProbabilityDistributionType probabilityDistributionType, ProbabilityDistributionTypeBehaviour behaviour) {
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(probabilityDistributionType);

    var probabilityOfExceedanceForm = getValidProbabilityOfExceedanceForm(behaviour);
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);

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

  public static ProbabilityDistributionForm getValidNotCalculatedProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.NOT_CALCULATED);
  }

  public static ProbabilityDistributionForm getValidCustomProbabilityDistributionForm() {
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.CUSTOM);
  }

  public static ProbabilityDistributionForm getValidCustomProbabilityDistributionForm(ProbabilityDistributionFieldInstance instance) {
    var behaviour = ProbabilityDistributionTypeBehaviourUtil.newCustomBehaviourInstance(instance);
    return getValidProbabilityDistributionForm(ProbabilityDistributionType.CUSTOM, behaviour);
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
      case NOT_CALCULATED -> {
        var validForm = getValidNotCalculatedProbabilityDistributionForm();
        validForm.setNotCalculated(probabilityOfExceedanceForm);
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
    probabilityOfExceedanceForm.setOneC(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setMode(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP50(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setTwoC(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setMean(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP10(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setThreeC(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setP1(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setMax(new DecimalInput("abc"));
    probabilityOfExceedanceForm.setConstant(new DecimalInput("abc"));
    return probabilityOfExceedanceForm;
  }

  private static ProbabilityOfExceedanceForm getValidProbabilityOfExceedanceForm(ProbabilityDistributionTypeBehaviour behaviour
  ) {
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    if (behaviour.isMinAvailable()) {
      probabilityOfExceedanceForm.setMin(DecimalInputTestUtil.getDecimalInput(1));
    }
    if (behaviour.isP99Available()) {
      probabilityOfExceedanceForm.setP99(DecimalInputTestUtil.getDecimalInput(2));
    }
    if (behaviour.isP90Available()) {
      probabilityOfExceedanceForm.setP90(DecimalInputTestUtil.getDecimalInput(3));
    }
    if (behaviour.is1CAvailable()) {
      probabilityOfExceedanceForm.setOneC(DecimalInputTestUtil.getDecimalInput(3));
    }
    if (behaviour.isModeAvailable()) {
      probabilityOfExceedanceForm.setMode(DecimalInputTestUtil.getDecimalInput(4));
    }
    if (behaviour.isP50Available()) {
      probabilityOfExceedanceForm.setP50(DecimalInputTestUtil.getDecimalInput(5));
    }
    if (behaviour.is2CAvailable()) {
      probabilityOfExceedanceForm.setTwoC(DecimalInputTestUtil.getDecimalInput(5));
    }
    if (behaviour.isMeanAvailable()) {
      probabilityOfExceedanceForm.setMean(DecimalInputTestUtil.getDecimalInput(6));
    }
    if (behaviour.isP10Available()) {
      probabilityOfExceedanceForm.setP10(DecimalInputTestUtil.getDecimalInput(7));
    }
    if (behaviour.is3CAvailable()) {
      probabilityOfExceedanceForm.setThreeC(DecimalInputTestUtil.getDecimalInput(7));
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
    if (behaviour.isMostLikelyAvailable()) {
      probabilityOfExceedanceForm.setMostLikely(DecimalInputTestUtil.getDecimalInput(5));
    }
    probabilityOfExceedanceForm.setComments("Comments");
    return probabilityOfExceedanceForm;
  }
}
