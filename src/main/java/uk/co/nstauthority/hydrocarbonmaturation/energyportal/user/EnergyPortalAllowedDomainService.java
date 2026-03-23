package uk.co.nstauthority.hydrocarbonmaturation.energyportal.user;


import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
@Profile("use-service-access-request")
public class EnergyPortalAllowedDomainService implements AllowedDomainService {

  private final OrganisationGroupQueryService organisationGroupQueryService;

  EnergyPortalAllowedDomainService(OrganisationGroupQueryService organisationGroupQueryService) {
    this.organisationGroupQueryService = organisationGroupQueryService;
  }

  @Override
  public boolean isAllowedDomain(String userEmail, Team team) {
    Optional<OrganisationGroupDto> group;
    switch (team.getTeamType()) {
      case TeamType.INDUSTRY -> group = organisationGroupQueryService
          .getOrganisationGroupById(Integer.parseInt(team.getScopeId()));
      case TeamType.REGULATOR,
           TeamType.ONSHORE_AREA,
           TeamType.NORTHERN_OFFSHORE_AREA,
           TeamType.SOUTHERN_OFFSHORE_AREA -> group = organisationGroupQueryService.getRegulatorOrganisationGroup();
      default -> throw new IllegalStateException("Unexpected value: " + team.getTeamType());
    }

    List<String> emailDomains = List.of();
    if (group.isPresent()) {
      emailDomains = group.get().getEmailDomains();
    }

    return emailDomains.stream()
        .anyMatch(domain -> userEmail.toLowerCase().endsWith(domain));
  }
}