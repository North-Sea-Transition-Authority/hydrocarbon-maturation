package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class ProbabilityDistributionForm {

  private ProbabilityDistributionType probabilityDistributionType;

  private ProbabilityOfExceedanceForm normal;

  private ProbabilityOfExceedanceForm lognormal;

  private ProbabilityOfExceedanceForm beta;

  private ProbabilityOfExceedanceForm triangular;

  private ProbabilityOfExceedanceForm rectangular;

  private ProbabilityOfExceedanceForm constant;

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
}
