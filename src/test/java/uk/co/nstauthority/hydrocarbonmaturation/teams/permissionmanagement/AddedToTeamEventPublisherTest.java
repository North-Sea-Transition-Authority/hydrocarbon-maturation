package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;

@ExtendWith(MockitoExtension.class)
class AddedToTeamEventPublisherTest {

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private AddedToTeamEventPublisher addedToTeamEventPublisher;

  @Test
  void publish_verifyEventPublished() {

    var teamId = new TeamId(UUID.randomUUID());
    var addedWebUserAccountId = new WebUserAccountId(123);
    var rolesGranted = Set.of("ROLE_1", "ROLE_2");
    var instigatingUser = UserTestUtil.Builder().build();

    var addedToTeamEventCaptor = ArgumentCaptor.forClass(AddedToTeamEvent.class);

    addedToTeamEventPublisher.publish(teamId, addedWebUserAccountId, rolesGranted, new WebUserAccountId(instigatingUser.wuaId()));

    verify(applicationEventPublisher, times(1)).publishEvent(addedToTeamEventCaptor.capture());

    var addToTeamEvent = addedToTeamEventCaptor.getValue();
    assertThat(addToTeamEvent.getTeamAddedTo()).isEqualTo(teamId);
    assertThat(addToTeamEvent.getAddedUserWebUserAccountId()).isEqualTo(addedWebUserAccountId);
    assertThat(addToTeamEvent.getRolesGranted()).isEqualTo(rolesGranted);
    assertThat(addToTeamEvent.getInstigatingUserWebUserAccountId().id()).isEqualTo(instigatingUser.wuaId());
  }

}
