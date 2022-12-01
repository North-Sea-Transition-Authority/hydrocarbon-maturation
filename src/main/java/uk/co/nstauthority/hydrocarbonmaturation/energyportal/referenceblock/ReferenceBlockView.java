package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record ReferenceBlockView(String blockRef,
                                 String sortKey) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return blockRef;
  }

  @Override
  public String getSelectionText() {
    return blockRef;
  }
}
