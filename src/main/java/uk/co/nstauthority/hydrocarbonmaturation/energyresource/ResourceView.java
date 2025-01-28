package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.fds.addtolist.AddToListItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record ResourceView(UUID resourceDetailId,
                           String resourceNameAndCentreOfInterestBlock) implements AddToListItem, SearchSelectable {

  @Override
  public String getId() {
    return resourceDetailId.toString();
  }

  @Override
  public String getName() {
    return resourceNameAndCentreOfInterestBlock;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public String getSelectionId() {
    return resourceDetailId.toString();
  }

  @Override
  public String getSelectionText() {
    return resourceNameAndCentreOfInterestBlock;
  }
}
