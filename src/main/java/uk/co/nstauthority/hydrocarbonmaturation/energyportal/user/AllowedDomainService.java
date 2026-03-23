package uk.co.nstauthority.hydrocarbonmaturation.energyportal.user;

import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;

public interface AllowedDomainService {

  boolean isAllowedDomain(String domain, Team team);
}