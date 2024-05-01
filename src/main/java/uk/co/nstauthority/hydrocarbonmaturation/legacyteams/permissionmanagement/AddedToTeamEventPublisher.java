package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;

@Component
public class AddedToTeamEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public AddedToTeamEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publish(TeamId teamAddedTo,
                      WebUserAccountId addedUserWebUserAccountId,
                      Set<String> rolesGranted,
                      WebUserAccountId instigatingUser) {
    applicationEventPublisher.publishEvent(
        new AddedToTeamEvent(
            this,
            teamAddedTo,
            addedUserWebUserAccountId,
            instigatingUser,
            rolesGranted
        )
    );
  }
}
