package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

record ProspectView(UUID generalDetailId, String prospectNameAndCentreOfInterestBlock) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return generalDetailId.toString();
  }

  @Override
  public String getSelectionText() {
    return prospectNameAndCentreOfInterestBlock;
  }
}
