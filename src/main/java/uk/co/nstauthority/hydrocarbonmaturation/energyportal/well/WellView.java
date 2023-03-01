package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record WellView(String id, String registeredNumber) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return id;
  }

  @Override
  public String getSelectionText() {
    return registeredNumber;
  }
}
