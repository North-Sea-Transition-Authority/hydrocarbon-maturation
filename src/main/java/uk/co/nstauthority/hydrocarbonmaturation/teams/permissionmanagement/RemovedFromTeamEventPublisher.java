package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;

@Component
public class RemovedFromTeamEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public RemovedFromTeamEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publish(TeamMember teamMember,
                      WebUserAccountId instigatingUser) {
    var event = new RemovedFromTeamEvent(this, teamMember, instigatingUser);
    applicationEventPublisher.publishEvent(event);
  }
}
