package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.ScopedTeamManagementController;

public enum TeamType {

  REGULATOR(
      "NSTA",
      "regulator",
      false,
      List.of(
          Role.MANAGE_TEAM,
          Role.REGULATOR_MANAGE_ALL_ORGANISATIONS,
          Role.EXPLORATION,
          Role.VIEW_ANY_RESOURCE,
          Role.RESERVES_AND_RESOURCES_ADVISOR,
          Role.PROJECT_ANALYST),
      null
  ),
  NORTHERN_OFFSHORE_AREA(
      "Northern North Sea and West of Shetland",
      "northern-offshore-area",
      false,
      List.of(Role.MANAGE_TEAM, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      null
  ),
  SOUTHERN_OFFSHORE_AREA(
      "Central North Sea, Southern North Sea and East Irish Sea",
      "southern-offshore-area",
      false,
      List.of(Role.MANAGE_TEAM, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      null
  ),
  ONSHORE_AREA(
      "Onshore area",
      "onshore-area",
      false,
      List.of(Role.MANAGE_TEAM, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
      null
  ),
  INDUSTRY(
      "Industry",
      "industry",
      true,
      List.of(Role.MANAGE_TEAM, Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER),
          () -> ReverseRouter.route(on(ScopedTeamManagementController.class).renderCreateNewOrgTeam(null))
  );

  private final String displayName;
  private final String urlSlug;
  private final boolean isScoped;
  private final List<Role> allowedRoles;
  private final Supplier<String> createNewInstanceRoute;

  TeamType(String displayName,
           String urlSlug,
           boolean isScoped,
           List<Role> allowedRoles,
           Supplier<String> createNewInstanceRoute) {
    this.displayName = displayName;
    this.urlSlug = urlSlug;
    this.isScoped = isScoped;
    this.allowedRoles = allowedRoles;
    this.createNewInstanceRoute = createNewInstanceRoute;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getUrlSlug() {
    return urlSlug;
  }

  public boolean isScoped() {
    return isScoped;
  }

  public List<Role> getAllowedRoles() {
    return allowedRoles;
  }

  public String getCreateNewInstanceRoute() {
    return createNewInstanceRoute.get();
  }

  public static Optional<TeamType> fromUrlSlug(String urlSlug) {
    return Arrays.stream(values())
        .filter(teamType -> teamType.urlSlug.equals(urlSlug))
        .findFirst();
  }

}
