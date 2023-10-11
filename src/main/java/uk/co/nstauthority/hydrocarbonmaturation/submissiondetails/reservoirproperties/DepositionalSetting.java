package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.List;

public enum DepositionalSetting {
  TERRESTRIAL("Terrestrial", "",
      List.of(DepositionalEnvironment.AEOLIAN, DepositionalEnvironment.FLUVIAL_LACSTRINE)),
  COASTAL("Coastal", "Estuarine, tidal flat, barrier/lagoon, swamp", List.of()),
  SHALLOW_MARINE_CLASTIC("Shallow marine clastic", "",
      List.of(DepositionalEnvironment.DELTA, DepositionalEnvironment.MARINE_SHOREFACE_SHELF)),
  SHALLOW_MARINE_CARBONATE("Shallow marine carbonate", "",
      List.of(DepositionalEnvironment.CARBONATE_REEF, DepositionalEnvironment.CARBONATE_RAMP)),
  DEEP_MARINE("Deep marine", "",
      List.of(DepositionalEnvironment.SUBMARINE_FAN, DepositionalEnvironment.DEEPWATER_PELAGIC)),
  EVAPORITIC("Evaporitic (Restricted salt basin)", "Large-scale evaporite deposition", List.of()),
  INJECTIES("Injectites", "Large-scale remobilisation", List.of()),
  FRACTURED_BASEMENT("Fractured basement", "Tight economic basement", List.of())
  ;

  private final String displayName;

  private final String info;

  private final List<DepositionalEnvironment> depositionalEnvironments;

  DepositionalSetting(String displayName, String info, List<DepositionalEnvironment> depositionalEnvironments) {
    this.displayName = displayName;
    this.info = info;
    this.depositionalEnvironments = depositionalEnvironments;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getInfo() {
    return info;
  }

  public List<DepositionalEnvironment> getDepositionalEnvironments() {
    return depositionalEnvironments;
  }

  public boolean hasDepositionalEnvironments() {
    return !this.depositionalEnvironments.isEmpty();
  }
}
