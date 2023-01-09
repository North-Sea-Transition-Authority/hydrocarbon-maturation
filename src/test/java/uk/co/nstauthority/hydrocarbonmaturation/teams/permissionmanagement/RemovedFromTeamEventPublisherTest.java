package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;

@ExtendWith(MockitoExtension.class)
class RemovedFromTeamEventPublisherTest {

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private RemovedFromTeamEventPublisher removedFromTeamEventPublisher;

  @Test
  void publish_verifyEventPublished() {

    var removedTeamMember = TeamMemberTestUtil.Builder().build();
    var instigatingUser = ServiceUserDetailTestUtil.Builder().build();

    var eventArgumentCaptor = ArgumentCaptor.forClass(RemovedFromTeamEvent.class);

    removedFromTeamEventPublisher.publish(removedTeamMember, new WebUserAccountId(instigatingUser.wuaId()));

    verify(applicationEventPublisher, times(1)).publishEvent(eventArgumentCaptor.capture());

    var teamMemberRemovedEvent = eventArgumentCaptor.getValue();
    assertThat(teamMemberRemovedEvent.getTeamMember()).isEqualTo(removedTeamMember);
    assertThat(teamMemberRemovedEvent.getInstigatingUserWebUserAccountId().id()).isEqualTo(instigatingUser.wuaId());
  }

}
