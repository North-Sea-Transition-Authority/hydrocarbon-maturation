package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;

class ProjectCapexSourceTest {
  @Test
  void getDisplayableOptionsForProjectType_fdp() {
    assertThat(ProjectCapexSource.getDisplayableOptionsForProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN))
        .isEqualTo(
            DisplayableEnumOptionUtil.getDisplayableOptions(
                List.of(
                    ProjectCapexSource.DRAFT_CSR,
                    ProjectCapexSource.APPROVED_CSR,
                    ProjectCapexSource.DRAFT_FDP,
                    ProjectCapexSource.APPROVED_FDP,
                    ProjectCapexSource.UKSS
                ))
        );
  }

  @Test
  void getDisplayableOptionsForProjectType_fdpa() {
    assertThat(ProjectCapexSource.getDisplayableOptionsForProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM))
        .isEqualTo(
            DisplayableEnumOptionUtil.getDisplayableOptions(
                List.of(
                    ProjectCapexSource.DRAFT_CSR,
                    ProjectCapexSource.APPROVED_CSR,
                    ProjectCapexSource.DRAFT_FDPA,
                    ProjectCapexSource.APPROVED_FDPA,
                    ProjectCapexSource.UKSS
                ))
        );
  }

  @Test
  void getDisplayableOptionsForProjectType_cop() {
    assertThat(ProjectCapexSource.getDisplayableOptionsForProjectType(ProjectType.CESSATION_OF_PRODUCTION))
        .isEqualTo(
            DisplayableEnumOptionUtil.getDisplayableOptions(
                List.of(
                    ProjectCapexSource.DRAFT_CSR,
                    ProjectCapexSource.APPROVED_CSR,
                    ProjectCapexSource.UKSS
                ))
        );
  }
}