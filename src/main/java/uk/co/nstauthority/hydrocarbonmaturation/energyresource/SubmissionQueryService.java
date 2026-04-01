package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELD_STATUS_HISTORY;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.LATEST_SUBMISSION_DETAIL_VIEW;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_LINKS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.TRAP_PROPERTIES;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@Service
class SubmissionQueryService {

  private static final SubmissionDetails SUBMISSION_DETAILS_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");

  private final DSLContext context;
  private final InternalTeamService internalTeamService;
  private final IndustryTeamService industryTeamService;

  @Autowired
  SubmissionQueryService(
      DSLContext context,
      InternalTeamService internalTeamService,
      IndustryTeamService industryTeamService
  ) {
    this.context = context;
    this.internalTeamService = internalTeamService;
    this.industryTeamService = industryTeamService;
  }

  /**
   * Returns List of all prospect resource views that match the search term based on the following rules
   * 1. The prospect is not active
   * 2. There are no open (not withdrawn) submissions of draft or submitted status against the prospect
   * 3. There is not already an accepted relinquishment submission against the prospect
   * 4. If the submission type is an update or dry hole, there is not an accepted dry hole submission against the prospect
   * 5. The prospect is not linked to a discovery
   * 6. If the user is not a regulator or area team user, the prospect is from an organisation unit the user has permissions for
   *
   * @param name The text to search by
   * @param submissionType Filter to check which submission types can be created on each prospect
   * @param user Used to check if the current user has the correct permissions to view each prospect
   * @return list of resource views returned by the search
   */
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
        TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
        GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE
        )
        .from(RESOURCE_DETAILS
            .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID))
            .join(SUBMISSIONS).on(SUBMISSIONS.ID.eq(SUBMISSION_DETAILS.SUBMISSION_ID))
            .join(RESOURCES).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID))
            .leftJoin(GENERAL_DETAILS).on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
        )
        .where(conditionList)
        .and(RESOURCES.ACTIVE.isTrue())
        .and(RESOURCE_DETAILS.RESOURCE_NAME.containsIgnoreCase(name))
        .and(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.in(select(
                LATEST_SUBMISSION_DETAIL_VIEW.LATEST_SUBMISSION_DETAIL_ID)
                      .from(LATEST_SUBMISSION_DETAIL_VIEW)
                .where(LATEST_SUBMISSION_DETAIL_VIEW.TYPE.eq(ResourceType.PROSPECT.name()))
            )
        )
        .and(notExists(select(one())
                .from(SUBMISSIONS_2
                .join(SUBMISSION_DETAILS_2).on(SUBMISSION_DETAILS_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID))
                    .where(SUBMISSIONS_2.RESOURCE_ID.eq(RESOURCES.ID)
                    .and(SUBMISSIONS_2.WITHDRAWN.isNull())
                    .and(((SUBMISSION_DETAILS_2.STATUS.in(
                        SubmissionDetailStatus.DRAFT.name(),
                        SubmissionDetailStatus.SUBMITTED.name())
                    )
                    .or(SUBMISSION_DETAILS_2.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name())
                        .and(SUBMISSION_DETAILS_2.SUBMISSION_TYPE.in(submissionTypesToFilterBy)))))))
            )
        )
        .and(notExists(select(one())
                .from(RESOURCE_LINKS)
                .where(RESOURCE_LINKS.PROSPECT_RESOURCE_ID.eq(RESOURCES.ID))
            )
        )
        .fetchInto(SubmissionQueryResult.class);

    return submissionQueryResults.stream()
        .map(this::getResourceViewFromSubmissionQueryResult)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  /**
   * Returns List of all discovery resource views that match the search term based on the following rules
   * 1. The discovery is active
   * 2. There are no open (not withdrawn) submissions of draft or submitted status against the discovery
   * 3. There are no accepted relinquishment submissions against the discovery
   * 4. The discovery is not linked to a field that has a status of 300 or higher in its history
   * 5. If the user is not a regulator or area team user, the prospect is from an organisation unit the user has permissions for
   *
   * @param name The text to search by
   * @param user Used to check if the current user has the correct permissions to view each discovery
   * @return list of resource views returned by the search
   */
  public List<ResourceView> getLatestDiscoveriesByNameForSubmissionType(String name, User user) {
    var conditionList = new ArrayList<Condition>();
    if (!internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      var orgUnitsWhereUserSubmitter =
          industryTeamService.getOrganisationUnitIdsForUserWithRoles(user, Set.of(Role.INDUSTRY_SUBMIT));
      conditionList.add(RESOURCE_DETAILS.ORGANISATION_ID.in(orgUnitsWhereUserSubmitter));
    }

    var submissionQueryResults = context.select(
        RESOURCE_DETAILS.ID,
        RESOURCE_DETAILS.RESOURCE_NAME,
        TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
        GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE
        )
        .from(RESOURCE_DETAILS
            .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID))
            .join(SUBMISSIONS).on(SUBMISSIONS.ID.eq(SUBMISSION_DETAILS.SUBMISSION_ID))
            .join(RESOURCES).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID))
            .leftJoin(GENERAL_DETAILS).on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
        )
        .where(conditionList)
        .and(RESOURCES.ACTIVE.isTrue())
        .and(RESOURCE_DETAILS.RESOURCE_NAME.containsIgnoreCase(name))
        .and(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.in(select(
                LATEST_SUBMISSION_DETAIL_VIEW.LATEST_SUBMISSION_DETAIL_ID)
                .from(LATEST_SUBMISSION_DETAIL_VIEW)
                .where(LATEST_SUBMISSION_DETAIL_VIEW.TYPE.eq(ResourceType.DISCOVERY.name()))
            )
        )
        .and(notExists(select(one())
                .from(SUBMISSIONS_2
                    .join(SUBMISSION_DETAILS_2).on(SUBMISSION_DETAILS_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID))
                    .where(SUBMISSIONS_2.RESOURCE_ID.eq(RESOURCES.ID)
                        .and(SUBMISSIONS_2.WITHDRAWN.isNull())
                        .and((SUBMISSION_DETAILS_2.STATUS.in(
                            SubmissionDetailStatus.DRAFT.name(),
                            SubmissionDetailStatus.SUBMITTED.name())
                        .or(SUBMISSION_DETAILS_2.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name())
                                .and(SUBMISSION_DETAILS_2.SUBMISSION_TYPE.eq(SubmissionType.RELINQUISHMENT_DISCOVERY.name()))))
                        )
                    )
                )
            )
        )
        .and(notExists(select(one())
                .from(RESOURCE_LINKS
                    .join(FIELDS).on(FIELDS.RESOURCE_ID.eq(RESOURCE_LINKS.FIELD_RESOURCE_ID))
                    .join(FIELD_STATUS_HISTORY).on(FIELD_STATUS_HISTORY.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
                    .and(FIELD_STATUS_HISTORY.FIELD_STATUS.in(
                        FieldStatus.STATUS_300.name(),
                        FieldStatus.STATUS_400.name(),
                        FieldStatus.STATUS_500.name(),
                        FieldStatus.STATUS_600.name(),
                        FieldStatus.STATUS_700.name(),
                        FieldStatus.STATUS_799.name(),
                        FieldStatus.STATUS_800.name(),
                        FieldStatus.STATUS_899.name(),
                        FieldStatus.STATUS_900.name()
                    )))
                .where(RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.eq(RESOURCES.ID))
            )
        )
        .fetchInto(SubmissionQueryResult.class);

    return submissionQueryResults.stream()
        .map(this::getResourceViewFromSubmissionQueryResult)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  /**
   * Returns List of all discovery resource views that match the search term based on the following rules
   * 1. The discovery is active
   * 2. There are no accepted relinquishment submissions against the discovery
   * 3. The discovery is not in a resource group
   *
   * @param name - The text to search by
   * @return list of resource views returned by the search
   */
  public List<ResourceView> getLatestActiveViewDiscoveriesByName(String name) {
    var submissionQueryResults = context.select(
        RESOURCE_DETAILS.ID,
        RESOURCE_DETAILS.RESOURCE_NAME,
        TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
        GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE
        )
        .from(RESOURCE_DETAILS
            .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID))
            .join(LATEST_SUBMISSION_DETAIL_VIEW)
            .on(LATEST_SUBMISSION_DETAIL_VIEW.LATEST_SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .join(SUBMISSIONS).on(SUBMISSIONS.ID.eq(SUBMISSION_DETAILS.SUBMISSION_ID))
            .join(RESOURCES).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID))
            .leftJoin(GENERAL_DETAILS).on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
        )
        .where(RESOURCE_DETAILS.RESOURCE_NAME.containsIgnoreCase(name))
        .and(RESOURCES.ACTIVE.isTrue())
        .and(RESOURCES.TYPE.eq(ResourceType.DISCOVERY.name()))
        .and(notExists(select(one())
            .from(SUBMISSIONS_2
                .join(SUBMISSION_DETAILS_2).on(SUBMISSION_DETAILS_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID))
                .where(SUBMISSIONS_2.RESOURCE_ID.eq(RESOURCES.ID)
                    .and(SUBMISSIONS_2.WITHDRAWN.isNull())
                    .and(SUBMISSION_DETAILS_2.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name()))
                    .and(SUBMISSION_DETAILS_2.SUBMISSION_TYPE.in(SubmissionType.RELINQUISHMENT_DISCOVERY.name()))
                )
            ))
        ).fetchInto(SubmissionQueryResult.class);

    return submissionQueryResults.stream()
        .map(this::getResourceViewFromSubmissionQueryResult)
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }

  private ResourceView getResourceViewFromSubmissionQueryResult(SubmissionQueryResult submissionQueryResult) {
    var displayName = SubmissionDetailDisplayUtil.getDisplayName(
        submissionQueryResult.resourceName(),
        submissionQueryResult.centreOfInterestQuadrantAndBlock(),
        submissionQueryResult.licenceApplicationReference()
    );

    return new ResourceView(submissionQueryResult.resourceId(), displayName);
  }
}