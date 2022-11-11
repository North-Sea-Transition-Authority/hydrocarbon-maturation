package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record ReferenceWellView(String id,
                                String reference) implements SearchSelectable {
  public static ReferenceWellView fromWellReference(String wellRef) {
    return new ReferenceWellView(wellRef, wellRef);
  }

  @Override
  public String getSelectionId() {
    return id;
  }

  @Override
  public String getSelectionText() {
    return reference;
  }
}
