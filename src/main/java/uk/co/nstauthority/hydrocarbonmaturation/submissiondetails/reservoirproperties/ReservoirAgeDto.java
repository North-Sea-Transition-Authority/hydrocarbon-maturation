package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record ReservoirAgeDto(String reservoirAgeId, String reservoirAgeDisplay) implements SearchSelectable {

  @Override
  public String getSelectionId() {
    return reservoirAgeId;
  }

  @Override
  public String getSelectionText() {
    return reservoirAgeDisplay;
  }
}