package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum AverageMethod implements Displayable {
  ARITHMETIC("Arithmetic"),
  GEOMETRIC("Geometric"),
  HARMONIC("Harmonic")
  ;

  private final String display;

  AverageMethod(String display) {
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
