package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public interface TeamRole extends Displayable {

  Set<RolePermission> getRolePermissions();

}
