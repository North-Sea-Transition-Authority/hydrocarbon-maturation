package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamViewComparatorTest {

  @Test
  void sort() {
    var regulatorTeamView = TeamTestUtil.createTeamView(TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withDisplayName("abc")
        .build());

    var industryTeamView1 = TeamTestUtil.createTeamView(TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withDisplayName("xyz")
        .build());

    var industryTeamView2 = TeamTestUtil.createTeamView(TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withDisplayName("def")
        .build());

    var unsortedList = List.of(
        regulatorTeamView,
        industryTeamView1,
        industryTeamView2
    );

    var sortedList = unsortedList.stream()
        .sorted(TeamView.sort())
        .toList();

    assertThat(sortedList).extracting(
        TeamView::teamType,
        TeamView::displayName
    ).containsExactly(
        tuple(
            regulatorTeamView.teamType(),
            regulatorTeamView.displayName()
        ),
        tuple(
            industryTeamView2.teamType(),
            industryTeamView2.displayName()
        ),
        tuple(
            industryTeamView1.teamType(),
            industryTeamView1.displayName()
        )
    );
  }
}
