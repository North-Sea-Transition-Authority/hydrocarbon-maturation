package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;

public class ProbabilityDistributionTypeWithInputs {

  private ProbabilityDistributionType probabilityDistributionType;

  private List<ProbabilityDistributionTypeInput> probabilityDistributionTypeInputs;

  public ProbabilityDistributionType getProbabilityDistributionType() {
    return probabilityDistributionType;
  }

  public void setProbabilityDistributionType(
      ProbabilityDistributionType probabilityDistributionType) {
    this.probabilityDistributionType = probabilityDistributionType;
  }

  public List<ProbabilityDistributionTypeInput> getProbabilityDistributionTypeInputs() {
    return probabilityDistributionTypeInputs;
  }

  public void setProbabilityDistributionTypeInputs(
      List<ProbabilityDistributionTypeInput> probabilityDistributionTypeInputs) {
    this.probabilityDistributionTypeInputs = probabilityDistributionTypeInputs;
  }
}
