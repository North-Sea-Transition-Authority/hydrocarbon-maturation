package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LegacyTeamMemberViewTest {

  @Test
  void getDisplayName() {

    var teamMemberView = TeamMemberViewTestUtil.Builder()
        .withFirstName("Forename")
        .withLastName("Surname")
        .build();

    assertThat(teamMemberView.getDisplayName()).isEqualTo("Forename Surname");
  }
}
