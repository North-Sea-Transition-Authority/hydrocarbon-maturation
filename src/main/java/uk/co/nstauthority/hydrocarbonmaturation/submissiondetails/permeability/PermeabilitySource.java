package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum PermeabilitySource implements Displayable {

  CONVENTIONAL("Conventional core"),
  SIDEWALL("Sidewall core"),
  FORMATION_PRESSURE_TESTER("Formation Pressure Tester"),
  NMR("NMR"),
  LOG_TRANSFORM("Log transform"),
  OTHER("Other")
  ;
  private final String display;

  PermeabilitySource(String display) {
    this.display = display;
  }

  @Override
  public String getDisplayName() {
    return display;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}


