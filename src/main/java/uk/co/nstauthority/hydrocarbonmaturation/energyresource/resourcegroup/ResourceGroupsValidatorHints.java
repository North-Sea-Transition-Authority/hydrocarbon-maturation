package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

record ResourceGroupsValidatorHints(Resource resource, ResourceGroupLink currentResourceGroupLink,
                                    List<ResourceGroupLink> linksWithResource, UUID selectedGroupId) {
}
