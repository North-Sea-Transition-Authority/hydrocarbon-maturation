package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.LATEST_SUBMISSION_DETAIL_REPORTING_VIEW;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_LINKS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.TRAP_PROPERTIES;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryAssociationService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Resources;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@Service
class SubmissionQueryService {

  private static final SubmissionDetails SUBMISSION_DETAILS_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");
  private static final Resources RESOURCES_2 = RESOURCES.as("r2");
  private static final SubmissionDetails SUBMISSION_DETAILS_3 = SUBMISSION_DETAILS.as("sd3");
  private static final Submissions SUBMISSIONS_3 = SUBMISSIONS.as("s3");
  private static final Resources RESOURCES_3 = RESOURCES.as("r3");

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionFilterService submissionFilterService;
  private final SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory;
  private final ResourceGroupService resourceGroupService;
  private final DiscoveryAssociationService discoveryAssociationService;
  private final DSLContext context;
  private final InternalTeamService internalTeamService;
  private final IndustryTeamService industryTeamService;

  @Autowired
  SubmissionQueryService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                         SubmissionDetailService submissionDetailService,
                         SubmissionResourceService submissionResourceService,
                         SubmissionFilterService submissionFilterService,
                         SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory,
                         ResourceGroupService resourceGroupService,
                         DiscoveryAssociationService discoveryAssociationService,
                         DSLContext context,
                         InternalTeamService internalTeamService,
                         IndustryTeamService industryTeamService
  ) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionFilterService = submissionFilterService;
    this.submissionSearchSelectorServiceFactory = submissionSearchSelectorServiceFactory;
    this.resourceGroupService = resourceGroupService;
    this.discoveryAssociationService = discoveryAssociationService;
    this.context = context;
    this.internalTeamService = internalTeamService;
    this.industryTeamService = industryTeamService;
  }

  public List<ResourceView> getLatestProspectsByNameForSubmissionType(String name, SubmissionType submissionType, User user) {
    var conditionList = new ArrayList<Condition>();
    if (!internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      var orgUnitsWhereUserSubmitter =
          industryTeamService.getOrganisationUnitIdsForUserWithRoles(user, Set.of(Role.INDUSTRY_SUBMIT));
      conditionList.add(RESOURCE_DETAILS.ORGANISATION_ID.in(orgUnitsWhereUserSubmitter));
    }

    var submissionTypesToFilterBy = new ArrayList<String>();
    submissionTypesToFilterBy.add(SubmissionType.RELINQUISHMENT_PROSPECT.name());

    if (submissionType != SubmissionType.RELINQUISHMENT_PROSPECT) {
      submissionTypesToFilterBy.add(SubmissionType.POST_DRILL_DRY_HOLE.name());
    }

    var submissionQueryResults = context.select(
        RESOURCE_DETAILS.ID,
        RESOURCE_DETAILS.RESOURCE_NAME,
        TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK
        )
        .from(RESOURCE_DETAILS
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID))
            .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID))
            .join(SUBMISSIONS).on(SUBMISSIONS.ID.eq(SUBMISSION_DETAILS.SUBMISSION_ID))
            .join(RESOURCES).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID)
                .and(RESOURCES.ARCHIVED.isFalse())
            )
        )
        .where(conditionList)
        .and(RESOURCE_DETAILS.RESOURCE_NAME.containsIgnoreCase(name))
        .and(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.in(select(
                LATEST_SUBMISSION_DETAIL_REPORTING_VIEW.LATEST_SUBMISSION_DETAIL_ID)
                      .from(LATEST_SUBMISSION_DETAIL_REPORTING_VIEW)
                .where(LATEST_SUBMISSION_DETAIL_REPORTING_VIEW.TYPE.eq(ResourceType.PROSPECT.name()))
            )
        )
        .and(notExists(select(one())
            .from(RESOURCES_2
                .join(SUBMISSIONS_2).on(SUBMISSIONS_2.RESOURCE_ID.eq(SUBMISSIONS.RESOURCE_ID)
                        .and(SUBMISSIONS_2.WITHDRAWN.isNull())
                    )
                .join(SUBMISSION_DETAILS_2).on(SUBMISSION_DETAILS_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID))
                .where((SUBMISSION_DETAILS_2.ID.notEqual(SUBMISSION_DETAILS.ID))
                    .and((SUBMISSION_DETAILS_2.STATUS.in(
                        SubmissionDetailStatus.DRAFT.name(),
                        SubmissionDetailStatus.SUBMITTED.name())
                    )
                    .or(SUBMISSION_DETAILS_2.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name())
                        .and(SUBMISSION_DETAILS_2.SUBMISSION_TYPE.in(submissionTypesToFilterBy))))))
            )
        )
        .and(notExists(select(one())
                .from(RESOURCE_LINKS
                    .join(SUBMISSION_DETAILS_3).on(SUBMISSION_DETAILS_3.ID.eq(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID))
                    .join(SUBMISSIONS_3).on(SUBMISSIONS_3.ID.eq(SUBMISSION_DETAILS_3.SUBMISSION_ID))
                    .join(RESOURCES_3).on(RESOURCES_3.ID.eq(SUBMISSIONS_3.RESOURCE_ID)))
                .where(RESOURCE_LINKS.PROSPECT_RESOURCE_ID.eq(RESOURCES_3.ID))
            )
        )
        .fetchInto(SubmissionQueryResult.class);

    return submissionQueryResults.stream()
        .map(this::getResourceViewFromSubmissionQueryResult)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  private ResourceView getResourceViewFromSubmissionQueryResult(SubmissionQueryResult submissionQueryResult) {
    var quadAndBlockString = submissionQueryResult.centreOfInterestQuadrantAndBlock() != null
        ? " (%s)".formatted(submissionQueryResult.centreOfInterestQuadrantAndBlock())
        : "";

    return new ResourceView(submissionQueryResult.resourceDetailId(), submissionQueryResult.resourceName() + quadAndBlockString);
  }

  public List<ResourceView> getLatestDiscoveriesByNameForSubmissionType(String name,
                                                                        SubmissionType submissionType,
                                                                        User user) {
    //gets every discovery resource with the name specified
    var distinctDiscoveries =
        submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, name);

    //gets the latest submission for each discovery
    var latestSubmissionsWhichCanBeSubmittedUpon =
        distinctDiscoveries.stream()
            .filter(resource -> BooleanUtils.isFalse(resource.getArchived()))
            .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
            .flatMap(Optional::stream)
            .filter(detail -> submissionFilterService.filterDiscoverySubmissionByOtherSubmissions(detail, submissionType))
            .filter(submissionDetail ->
                !discoveryAssociationService.associatedWithStatus300PlusField(submissionDetail.getSubmission()));

    return submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy(user)
        .getResourceDetailsForSubmissionsWithTeam(latestSubmissionsWhichCanBeSubmittedUpon, submissionType, user)
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  public List<ResourceView> getLatestNotRelinquishedAndNotInGroupOrPreferredViewDiscoveriesByName(String name) {
    //gets every discovery resource with the name specified
    var distinctDiscoveries =
        submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, name);

    //gets the latest submission for each discovery
    //filtering out ones in a group but not the preferred view
    var latestSubmissionsWhichCanBeSubmittedUpon =
        distinctDiscoveries.stream()
            .filter(resource -> BooleanUtils.isFalse(resource.getArchived()))
            .filter(resource -> !resourceGroupService.isInGroupAndIsNotPreferredView(resource))
            .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
            .flatMap(Optional::stream)
            .filter(submissionDetail -> !submissionDetail.isDiscoveryRelinquishment());

    return latestSubmissionsWhichCanBeSubmittedUpon
        .map(resourceDetailsPersistenceService::getResourceDetailOrThrow)
        .map(this::getResourceViewFromResourceDetail)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  private ResourceView getResourceViewFromResourceDetail(ResourceDetail resourceDetail) {
    var resourceDisplayName = submissionResourceService.getResourceDisplayName(resourceDetail);
    return new ResourceView(resourceDetail.getId(), resourceDisplayName);
  }
}