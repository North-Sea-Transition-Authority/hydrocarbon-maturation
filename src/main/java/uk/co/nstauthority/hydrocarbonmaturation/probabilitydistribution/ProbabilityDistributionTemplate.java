package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.List;

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
}
