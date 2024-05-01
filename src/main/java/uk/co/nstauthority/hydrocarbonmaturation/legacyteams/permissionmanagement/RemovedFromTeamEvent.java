package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import java.io.Serial;
import org.springframework.context.ApplicationEvent;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;

public class RemovedFromTeamEvent extends ApplicationEvent {

  @Serial
  private static final long serialVersionUID = 7459965432418347644L;
  private final TeamMember teamMember;
  private final WebUserAccountId instigatingUserWebUserAccountId;

  public RemovedFromTeamEvent(Object source,
                              TeamMember teamMember,
                              WebUserAccountId instigatingUserWebUserAccountId) {
    super(source);
    this.teamMember = teamMember;
    this.instigatingUserWebUserAccountId = instigatingUserWebUserAccountId;
  }

  public TeamMember getTeamMember() {
    return teamMember;
  }

  public WebUserAccountId getInstigatingUserWebUserAccountId() {
    return instigatingUserWebUserAccountId;
  }
}
