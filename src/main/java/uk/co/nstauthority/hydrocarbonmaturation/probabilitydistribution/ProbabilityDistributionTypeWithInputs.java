package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProbabilityDistributionTypeWithInputs)) {
      return false;
    }
    ProbabilityDistributionTypeWithInputs that = (ProbabilityDistributionTypeWithInputs) o;
    return probabilityDistributionType == that.probabilityDistributionType && probabilityDistributionTypeInputs.equals(
        that.probabilityDistributionTypeInputs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(probabilityDistributionType, probabilityDistributionTypeInputs);
  }
}
