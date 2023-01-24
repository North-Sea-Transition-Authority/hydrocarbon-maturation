package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

record ResourceGroupView(UUID submissionDetailId, String resourceName, UUID resourceGroupId) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return submissionDetailId.toString();
  }

  @Override
  public String getSelectionText() {
    return resourceName;
  }
}
