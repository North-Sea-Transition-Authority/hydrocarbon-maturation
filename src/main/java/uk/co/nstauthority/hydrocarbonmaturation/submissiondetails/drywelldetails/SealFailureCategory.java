package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum SealFailureCategory implements Displayable {
  SEAL_LITHOLOGY_NOT_PRESENT("Sealing lithology not present"),
  SEAL_LITHOLOGY_PRESENT_POOR_QUALITY("Sealing lithology present but of poor quality"),
  SEAL_LITHOLOGY_PRESENT_POTENTIALLY_BREACHED("Sealing lithology present but potentially breached " +
      "(such as faulting or hydraulic fracturing)"),
  NOT_KNOWN("Not known")
  ;

  private final String displayName;

  SealFailureCategory(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
