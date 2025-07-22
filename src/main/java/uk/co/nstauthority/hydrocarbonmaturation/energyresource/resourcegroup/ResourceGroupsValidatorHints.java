package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

record ResourceGroupsValidatorHints(Resource resource, UUID selectedGroupId) {
}
