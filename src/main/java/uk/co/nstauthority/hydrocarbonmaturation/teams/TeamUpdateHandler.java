package uk.co.nstauthority.hydrocarbonmaturation.teams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.fivium.energyportal.starter.organisationgroup.EnergyPortalOrganisationGroupConsumer;
import uk.co.fivium.energyportal.starter.organisationgroup.EnergyPortalOrganisationGroupEvent;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementService;

@Component
class TeamUpdateHandler implements EnergyPortalOrganisationGroupConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamUpdateHandler.class);

  private final TeamQueryService teamQueryService;
  private final TeamManagementService teamManagementService;

  TeamUpdateHandler(TeamQueryService teamQueryService, TeamManagementService teamManagementService) {
    this.teamQueryService = teamQueryService;
    this.teamManagementService = teamManagementService;
  }

  @Override
  public void accept(EnergyPortalOrganisationGroupEvent energyPortalOrganisationGroupEvent) {
    if (energyPortalOrganisationGroupEvent.isCreated()) {
      LOGGER.info("Received organisation group created event for group {}", energyPortalOrganisationGroupEvent.groupId());
      return;
    }

    var teamOptional = teamQueryService.getScopedTeam(
        TeamType.INDUSTRY,
        TeamScopeReference.from(Long.toString(energyPortalOrganisationGroupEvent.groupId()), "ORGGRP")
    );

    if (teamOptional.isEmpty() || teamOptional.get().getName().equals(energyPortalOrganisationGroupEvent.name())) {
      return;
    }

    teamManagementService.updateTeamName(teamOptional.get(), energyPortalOrganisationGroupEvent.name());
    LOGGER.info("Updated team name for group {}", energyPortalOrganisationGroupEvent.groupId());
  }
}
