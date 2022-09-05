package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class ProbabilityDistributionTypeInput {

  private ProbabilityOfExceedanceType probabilityOfExceedanceType;
  private boolean isRequired;

  public ProbabilityOfExceedanceType getProbabilityOfExceedanceType() {
    return probabilityOfExceedanceType;
  }

  public void setProbabilityOfExceedanceType(
      ProbabilityOfExceedanceType probabilityOfExceedanceType) {
    this.probabilityOfExceedanceType = probabilityOfExceedanceType;
  }

  public boolean getRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;
  }
}
