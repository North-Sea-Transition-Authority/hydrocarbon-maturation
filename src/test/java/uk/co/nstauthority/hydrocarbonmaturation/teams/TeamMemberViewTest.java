package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TeamMemberViewTest {

  @Test
  void getDisplayName() {

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withFirstName("Forename")
        .withLastName("Surname")
        .build();

    assertThat(teamMemberView.getDisplayName()).isEqualTo("Forename Surname");
  }
}
