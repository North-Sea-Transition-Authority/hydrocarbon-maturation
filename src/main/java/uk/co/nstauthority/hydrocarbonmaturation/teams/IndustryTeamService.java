package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;

@Service
public class IndustryTeamService {

  private final TeamQueryService teamQueryService;
  private final OrganisationGroupQueryService organisationGroupQueryService;

  @Autowired
  IndustryTeamService(TeamQueryService teamQueryService, OrganisationGroupQueryService organisationGroupQueryService) {
    this.teamQueryService = teamQueryService;
    this.organisationGroupQueryService = organisationGroupQueryService;
  }

  public List<Integer> getOrganisationGroupIdsForUser(User user) {
    var organisationGroupIds = teamQueryService.getTeamsOfTypeUserIsMemberOf(user.wuaId(), TeamType.INDUSTRY);
    if (organisationGroupIds.isEmpty()) {
      return Collections.emptyList();
    }
    return organisationGroupIds.stream()
        .map(Team::getScopeId)
        .map(Integer::valueOf)
        .toList();
  }

  public List<Integer> getOrganisationGroupIdsForUserWithRoles(User user, Set<Role> roles) {
    var organisationGroupIds = teamQueryService.getTeamsOfTypeUserIsMemberOfWithRoles(
        user.wuaId(),
        TeamType.INDUSTRY,
        roles);
    if (organisationGroupIds.isEmpty()) {
      return Collections.emptyList();
    }
    return organisationGroupIds.stream()
        .map(Team::getScopeId)
        .map(Integer::valueOf)
        .toList();
  }

  public List<Integer> getOrganisationUnitIdsForUser(User user) {
    return organisationGroupQueryService.getOrganisationGroupsByIds(getOrganisationGroupIdsForUser(user))
        .stream()
        .flatMap(organisationGroup -> {
          if (organisationGroup.getOrganisationUnits() == null) {
            return Stream.of();
          }
          return organisationGroup.getOrganisationUnits().stream();
        })
        .filter(Objects::nonNull)
        .map(OrganisationUnit::getOrganisationUnitId)
        .distinct()
        .toList();
  }

  public List<Integer> getOrganisationUnitIdsForUserWithRoles(User user, Set<Role> roles) {
    return organisationGroupQueryService.getOrganisationGroupsByIds(getOrganisationGroupIdsForUserWithRoles(user, roles))
        .stream()
        .flatMap(organisationGroup -> {
          if (organisationGroup.getOrganisationUnits() == null) {
            return Stream.of();
          }
          return organisationGroup.getOrganisationUnits().stream();
        })
        .filter(Objects::nonNull)
        .map(OrganisationUnit::getOrganisationUnitId)
        .distinct()
        .toList();
  }

  public List<EnergyPortalUserDto> getWuaIdsOfUsersWithRoleForOrganisationGroupIds(List<Integer> organisationGroupIds,
                                                                                   Set<Role> roles) {
    var orgGroupIds = organisationGroupIds.stream().map(String::valueOf).collect(Collectors.toSet());
    var orgGroupTeams = teamQueryService.getOrganisationScopedTeams(orgGroupIds);
    return teamQueryService.getUsersInTeamsWithRoles(orgGroupTeams, roles);
  }
}
