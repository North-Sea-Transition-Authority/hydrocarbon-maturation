package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum TrapFailureCategory implements Displayable {
  TRAP_CLOSURE_NOT_CONSISTENT("Trap closure not consistent with pre-drill estimates"),
  DEPTH_CLOSURE_NOT_PRESENT("Depth closure not present"),
  STRATIGRAPHIC_TRAP_COMPONENTS_NOT_PRESENT("Stratigraphic trap components not present"),
  NOT_KNOWN("Not known")
  ;

  private final String displayName;

  TrapFailureCategory(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
