package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

public record ReferenceBlockView(
    String quadAndBlockRef,
    String quadrant,
    Integer blockNo,
    OnshoreOffshore onshoreOffshore
) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return quadAndBlockRef;
  }

  @Override
  public String getSelectionText() {
    return quadAndBlockRef;
  }
}
