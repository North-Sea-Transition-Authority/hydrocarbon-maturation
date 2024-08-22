package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;

@Component
public class OperatorResourceAccessInterceptorRule implements ResourceAccessInterceptorStrategy {
  private static final Logger LOGGER = LoggerFactory.getLogger(OperatorResourceAccessInterceptorRule.class);

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final FieldPersistenceService fieldPersistenceService;
  private final TeamQueryService teamQueryService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  OperatorResourceAccessInterceptorRule(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                        FieldPersistenceService fieldPersistenceService,
                                        TeamQueryService teamQueryService,
                                        OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.teamQueryService = teamQueryService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @Override
  public SecurityRuleResult hasRequiredResourceAccessPermissions(User user,
                                                                 SubmissionDetail submissionDetail) {
    var responsibleOrganisationId = getResponsibleOrganisationId(submissionDetail);
    if (submissionDetail.getSubmitterType() != SubmitterType.INDUSTRY || responsibleOrganisationId == null) {
      return getForbiddenResponse(user, submissionDetail, responsibleOrganisationId);
    }

    var portalOrganisationGroups = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(responsibleOrganisationId)
            .stream()
            .map(String::valueOf)
            .toList();
    var responsibleOrganisationGroups = teamQueryService.getOrganisationScopedTeams(portalOrganisationGroups)
            .stream()
            .map(Team::getId)
            .collect(Collectors.toSet());
    var userSubmitterTeams = teamQueryService.getTeamRolesForUser(user.wuaId())
            .stream()
            .filter(role -> Role.INDUSTRY_SUBMIT.equals(role.getRole()))
            .map(TeamRole::getTeam)
            .map(Team::getId)
            .collect(Collectors.toSet());

    if (userSubmitterTeams.stream().anyMatch(responsibleOrganisationGroups::contains)) {
      return SecurityRuleResult.continueAsNormal();
    } else {
      return getForbiddenResponse(user, submissionDetail, responsibleOrganisationId);
    }

  }

  private SecurityRuleResult getForbiddenResponse(User user,
                                                  SubmissionDetail submissionDetail,
                                                  Integer responsibleOrganisationId) {
    var userId = user.wuaId();
    var submissionDetailId = submissionDetail.getId();

    LOGGER.warn("User {} attempted to access submission {} with responsible organisation id {}. " +
                    " User expected role within responsible organisation submitter.",
            userId,
            submissionDetailId,
            responsibleOrganisationId);

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }

  private Integer getResponsibleOrganisationId(SubmissionDetail submissionDetail) {
    if (submissionDetail.isFieldSubmission()) {
      var resource = submissionDetail.getSubmission().getResource();
      return fieldPersistenceService.getFieldOrThrow(resource).getResponsibleOrganisationId();
    }

    return resourceDetailsPersistenceService
        .getResourceDetailBySubmissionDetail(submissionDetail)
        .map(ResourceDetail::getOrganisationId)
        .orElse(null);
  }
}
