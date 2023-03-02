package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Component
public class TeamMembershipInterceptorRule implements SubmissionInterceptorSecurityRule {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamMembershipInterceptorRule.class);

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final PermissionService permissionService;
  private final ResponsibleTeamPersistenceService responsibleTeamPersistenceService;
  private final IndustryTeamService industryTeamService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  public TeamMembershipInterceptorRule(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                       PermissionService permissionService,
                                       ResponsibleTeamPersistenceService responsibleTeamPersistenceService,
                                       IndustryTeamService industryTeamService,
                                       OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.permissionService = permissionService;
    this.responsibleTeamPersistenceService = responsibleTeamPersistenceService;
    this.industryTeamService = industryTeamService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @Override
  public Class<? extends Annotation> supports() {
    return IsFromResponsibleTeam.class;
  }

  @Override
  public SecurityRuleResult check(Object annotation,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  ServiceUserDetail userDetails,
                                  SubmissionDetail submissionDetail) {
    var responsibleTeamType = responsibleTeamPersistenceService
        .getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()).getResponsibleTeamType();
    var permissionRequired = responsibleTeamType.getRolePermissions();

    // Ensures draft industry submissions are not exposed to regulators
    // Then checks the regulator permission for the responsible team type.
    if (isNotDraftIndustrySubmission(submissionDetail)
        && permissionService.hasPermission(userDetails, permissionRequired)) {
      return SecurityRuleResult.continueAsNormal();
    }

    var responsibleOrganisationId = resourceDetailsPersistenceService
        .getResourceDetailBySubmissionDetail(submissionDetail)
        .map(ResourceDetail::getOrganisationId)
        .orElse(null);
    var organisationGroups = organisationUnitQueryService.findOrganisationGroupIdsByUnitId(responsibleOrganisationId);
    if (isIndustrySubmissionOrSubmittedAndUserHasSubmitterRole(
        submissionDetail, userDetails, organisationGroups, responsibleOrganisationId)) {
      return SecurityRuleResult.continueAsNormal();
    }

    var userId = userDetails.wuaId();
    var submissionDetailId = submissionDetail.getId();

    LOGGER.warn("User {} attempted to access submission {} with responsible organisation id {}. " +
            " User expected role within responsible organisation submitter or NSTA role {}.",
        userId,
        submissionDetailId,
        responsibleOrganisationId,
        responsibleTeamType);

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }

  private boolean isNotDraftIndustrySubmission(SubmissionDetail submissionDetail) {
    return !(submissionDetail.getStatus() == SubmissionDetailStatus.DRAFT
        && submissionDetail.getSubmitterType() == SubmitterType.INDUSTRY);
  }

  private boolean isIndustrySubmissionOrSubmittedAndUserHasSubmitterRole(SubmissionDetail submissionDetail,
                                                                         ServiceUserDetail user,
                                                                         List<Integer> userOrganisationGroups,
                                                                         Integer responsibleOrganisationId) {
    // Ensures industry users can only access submissions for which responsible organisation they are a submitter for.
    return submissionDetail.getSubmitterType() == SubmitterType.INDUSTRY
        && responsibleOrganisationId != null
        && industryTeamService.isSubmitterForOrganisationGroups(user, userOrganisationGroups);
  }
}
