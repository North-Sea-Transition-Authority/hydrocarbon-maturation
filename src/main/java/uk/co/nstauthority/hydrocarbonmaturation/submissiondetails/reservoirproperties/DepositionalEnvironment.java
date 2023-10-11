package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

public enum DepositionalEnvironment {
  AEOLIAN("Aeolian", "dune, interdune, loess"),
  FLUVIAL_LACSTRINE("Fluvial/lacustrine", "all river morphologies, lakes incl. playa, swamp"),
  DELTA("Delta", "all delta types"),
  MARINE_SHOREFACE_SHELF("Marine shoreface-shelf", "lower, middle and upper shoreface"),
  CARBONATE_REEF("Carbonate reef", "rimmed platforms, pinnacle reef"),
  CARBONATE_RAMP("Carbonate ramp", "carbonate shoals, inner and outer ramp"),
  SUBMARINE_FAN("Submarine fan", "all turbidites, debris flows, slurry flows"),
  DEEPWATER_PELAGIC("Deepwater pelagic", "clastic and carbonate")
  ;

  private final String displayName;

  private final String info;

  DepositionalEnvironment(String displayName, String info) {
    this.displayName = displayName;
    this.info = info;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getInfo() {
    return info;
  }
}
