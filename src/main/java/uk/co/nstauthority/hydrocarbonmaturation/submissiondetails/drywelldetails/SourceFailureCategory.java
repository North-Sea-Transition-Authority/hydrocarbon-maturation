package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum SourceFailureCategory implements Displayable {
  SOURCE_NOT_PRESENT("Source not present"),
  SOURCE_NOT_MATURE("Source not mature"),
  NOT_KNOWN("Not known")
  ;

  private final String displayName;

  SourceFailureCategory(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
