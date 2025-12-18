package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public enum ProbabilityOfExceedanceUnits {
  M_TVDSS("m tvdss"),
  M_TVT("m tvt"),
  MILLION_M3("million m3"),
  PERCENT("%"),
  KM2("km2"),
  SCF_RCF("scf/rcf"),
  STB_MMCF("stb/mmcf"),
  RB_STB("rb/stb"),
  SCF_STB("scf/stb"),
  MMSTB("mmstb"),
  MMBBL("mmbbl"),
  MMBOE("mmboe"),
  BCF("bcf"),
  MD("mD"),
  KV_KH("Kv/Kh");

  private final String unitDisplay;

  ProbabilityOfExceedanceUnits(String unitDisplay) {
    this.unitDisplay = unitDisplay;
  }

  public String getUnitDisplay() {
    return unitDisplay;
  }
}
