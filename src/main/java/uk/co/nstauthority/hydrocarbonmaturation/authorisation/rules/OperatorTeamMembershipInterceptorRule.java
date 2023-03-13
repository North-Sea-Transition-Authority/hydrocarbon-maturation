package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.TeamMembershipInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Component
public class OperatorTeamMembershipInterceptorRule implements TeamMembershipInterceptorStrategy {
  private static final Logger LOGGER = LoggerFactory.getLogger(OperatorTeamMembershipInterceptorRule.class);

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final IndustryTeamService industryTeamService;

  @Autowired
  OperatorTeamMembershipInterceptorRule(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                        IndustryTeamService industryTeamService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.industryTeamService = industryTeamService;
  }

  @Override
  public SecurityRuleResult hasRequiredTeamMembershipPermissions(ServiceUserDetail userDetails,
                                                                 SubmissionDetail submissionDetail) {
    var responsibleOrganisationId = getResponsibleOrganisationId(submissionDetail);
    var userSubmitterOrgUnits = industryTeamService
        .getOrganisationUnitIdsForUserWithIndustryTeamRoles(userDetails, Set.of(IndustryTeamRole.SUBMITTER));
    if (isIndustrySubmissionOrSubmittedAndUserHasSubmitterRole(
        submissionDetail, userSubmitterOrgUnits, responsibleOrganisationId)) {
      return SecurityRuleResult.continueAsNormal();
    }

    var userId = userDetails.wuaId();
    var submissionDetailId = submissionDetail.getId();

    LOGGER.warn("User {} attempted to access submission {} with responsible organisation id {}. " +
            " User expected role within responsible organisation submitter.",
        userId,
        submissionDetailId,
        responsibleOrganisationId);

    return SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN);
  }

  private Integer getResponsibleOrganisationId(SubmissionDetail submissionDetail) {
    return resourceDetailsPersistenceService
        .getResourceDetailBySubmissionDetail(submissionDetail)
        .map(ResourceDetail::getOrganisationId)
        .orElse(null);
  }

  private boolean isIndustrySubmissionOrSubmittedAndUserHasSubmitterRole(SubmissionDetail submissionDetail,
                                                                         List<Integer> userSubmitterOrgUnits,
                                                                         Integer responsibleOrganisationId) {
    // Ensures industry users can only access submissions for which responsible organisation they are a submitter for.
    return submissionDetail.getSubmitterType() == SubmitterType.INDUSTRY
        && responsibleOrganisationId != null
        && userSubmitterOrgUnits.contains(responsibleOrganisationId);
  }
}
