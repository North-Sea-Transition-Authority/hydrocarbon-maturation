package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record ReservoirSubUnitDto(String subUnitValue, String subUnitDisplay) implements SearchSelectable {

  @Override
  public String getSelectionId() {
    return subUnitValue;
  }

  @Override
  public String getSelectionText() {
    return subUnitDisplay;
  }
}
