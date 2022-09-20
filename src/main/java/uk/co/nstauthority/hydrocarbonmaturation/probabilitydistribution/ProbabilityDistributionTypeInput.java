package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProbabilityDistributionTypeInput)) {
      return false;
    }
    ProbabilityDistributionTypeInput input = (ProbabilityDistributionTypeInput) o;
    return isRequired == input.isRequired && probabilityOfExceedanceType == input.probabilityOfExceedanceType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(probabilityOfExceedanceType, isRequired);
  }
}
