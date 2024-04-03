package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;

enum ProjectCapexSource implements Displayable {
  DRAFT_CSR("Draft CSR", 10),
  APPROVED_CSR("Approved CSR", 20),
  DRAFT_FDP("Draft FDP", 30, Set.of(ProjectType.FIELD_DEVELOPMENT_PLAN)),
  APPROVED_FDP("Approved FDP", 40, Set.of(ProjectType.FIELD_DEVELOPMENT_PLAN)),
  DRAFT_FDPA("Draft FDPA", 50, Set.of(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM)),
  APPROVED_FDPA("Approved FDPA", 60, Set.of(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM)),
  UKSS("UKSS", 70);

  private final String displayName;
  private final int displayOrder;
  private final Set<ProjectType> projectTypes;

  ProjectCapexSource(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.projectTypes = Arrays.stream(ProjectType.values()).collect(Collectors.toSet());
  }

  ProjectCapexSource(String displayName, int displayOrder, Set<ProjectType> projectTypes) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.projectTypes = projectTypes;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  public static Map<String, String> getDisplayableOptionsForProjectType(ProjectType projectType) {
    var projectCapexSources = Arrays.stream(ProjectCapexSource.values())
        .filter(projectCapexSource -> projectCapexSource.projectTypes.contains(projectType))
        .toList();
    return DisplayableEnumOptionUtil.getDisplayableOptions(projectCapexSources);
  }
}
