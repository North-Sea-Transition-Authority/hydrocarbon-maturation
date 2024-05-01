package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;

@Service
public class TeamService {

  private final LegacyTeamRepository legacyTeamRepository;
  private final TeamMemberRoleRepository teamMemberRoleRepository;

  @Autowired
  TeamService(LegacyTeamRepository legacyTeamRepository, TeamMemberRoleRepository teamMemberRoleRepository) {
    this.legacyTeamRepository = legacyTeamRepository;
    this.teamMemberRoleRepository = teamMemberRoleRepository;
  }

  public Optional<LegacyTeam> getTeam(TeamId teamId, TeamType teamType) {
    return legacyTeamRepository.findByUuidAndTeamType(teamId.uuid(), teamType);
  }

  public Optional<LegacyTeam> getTeam(TeamId teamId) {
    return legacyTeamRepository.findById(teamId.uuid());
  }

  public List<LegacyTeam> getTeamsOfTypeThatUserBelongsTo(User user, TeamType teamType) {
    return legacyTeamRepository.findAllTeamsOfTypeThatUserIsMemberOf(user.wuaId(), teamType);
  }

  public List<LegacyTeam> getRegulatorTeams() {
    return legacyTeamRepository.findAllByTeamType(TeamType.REGULATOR);
  }

  public List<LegacyTeam> getTeamsThatUserBelongsTo(User user) {
    return teamMemberRoleRepository.findAllByWuaId(user.wuaId()).stream()
        .map(TeamMemberRole::getTeam)
        .distinct()
        .toList();
  }

  public boolean isUserInMultipleTeams(User user) {
    return getTeamsThatUserBelongsTo(user).size() > 1;
  }

  public void createTeam(LegacyTeam legacyTeam) {
    legacyTeamRepository.save(legacyTeam);
  }

  public Optional<LegacyTeam> getTeamByOrganisationGroupId(int organisationGroupId) {
    return legacyTeamRepository.findByOrganisationGroupId(organisationGroupId);
  }

  public List<Integer> getOrganisationGroupsUserIsTeamMemberOf(User user) {
    return teamMemberRoleRepository.findAllByWuaId(user.wuaId())
        .stream()
        .map(teamMemberRole -> teamMemberRole.getTeam().getOrganisationGroupId())
        .distinct()
        .filter(Objects::nonNull)
        .toList();
  }

  public List<LegacyTeam> getAllTeamOfTypes(Set<TeamType> teamTypes) {
    return legacyTeamRepository.findAll()
        .stream()
        .filter(team -> teamTypes.contains(team.getTeamType()))
        .toList();
  }
}
