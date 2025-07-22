package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

public record ResourceGroupSummaryView(List<ResourceItemSummaryView> resourcesInGroup,
                                       ResourceType resourceType,
                                       String isActive) {
  static ResourceGroupSummaryView empty(ResourceType resourceType) {
    return new ResourceGroupSummaryView(List.of(), resourceType, "");
  }
}
