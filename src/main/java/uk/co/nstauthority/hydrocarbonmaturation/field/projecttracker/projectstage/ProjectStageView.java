package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

public record ProjectStageView(String stage, String date) {
  public static ProjectStageView from(ProjectStageHistory projectStageHistory) {
    return new ProjectStageView(
        projectStageHistory.getStage().getDisplayName(),
        DateUtil.formatLongDate(projectStageHistory.getStageDate())
    );
  }
}
