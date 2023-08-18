package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.fds.addtolist.AddToListItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record ReservoirView(UUID reservoirId, String reservoirName) implements AddToListItem, SearchSelectable {

  @Override
  public String getId() {
    return reservoirId.toString();
  }

  @Override
  public String getName() {
    return reservoirName;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public String getSelectionId() {
    return reservoirId.toString();
  }

  @Override
  public String getSelectionText() {
    return reservoirName;
  }
}
