package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class ProbabilityDistributionForm {

  private ProbabilityDistributionType probabilityDistributionType;

  private ProbabilityOfExceedanceForm normal = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm lognormal = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm beta = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm triangular = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm rectangular = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm constant = new ProbabilityOfExceedanceForm();

  private ProbabilityOfExceedanceForm notCalculated = new ProbabilityOfExceedanceForm();

  public ProbabilityDistributionType getProbabilityDistributionType() {
    return probabilityDistributionType;
  }

  public void setProbabilityDistributionType(
      ProbabilityDistributionType probabilityDistributionType) {
    this.probabilityDistributionType = probabilityDistributionType;
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

  public ProbabilityOfExceedanceForm getNotCalculated() {
    return notCalculated;
  }

  public void setNotCalculated(
      ProbabilityOfExceedanceForm notCalculated) {
    this.notCalculated = notCalculated;
  }

  public ProbabilityOfExceedanceForm getProbabilityOfExceedanceForm() {
    return switch (probabilityDistributionType) {
      case NORMAL -> normal;
      case LOGNORMAL -> lognormal;
      case BETA -> beta;
      case TRIANGULAR -> triangular;
      case RECTANGULAR -> rectangular;
      case CONSTANT -> constant;
      case NOT_CALCULATED -> notCalculated;
    };
  }
}
