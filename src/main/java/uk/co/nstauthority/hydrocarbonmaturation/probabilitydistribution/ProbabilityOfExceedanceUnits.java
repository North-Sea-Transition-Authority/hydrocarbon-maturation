package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public enum ProbabilityOfExceedanceUnits {
  M_TVDSS("m tvdss"),
  M_TVT("m tvt"),
  MILLION_M3("million m3"),
  PERCENT("%"),
  KM2("km2"),
  SCF_RCF("scf/rcf"),
  RCF_SCF("rcf/scf"),
  STB_MCF("stb/mcf"),
  RB_STB("rb/stb"),
  SCF_STB("scf/stb"),
  MMSTB("mmstb"),
  MMBOE("mmboe"),
  BCF("bcf"),
  MD("mD")
  ;

  private final String unitDisplay;

  ProbabilityOfExceedanceUnits(String unitDisplay) {
    this.unitDisplay = unitDisplay;
  }

  public String getUnitDisplay() {
    return unitDisplay;
  }
}
