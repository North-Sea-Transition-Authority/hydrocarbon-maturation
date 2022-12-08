package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record LicenceView(String licenceRef, Integer licenceId) implements SearchSelectable {

  @Override
  public String getSelectionId() {
    return licenceId.toString();
  }

  @Override
  public String getSelectionText() {
    return licenceRef;
  }
}
