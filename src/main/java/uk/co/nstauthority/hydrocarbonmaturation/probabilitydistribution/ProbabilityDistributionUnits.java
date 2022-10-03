package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public enum ProbabilityDistributionUnits {
  FT_TVDSS("ft tvdss"),
  M_TVT("m tvt"),
  MILLION_M3("million m3"),
  PERCENT("%"),
  KM2("km2");

  private final String unitDisplay;

  ProbabilityDistributionUnits(String unitDisplay) {
    this.unitDisplay = unitDisplay;
  }

  public String getUnitDisplay() {
    return unitDisplay;
  }
}
