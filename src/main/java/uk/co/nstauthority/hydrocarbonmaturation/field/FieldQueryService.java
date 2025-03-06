package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.partitionBy;
import static org.jooq.impl.DSL.rowNumber;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELD_STATUS_HISTORY;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorAndLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@Service
public class FieldQueryService {

  private static final SubmissionDetails SUBMISSION_DETAILS_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");

  private final ResponsibleTeamService responsibleTeamService;
  private final IndustryTeamService industryTeamService;
  private final DSLContext context;
  private final InternalTeamService internalTeamService;
  private final FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService;

  @Autowired
  public FieldQueryService(
      ResponsibleTeamService responsibleTeamService,
      IndustryTeamService industryTeamService,
      DSLContext context,
      InternalTeamService internalTeamService,
      FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService
  ) {
    this.responsibleTeamService = responsibleTeamService;
    this.industryTeamService = industryTeamService;
    this.context = context;
    this.internalTeamService = internalTeamService;
    this.fieldOperatorAndLicenceQueryService = fieldOperatorAndLicenceQueryService;
  }

  public List<FieldView> getFieldByNameForSubmissionType(String name, SubmissionType submissionType, User user) {
    HashSet<String> allowedStatuses = new HashSet<>();

    switch (submissionType) {
      case CREATE_CSR -> allowedStatuses.add(FieldStatus.STATUS_200.name());
      case CREATE_FDP -> allowedStatuses.addAll(Set.of(
          FieldStatus.STATUS_200.name(),
          FieldStatus.STATUS_300.name(),
          FieldStatus.STATUS_400.name()));
      case CREATE_FDPA -> allowedStatuses.addAll(Set.of(
          FieldStatus.STATUS_600.name(),
          FieldStatus.STATUS_700.name(),
          FieldStatus.STATUS_799.name(),
          FieldStatus.STATUS_800.name(),
          FieldStatus.STATUS_899.name()));
      case CREATE_COP -> allowedStatuses.addAll(Set.of(
          FieldStatus.STATUS_700.name(),
          FieldStatus.STATUS_799.name(),
          FieldStatus.STATUS_800.name(),
          FieldStatus.STATUS_899.name()));
      default -> throw new ActionNotAllowedException("No field statuses for submission type: %s".formatted(submissionType));
    }

    return doFieldSearch(name, allowedStatuses, user);
  }

  public List<FieldView> getShelvedOrCopFieldByName(String name, User user) {
    return doFieldSearch(name, Set.of(FieldStatus.STATUS_000.name(), FieldStatus.STATUS_900.name()), user);
  }

  /** Returns List of all field views that match the search term based on the following rules
   * 1. The field is not archived
   * 2. There are no open (not withdrawn) submissions of draft or submitted status against the field
   * 3. The latest field status is in the set of allowed statuses
   * 4. If the user is an operator user, only show fields from organisation units the user has permissions for
   * 5. If the user is not an operator user, only show fields where the user is in the responsible team
   *
   * @param name The text to search by
   * @param allowedStatuses The set of statuses used to filter by the latest status in the field status history
   * @param user Used to check if the current user has the correct permissions to view each Field
   * @return list of field views returned by the search
   */
  private List<FieldView> doFieldSearch(String name, Set<String> allowedStatuses, User user) {
    var conditionList = new ArrayList<Condition>();
    var isRegulatorOrAreaTeam = internalTeamService.isRegulatorOrAreaTeamUser(user);

    if (isRegulatorOrAreaTeam) {
      var responsibleTeamTypesForUser = responsibleTeamService.getResponsibleTeamTypesForUser(user).stream()
          .map(ResponsibleTeamType::name)
          .toList();

      conditionList.add(REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM.in(responsibleTeamTypesForUser));
    }

    var fieldViews = context.with("field_rec").as(select(
        FIELDS.FIELD_IDENTIFIER,
        FIELDS.CORRECTED_LEGAL_FIELD_NAME,
        FIELD_STATUS_HISTORY.FIELD_STATUS,
                rowNumber().over(
                    partitionBy(FIELD_STATUS_HISTORY.FIELD_IDENTIFIER)
                        .orderBy(FIELD_STATUS_HISTORY.STATUS_DATE.desc(), FIELD_STATUS_HISTORY.FIELD_STATUS.desc()))
                    .as("status_order")
        )
        .from(FIELDS
            .join(RESOURCES).on(RESOURCES.ID.eq(FIELDS.RESOURCE_ID))
            .join(REGULATOR_MANAGED_INFORMATION).on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(RESOURCES.ID))
            .join(FIELD_STATUS_HISTORY).on(FIELD_STATUS_HISTORY.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
            .leftJoin(SUBMISSIONS).on(SUBMISSIONS.RESOURCE_ID.eq(RESOURCES.ID))
            .leftJoin(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.SUBMISSION_ID.eq(SUBMISSIONS.ID))
        )
        .where(conditionList)
            .and(FIELDS.CORRECTED_LEGAL_FIELD_NAME.containsIgnoreCase(name))
            .and(RESOURCES.ARCHIVED.isFalse())
            .and(notExists(select(one())
                .from(SUBMISSIONS_2
                    .join(SUBMISSION_DETAILS_2).on(SUBMISSION_DETAILS_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID))
                    .where(SUBMISSIONS_2.RESOURCE_ID.eq(RESOURCES.ID)
                        .and(SUBMISSIONS_2.WITHDRAWN.isNull())
                        .and((SUBMISSION_DETAILS_2.STATUS.in(
                            SubmissionDetailStatus.DRAFT.name(),
                            SubmissionDetailStatus.SUBMITTED.name())
                        ))))
                )
            )
        )
        .select(
            field("field_identifier"),
            field("corrected_legal_field_name")
        )
        .from(table(name("field_rec")))
        .where(field(name("status_order"), Integer.class).eq(one()))
        .and(field(name("field_status"), String.class).in(allowedStatuses))
        .orderBy(field("corrected_legal_field_name"))
        .fetchInto(FieldView.class);

    if (!isRegulatorOrAreaTeam) {
      return filterFieldViewsForIndustryUser(fieldViews, user);
    }

    return fieldViews;
  }

  private List<FieldView> filterFieldViewsForIndustryUser(List<FieldView> fieldViews, User user) {
    var userOrgUnits = industryTeamService.getOrganisationUnitIdsForUserWithRoles(user, Set.of(Role.INDUSTRY_SUBMIT));

    var fieldIds = fieldViews.stream()
        .map(FieldView::fieldId)
        .toList();

    var fieldIdsWhereUserHasPermissions = fieldOperatorAndLicenceQueryService.getFieldOperatorIdMap(fieldIds).entrySet().stream()
        .filter(entry -> userOrgUnits.contains(entry.getValue().getOrganisationUnitId()))
        .map(Map.Entry::getKey)
        .toList();

    return fieldViews.stream()
          .filter(fieldView -> fieldIdsWhereUserHasPermissions.contains(fieldView.fieldId()))
          .toList();
  }
}