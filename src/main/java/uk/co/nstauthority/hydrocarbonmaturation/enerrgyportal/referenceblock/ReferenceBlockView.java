package uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record ReferenceBlockView(String id, String name) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return id;
  }

  @Override
  public String getSelectionText() {
    return name;
  }
}
