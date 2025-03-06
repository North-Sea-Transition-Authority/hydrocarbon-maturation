package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessInterceptorStrategy;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorAndLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@Component
public class OperatorResourceAccessInterceptorRule implements ResourceAccessInterceptorStrategy {
  private static final Logger LOGGER = LoggerFactory.getLogger(OperatorResourceAccessInterceptorRule.class);

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final FieldPersistenceService fieldPersistenceService;
  private final ResourceAccessService resourceAccessService;
  private final FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService;

  @Autowired
  OperatorResourceAccessInterceptorRule(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                        FieldPersistenceService fieldPersistenceService,
                                        ResourceAccessService resourceAccessService,
                                        FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.resourceAccessService = resourceAccessService;
    this.fieldOperatorAndLicenceQueryService = fieldOperatorAndLicenceQueryService;
  }

  @Override
  public SecurityRuleResult hasRequiredResourceAccessPermissions(User user,
                                                                 SubmissionDetail submissionDetail) {
    var responsibleOrganisationId = getResponsibleOrganisationId(submissionDetail);
    if (submissionDetail.getSubmitterType() != SubmitterType.INDUSTRY || responsibleOrganisationId == null) {
      return getForbiddenResponse(user, submissionDetail, responsibleOrganisationId);
    }

    if (resourceAccessService.userHasAccessToResourceForOrganisation(
        user,
        Set.of(Role.INDUSTRY_SUBMIT),
        responsibleOrganisationId)
    ) {
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
      var field = fieldPersistenceService.getFieldOrThrow(resource);
      return fieldOperatorAndLicenceQueryService.getFieldOperatorByFieldIdentifier(field.getFieldIdentifier())
          .map(OrganisationUnit::getOrganisationUnitId)
          .orElse(null);
    }

    return resourceDetailsPersistenceService
        .getResourceDetailBySubmissionDetail(submissionDetail)
        .map(ResourceDetail::getOrganisationId)
        .orElse(null);
  }
}
