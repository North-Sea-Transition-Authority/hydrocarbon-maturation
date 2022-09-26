package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;
import java.util.Objects;

public class ProbabilityDistributionTemplate {

  private String units;

  private List<ProbabilityDistributionTypeWithInputs> probabilityDistributionTypeWithInputsList;

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public List<ProbabilityDistributionTypeWithInputs> getProbabilityDistributionTypeWithInputsList() {
    return probabilityDistributionTypeWithInputsList;
  }

  public void setProbabilityDistributionTypeWithInputsList(
      List<ProbabilityDistributionTypeWithInputs> probabilityDistributionTypeWithInputsList) {
    this.probabilityDistributionTypeWithInputsList = probabilityDistributionTypeWithInputsList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProbabilityDistributionTemplate)) {
      return false;
    }
    ProbabilityDistributionTemplate that = (ProbabilityDistributionTemplate) o;
    return units.equals(that.units) && probabilityDistributionTypeWithInputsList.equals(
        that.probabilityDistributionTypeWithInputsList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(units, probabilityDistributionTypeWithInputsList);
  }
}
