package uk.co.nstauthority.hydrocarbonmaturation.energyportal.access;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddedToTeamEvent;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RemovedFromTeamEvent;

@Component
class EnergyPortalAccessEventListener {


  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserAddedToTeam(AddedToTeamEvent event) {

    //TODO send emails on adding a user to a team
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserRemovedFromTeam(RemovedFromTeamEvent event) {

    //TODO send email to instigating user on removing a member of a team
  }
}
