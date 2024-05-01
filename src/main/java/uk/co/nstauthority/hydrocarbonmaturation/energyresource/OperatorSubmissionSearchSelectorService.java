package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
class OperatorSubmissionSearchSelectorService implements SubmissionSearchSelectorStrategy {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final UserService userService;
  private final IndustryTeamService industryTeamService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Autowired
  OperatorSubmissionSearchSelectorService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                          UserService userService,
                                          IndustryTeamService industryTeamService,
                                          GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.userService = userService;
    this.industryTeamService = industryTeamService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
  }

  @Override
  public Stream<ResourceDetail> getResourceDetailsForSubmissionsWithTeam(Stream<SubmissionDetail> submissionDetailStream,
                                                                         SubmissionType submissionType) {
    var user = userService.getUser();
    var orgUnitsWhereUserSubmitter = industryTeamService
        .getOrganisationUnitIdsForUserWithIndustryTeamRoles(user, Set.of(IndustryTeamRole.SUBMITTER));
    //Gets the list of resource views for prospects to be displayed in a search selector
    return submissionDetailStream
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        .filter(resourceDetail -> filterResourceDetailByExtantLicenceAndResponsibleOrganisation(
                orgUnitsWhereUserSubmitter, resourceDetail, submissionType));
  }

  /**
   * This filters out the resource for industry users when the user is not on the responsible organisation group team
   * as submitter or when the resource is not on an extant licence - unless its a relinquishment submission, then it does not
   * need to have an extant licence.
   */
  private boolean filterResourceDetailByExtantLicenceAndResponsibleOrganisation(
      List<Integer> orgUnitsWhereUserSubmitter,
      ResourceDetail resourceDetail,
      SubmissionType submissionType) {
    return filterByResponsibleOrganisationForCreatingSubmission(orgUnitsWhereUserSubmitter, resourceDetail)
        && (submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT
        || filterByExtantLicence(resourceDetail));
  }

  /**
   * This filters out the resource if the user is not part of the team for the responsible organisation
   * for the resource. Submissions on a resource can only be made by the responsible organisation.
   * Or by the regulator in the Exploration team.
   */
  private boolean filterByResponsibleOrganisationForCreatingSubmission(List<Integer> userSubmitterOrgUnits,
                                                                       ResourceDetail resourceDetail) {
    return resourceDetail.getOrganisationId() != null
        && userSubmitterOrgUnits.contains(resourceDetail.getOrganisationId());
  }

  private boolean filterByExtantLicence(ResourceDetail resourceDetail) {
    return generalDetailsPersistenceService
        .getGeneralDetailBySubmissionDetail(resourceDetail.getSubmissionDetail())
        .map(generalDetailsPersistenceService::isOnAnExtantLicence)
        .orElse(false);
  }
}
