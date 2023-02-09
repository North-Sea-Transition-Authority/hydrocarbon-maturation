package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Service
public class SubmissionQueryService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final ResourceLinkService resourceLinkService;
  private final UserDetailService userDetailService;
  private final IndustryTeamService industryTeamService;
  private final PermissionService permissionService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  SubmissionQueryService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                         SubmissionDetailService submissionDetailService,
                         SubmissionResourceService submissionResourceService,
                         ResourceLinkService resourceLinkService,
                         UserDetailService userDetailService,
                         IndustryTeamService industryTeamService,
                         PermissionService permissionService,
                         OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.resourceLinkService = resourceLinkService;
    this.userDetailService = userDetailService;
    this.industryTeamService = industryTeamService;
    this.permissionService = permissionService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  public List<ResourceView> getLatestDiscoveriesByNameForSubmissionType(String name,
                                                                        SubmissionType submissionType) {
    //gets every discovery resource with the name specified
    var distinctDiscoveries =
        submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, name);

    //gets the latest general detail for each discovery
    var latestSubmissionsWhichCanBeSubmittedUpon =
        distinctDiscoveries.stream()
            .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
            .flatMap(Optional::stream)
            .filter(detail -> filterDiscoverySubmissionByOtherSubmissions(detail, submissionType));

    return getResourceViewsForSubmissions(latestSubmissionsWhichCanBeSubmittedUpon, submissionType);
  }

  public List<ResourceView> getLatestProspectsByNameForSubmissionType(String name, SubmissionType submissionType) {
    //gets every prospect resource
    var distinctProspectsForSubmissionType =
        submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, name);

    //gets the latest approved submission if any for each prospect and filters out submissions from which you
    //can't start a submission
    var latestSubmissionsWhichCanBeSubmittedUpon =
        distinctProspectsForSubmissionType.stream()
            .map(submissionDetailService::getLatestApprovedProspectSubmissionForResource)
            .flatMap(Optional::stream)
            .filter(detail -> filterProspectSubmissionByOtherSubmissions(detail, submissionType));

    return getResourceViewsForSubmissions(latestSubmissionsWhichCanBeSubmittedUpon, submissionType);
  }

  private List<ResourceView> getResourceViewsForSubmissions(
      Stream<SubmissionDetail> submissionDetailStream,
      SubmissionType submissionType) {
    var user = userDetailService.getUserDetail();
    var isExplorationTeam = permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION));

    //Gets the list of resource views for prospects to be displayed in a search selector
    return submissionDetailStream
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        .filter(generalDetail -> isExplorationTeam
            || filterByResponsibleOrganisationForCreatingSubmission(user, generalDetail, submissionType))
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  public List<ResourceView> getLatestResourceSubmissionsByName(String name) {
    // Get every resource by name
    return submissionResourceService.getResourcesByName(name).stream()
        // Get the latest approved submission for each resource
        .map(submissionDetailService::getLatestApprovedSubmissionDetailForResource)
        .flatMap(Optional::stream)
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        // Create a list of resource views for prospects to be displayed in a search selector
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  /**
   * This filters out the resource if the user is not part of the team for the responsible organisation
   * for the resource. Discovery and prospect relinquishment submissions can only be made by the responsible organisation.
   * Or by the NSTA in the Exploration team.
   */
  private boolean filterByResponsibleOrganisationForCreatingSubmission(ServiceUserDetail user,
                                                                       ResourceDetail resourceDetail,
                                                                       SubmissionType submissionType) {
    if (resourceDetail.getOrganisationId() != null
        && (submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT)) {
      var responsibleOrganisationId = resourceDetail.getOrganisationId();
      var organisationGroupsIds = organisationUnitQueryService
          .findOrganisationGroupIdsByUnitId(responsibleOrganisationId);
      if (!organisationGroupsIds.isEmpty()) {
        return industryTeamService.isSubmitterForOrganisationGroups(user, organisationGroupsIds);
      }
    }

    return submissionType != SubmissionType.RELINQUISHMENT_DISCOVERY
        && submissionType != SubmissionType.RELINQUISHMENT_PROSPECT;
  }

  /**
   * Gets the discoveries linked to the prospect.
   */
  private List<Resource> getLinkedDiscoveries(Resource prospect) {
    var resourceLinks = resourceLinkService.getResourceLinksByProspect(prospect);
    var linkedDiscoveries = new ArrayList<Resource>();
    resourceLinks.forEach(resourceLink ->
        linkedDiscoveries.add(resourceLink.getDiscoveryResource())
    );

    return linkedDiscoveries;
  }

  /** Checks other submissions on the discovery to determine whether a new submission can be made.
  * @param submission Current latest approved submission being checked
  * @param submissionToBeCreatedType Submission type that is being created
  * @return if the submission should be available to be chosen
  */
  private boolean filterDiscoverySubmissionByOtherSubmissions(SubmissionDetail submission,
                                                              SubmissionType submissionToBeCreatedType) {
    var otherSubmissionsOnResource = submissionDetailService
        .getSubmissionDetailsForResource(submission.getResourceId());

    return otherSubmissionsOnResource
        .stream()
        .noneMatch(submissionToBeChecked ->
            isSubmissionPreventingNewSubmission(submissionToBeChecked, submissionToBeCreatedType)
        );
  }

  /** Checks other submissions on the prospect to determine whether a new submission can be made.
   * @param submission Current latest approved submission being checked
   * @param submissionToBeCreatedType Submission type that is being created
   * @return if the submission should be available to be chosen
   */
  private boolean filterProspectSubmissionByOtherSubmissions(SubmissionDetail submission,
                                                             SubmissionType submissionToBeCreatedType) {
    if (isLinkedDiscoveryPreventingSubmission(
        submissionToBeCreatedType, submission.getResourceId())) {
      // cannot start a prospect update or relinquishment on a prospect with a discovery submission on it
      return false;
    }

    //Gets other submissions made on the submission's resource
    var otherSubmissionsOnResource =
        submissionDetailService.getSubmissionDetailsForResource(submission.getResourceId());

    //True means the condition prevents the creation of a submission of type 'submissionToBeCreatedType',
    //false means the submission does not prevent submissionToBeCreatedType
    //and that the next submission for the resource should be checked
    return otherSubmissionsOnResource
        .stream()
        .noneMatch(submissionToBeChecked ->
          isSubmissionPreventingNewSubmission(submissionToBeChecked, submissionToBeCreatedType)
        );
  }

  /**
   *  Checks the business rules relating to other submissions on the same resource
   *  for the submission type that is being created.
   * @param submissionToBeChecked The submission to be checked whether it prevents newSubmissionType from being submitted.
   * @param newSubmissionType The type of the submission being created.
   * @return True if the old submission prevents a new submission of type newSubmissionType.
   */
  private boolean isSubmissionPreventingNewSubmission(SubmissionDetail submissionToBeChecked,
                                                      SubmissionType newSubmissionType) {
    return switch (newSubmissionType) {
      case POST_DRILL_DRY_HOLE, CREATE_DISCOVERY_FROM_PROSPECT ->
          SubmissionFilterUtil.isSubmissionPreventingDryHoleOrDiscovery(submissionToBeChecked);
      case PROSPECT_UPDATE ->
          SubmissionFilterUtil.isSubmissionPreventingProspectUpdate(submissionToBeChecked);
      case RELINQUISHMENT_PROSPECT ->
          SubmissionFilterUtil.isSubmissionPreventingProspectRelinquishment(submissionToBeChecked);
      case RELINQUISHMENT_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryRelinquishment(submissionToBeChecked);
      case UPDATE_DISCOVERY ->
          SubmissionFilterUtil.isSubmissionPreventingDiscoveryUpdate(submissionToBeChecked);
      default -> false;
    };
  }

  /**
   * Prospect relinquishment and prospect update cannot be started on a resource with a discovery submission.
   * This method returns true if the linked discovery prevents the submission of submissionTypeToBeCreated
   */
  private boolean isLinkedDiscoveryPreventingSubmission(SubmissionType submissionTypeToBeCreated,
                                                        Resource prospect) {
    //gets discovery resources linked to the prospect
    var linkedDiscoveries = getLinkedDiscoveries(prospect);
    var linkedDiscoverySubmissions = new ArrayList<SubmissionDetail>();
    ///Gets the submissions on the discovery and filters out the deleted ones
    linkedDiscoveries.forEach(discovery ->
        linkedDiscoverySubmissions.addAll(submissionDetailService.getSubmissionDetailsForResource(discovery)
            .stream()
            .filter(submissionDetail -> submissionDetail.getStatus() != SubmissionDetailStatus.DELETED)
            .toList())
    );
    //The list will be empty if there are no non-deleted discovery submissions.
    return !linkedDiscoverySubmissions.isEmpty()
        && (submissionTypeToBeCreated == SubmissionType.RELINQUISHMENT_PROSPECT
        || submissionTypeToBeCreated == SubmissionType.PROSPECT_UPDATE);
  }

  private ResourceView getResourceViewFromResourceDetail(ResourceDetail resourceDetail) {
    var resourceDisplayName = submissionResourceService.getResourceDisplayName(resourceDetail);
    return new ResourceView(resourceDetail.getId(), resourceDisplayName);
  }
}
