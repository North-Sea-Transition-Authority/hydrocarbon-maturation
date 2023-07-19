package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.Objects;

public class ProbabilityDistributionTypeInput {

  private ProbabilityOfExceedanceType probabilityOfExceedanceType;
  private String prompt;
  private boolean isRequired;

  public ProbabilityOfExceedanceType getProbabilityOfExceedanceType() {
    return probabilityOfExceedanceType;
  }

  public void setProbabilityOfExceedanceType(ProbabilityOfExceedanceType probabilityOfExceedanceType) {
    this.probabilityOfExceedanceType = probabilityOfExceedanceType;
  }

  public boolean getRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;
  }

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProbabilityDistributionTypeInput that = (ProbabilityDistributionTypeInput) o;
    return isRequired == that.isRequired && probabilityOfExceedanceType == that.probabilityOfExceedanceType
        && Objects.equals(prompt, that.prompt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(probabilityOfExceedanceType, prompt, isRequired);
  }
}
