package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

record ResourceView(UUID generalDetailId, String resourceNameAndCentreOfInterestBlock) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return generalDetailId.toString();
  }

  @Override
  public String getSelectionText() {
    return resourceNameAndCentreOfInterestBlock;
  }
}
