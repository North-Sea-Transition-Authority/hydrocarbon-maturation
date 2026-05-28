package uk.co.nstauthority.hydrocarbonmaturation.energyportal.user;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
public class AllowedDomainService {

  private final OrganisationGroupQueryService organisationGroupQueryService;

  AllowedDomainService(OrganisationGroupQueryService organisationGroupQueryService) {
    this.organisationGroupQueryService = organisationGroupQueryService;
  }

  public boolean isAllowedDomain(String userEmail, Team team) {
    var group = switch (team.getTeamType()) {
      case TeamType.INDUSTRY -> organisationGroupQueryService
          .getOrganisationGroupById(Integer.parseInt(team.getScopeId()));
      case TeamType.REGULATOR,
           TeamType.ONSHORE_AREA,
           TeamType.NORTHERN_OFFSHORE_AREA,
           TeamType.SOUTHERN_OFFSHORE_AREA -> organisationGroupQueryService.getRegulatorOrganisationGroup();
    };

    var lowerEmail = userEmail.toLowerCase();
    return group.map(OrganisationGroupDto::getEmailDomains).orElse(List.of()).stream()
        .map(String::toLowerCase)
        .anyMatch(domain -> lowerEmail.endsWith("@" + domain));
  }
}