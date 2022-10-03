package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class ProbabilityDistributionForm {

  private ProbabilityDistributionType probabilityDistributionType;
  private ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance;

  private ProbabilityOfExceedanceForm normal = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm lognormal = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm beta = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm triangular = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm rectangular = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm constant = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm custom = new ProbabilityOfExceedanceForm();

  public ProbabilityDistributionType getProbabilityDistributionType() {
    return probabilityDistributionType;
  }

  public void setProbabilityDistributionType(
      ProbabilityDistributionType probabilityDistributionType) {
    this.probabilityDistributionType = probabilityDistributionType;
  }

  public ProbabilityDistributionFieldInstance getProbabilityDistributionFieldInstance() {
    return probabilityDistributionFieldInstance;
  }

  public void setProbabilityDistributionFieldInstance(
      ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance) {
    this.probabilityDistributionFieldInstance = probabilityDistributionFieldInstance;
  }

  public ProbabilityOfExceedanceForm getNormal() {
    return normal;
  }

  public void setNormal(ProbabilityOfExceedanceForm normal) {
    this.normal = normal;
  }

  public ProbabilityOfExceedanceForm getLognormal() {
    return lognormal;
  }

  public void setLognormal(
      ProbabilityOfExceedanceForm lognormal) {
    this.lognormal = lognormal;
  }

  public ProbabilityOfExceedanceForm getBeta() {
    return beta;
  }

  public void setBeta(ProbabilityOfExceedanceForm beta) {
    this.beta = beta;
  }

  public ProbabilityOfExceedanceForm getTriangular() {
    return triangular;
  }

  public void setTriangular(
      ProbabilityOfExceedanceForm triangular) {
    this.triangular = triangular;
  }

  public ProbabilityOfExceedanceForm getRectangular() {
    return rectangular;
  }

  public void setRectangular(
      ProbabilityOfExceedanceForm rectangular) {
    this.rectangular = rectangular;
  }

  public ProbabilityOfExceedanceForm getConstant() {
    return constant;
  }

  public void setConstant(
      ProbabilityOfExceedanceForm constant) {
    this.constant = constant;
  }

  public ProbabilityOfExceedanceForm getCustom() {
    return custom;
  }

  public void setCustom(ProbabilityOfExceedanceForm custom) {
    this.custom = custom;
  }

  public ProbabilityOfExceedanceForm getProbabilityOfExceedanceForm() {
    return switch (probabilityDistributionType) {
      case NORMAL -> normal;
      case LOGNORMAL -> lognormal;
      case BETA -> beta;
      case TRIANGULAR -> triangular;
      case RECTANGULAR -> rectangular;
      case CONSTANT -> constant;
      case CUSTOM -> custom;
    };
  }

  public void setProbabilityOfExceedanceForm(ProbabilityOfExceedanceForm exceedanceForm) {
    switch (probabilityDistributionType) {
      case NORMAL -> setNormal(exceedanceForm);
      case LOGNORMAL -> setLognormal(exceedanceForm);
      case BETA -> setBeta(exceedanceForm);
      case TRIANGULAR -> setTriangular(exceedanceForm);
      case RECTANGULAR -> setRectangular(exceedanceForm);
      case CONSTANT -> setConstant(exceedanceForm);
      case CUSTOM -> setCustom(exceedanceForm);
      default -> throw new IllegalStateException("Unexpected value: " + probabilityDistributionType);
    }
  }
}
