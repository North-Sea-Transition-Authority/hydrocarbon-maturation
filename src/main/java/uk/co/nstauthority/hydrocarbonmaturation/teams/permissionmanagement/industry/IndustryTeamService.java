package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
public class IndustryTeamService {

  private final TeamService teamService;
  private final TeamMemberService teamMemberService;
  private final TeamMemberRoleService teamMemberRoleService;
  private final OrganisationGroupQueryService organisationGroupQueryService;

  @Autowired
  IndustryTeamService(TeamService teamService,
                      TeamMemberService teamMemberService,
                      TeamMemberRoleService teamMemberRoleService,
                      OrganisationGroupQueryService organisationGroupQueryService) {
    this.teamService = teamService;
    this.teamMemberService = teamMemberService;
    this.teamMemberRoleService = teamMemberRoleService;
    this.organisationGroupQueryService = organisationGroupQueryService;
  }

  public boolean isAccessManager(TeamId teamId, User user) {
    return teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.ACCESS_MANAGER.name()));
  }

  public boolean isSubmitter(TeamId teamId, User user) {
    return teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.SUBMITTER.name()));
  }

  public boolean isSubmitter(User user) {
    return getIndustryTeamForUser(user)
        .map(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            Set.of(IndustryTeamRole.SUBMITTER.name())))
        .orElse(false);
  }

  Optional<Team> getTeam(TeamId teamId) {
    return teamService.getTeam(teamId, TeamType.INDUSTRY);
  }

  void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<IndustryTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);

    teamMemberRoleService.addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  public boolean isSubmitterForOrganisationGroups(User user, List<Integer> organisationGroupIds) {

    return isTeamMemberForOrganisationGroups(organisationGroupIds, team -> isSubmitter(team.toTeamId(), user));
  }

  boolean isTeamMemberForOrganisationGroups(User user, List<Integer> organisationGroupIds) {

    return isTeamMemberForOrganisationGroups(
        organisationGroupIds,
        team -> teamMemberService.isMemberOfTeam(team.toTeamId(), user)
    );
  }

  private boolean isTeamMemberForOrganisationGroups(List<Integer> organisationGroupIds, Predicate<Team> membershipPredicate) {
    return organisationGroupIds.stream()
        .map(this::getTeamByOrganisationGroupId)
        .flatMap(Optional::stream)
        .anyMatch(membershipPredicate);
  }

  @Transactional
  public Team createTeam(String groupName, int organisationGroupId) {
    var team = new Team();
    team.setTeamType(TeamType.INDUSTRY);
    team.setDisplayName(groupName);
    team.setOrganisationGroupId(organisationGroupId);
    teamService.createTeam(team);
    return team;
  }

  public Optional<Team> getTeamByOrganisationGroupId(int organisationGroupId) {
    return teamService.getTeamByOrganisationGroupId(organisationGroupId);
  }

  public List<Integer> getOrganisationGroupIdsForUser(User user) {
    return teamService.getOrganisationGroupsUserIsTeamMemberOf(user);
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

  public List<Integer> getOrganisationUnitIdsForUserWithIndustryTeamRoles(User user,
                                                                          Set<IndustryTeamRole> industryTeamRoles) {
    var organisationGroupIdsForUser = getOrganisationGroupIdsWhereUserHasTeamRole(
        user, industryTeamRoles);
    if (organisationGroupIdsForUser.isEmpty()) {
      return List.of();
    }
    return organisationGroupQueryService
        .getOrganisationGroupsByIds(organisationGroupIdsForUser)
        .stream()
        .flatMap(organisationGroup -> {
          if (organisationGroup.getOrganisationUnits() == null) {
            return Stream.of();
          } else {
            return organisationGroup.getOrganisationUnits().stream();
          }
        })
        .filter(Objects::nonNull)
        .map(OrganisationUnit::getOrganisationUnitId)
        .distinct()
        .toList();
  }

  public List<Long> getWuaIdsOfUsersWithRoleForOrganisationGroupIds(List<Integer> organisationGroupIds,
                                                                    Set<IndustryTeamRole> industryTeamRoles) {
    var listOfTeams = organisationGroupIds
        .stream()
        .map(teamService::getTeamByOrganisationGroupId)
        .flatMap(Optional::stream)
        .toList();
    return teamMemberRoleService.getTeamMembersWithRoleInTeams(listOfTeams, getRolesAsStrings(industryTeamRoles))
        .stream()
        .map(TeamMemberRole::getWuaId)
        .toList();
  }

  private List<Integer> getOrganisationGroupIdsWhereUserHasTeamRole(User user,
                                                                    Set<IndustryTeamRole> roles) {
    var rolesAsStrings = getRolesAsStrings(roles);
    return getOrganisationGroupIdsForUser(user)
        .stream()
        .map(this::getTeamByOrganisationGroupId)
        .flatMap(Optional::stream)
        .filter(team -> teamMemberService.isMemberOfTeamWithAnyRoleOf(
            team.toTeamId(),
            user,
            rolesAsStrings))
        .map(Team::getOrganisationGroupId)
        .toList();
  }

  private Set<String> getRolesAsStrings(Set<IndustryTeamRole> industryTeamRoles) {
    return industryTeamRoles.stream()
        .map(IndustryTeamRole::name)
        .collect(Collectors.toSet());
  }

  private Optional<Team> getIndustryTeamForUser(User user) {
    return teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.INDUSTRY)
        .stream()
        .findFirst();
  }
}
