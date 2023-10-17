package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

public enum DepositionalEnvironment {
  AEOLIAN("Aeolian", "Dune, interdune, loess"),
  FLUVIAL_LACSTRINE("Fluvial/lacustrine", "All river morphologies, lakes incl. playa, swamp"),
  DELTA("Delta", "All delta types"),
  MARINE_SHOREFACE_SHELF("Marine shoreface-shelf", "Lower, middle and upper shoreface"),
  CARBONATE_REEF("Carbonate reef", "Rimmed platforms, pinnacle reef"),
  CARBONATE_RAMP("Carbonate ramp", "Carbonate shoals, inner and outer ramp"),
  SUBMARINE_FAN("Submarine fan", "All turbidites, debris flows, slurry flows"),
  DEEPWATER_PELAGIC("Deepwater pelagic", "Clastic and carbonate")
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
