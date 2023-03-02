package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.trueCondition;

import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
class SubmissionWorkAreaQueryService {
  private final DSLContext context;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;
  private final IndustryTeamService industryTeamService;
  private final LicenceQueryService licenceQueryService;
  private final JooqSchemaProviderService jooqSchemaProviderService;

  @Autowired
  SubmissionWorkAreaQueryService(DSLContext context,
                                 UserDetailService userDetailService,
                                 RegulatorTeamService regulatorTeamService,
                                 IndustryTeamService industryTeamService,
                                 LicenceQueryService licenceQueryService,
                                 JooqSchemaProviderService jooqSchemaProviderService) {
    this.context = context;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
    this.industryTeamService = industryTeamService;
    this.licenceQueryService = licenceQueryService;
    this.jooqSchemaProviderService = jooqSchemaProviderService;
  }

  List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems() {
    var user = userDetailService.getUserDetail();
    return context
        .select(
            field("s.id"),
            field("s.submission_type"),
            field("s.status"),
            field("rd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("rd.organisation_id"),
            DSL.arrayAgg(field("gdl.licence_id")).filterWhere(field("gdl.licence_id").isNotNull()),
            field("s.created_timestamp"),
            field("s.submitted_timestamp"))
        .from(remiTable("submission_details").as("s")
        .leftJoin(remiTable("resource_details").as("rd"))
            .on(field("rd.submission_detail_id").eq(field("s.id")))
        .leftJoin(remiTable("general_details").as("gd"))
            .on(field("gd.submission_detail_id").eq(field("s.id")))
        .leftJoin(remiTable("trap_properties").as("tp"))
            .on(field("tp.submission_detail_id").eq(field("s.id")))
        .leftJoin(remiTable("general_details_licences").as("gdl"))
            .on(field("gdl.general_details_id").eq(field("gd.id")))
        .leftJoin(remiTable("responsible_teams").as("rt"))
            .on(field("rt.resource_id").eq(field("s.resource_id")))
        .where(getConditions(user)))
        .groupBy(
            field("s.id"),
            field("rd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("rd.organisation_id"))
        .having(getExtantLicenceConditionIfIndustry(user))
        .orderBy(field("s.submitted_timestamp").desc().nullsLast(),
            field("s.created_timestamp").desc())
        .fetchInto(SubmissionWorkAreaQueryResult.class);
  }

  private List<Condition> getConditions(ServiceUserDetail user) {
    var submissionTeamMembershipCondition = getSubmissionsForResponsibleTeamOrOrganisation(user);
    var workAreaVisibleSubmissionStatuses = getVisibleSubmissions();

    return List.of(submissionTeamMembershipCondition, workAreaVisibleSubmissionStatuses);
  }

  private Condition getExtantLicenceConditionIfIndustry(ServiceUserDetail user) {
    if (regulatorTeamService.isRegulatorUser(user)) {
      return trueCondition();
    }
    // ensures submitted submissions are only visible for operators when they are on an extant licence,
    // or if the latest submission on the resource is on an extant licence.
    var userOrgUnits = industryTeamService.getOrganisationUnitIdsForUser(user);
    var userLicences = getLicenceIdsAssociatedWithUser(userOrgUnits);
    var draftStatusCondition = field("s.status").eq(SubmissionDetailStatus.DRAFT.name());

    if (userLicences.isEmpty()) {
      // if user has no licences, checks if the submission is an initial draft submission
      return draftStatusCondition.and(getIsInitialSubmissionCondition());
    }

    //gets the up-to-date list of extant licences associated with the users
    var userExtantLicences = NumberUtil.toListOfIntegers(
        licenceQueryService.getExtantLicencesByIds(userLicences)
            .stream()
            .map(LicenceView::getId)
            .toList());

    var initialDraftSubmissionCondition = draftStatusCondition.and(getIsInitialSubmissionCondition());

    return initialDraftSubmissionCondition
        .or(getIsInitialSubmissionWithExtantLicenceCondition(userExtantLicences))
        .or(getExistsExtantLicenceOnLatestApprovedSubmissionCondition(userExtantLicences));

  }

  private Condition getSubmissionsForResponsibleTeamOrOrganisation(ServiceUserDetail user) {
    if (regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION)) {
      //checks if responsible team is exploration on current submission if its the initial submission on resource
      //else checks the latest approved submission
      return field("rt.responsible_team")
          .eq(ResponsibleTeamType.EXPLORATION.name())
          .and(field("s.submitter_type").eq(SubmitterType.NSTA.name())
              .or(field("s.status").eq(SubmissionDetailStatus.SUBMITTED.name())));
    } else {
      //checks if responsible organisation is in user's organisation units
      //on current submission if its the initial submission on resource otherwise checks the latest approved submission
      var userOrgUnits = industryTeamService.getOrganisationUnitIdsForUser(user);
      return isIndustrySubmissionCondition()
          .and(isInitialIndustrySubmissionInUserResponsibleTeamCondition(userOrgUnits)
              .or(isLatestApprovedSubmissionInUserResponsibleTeamCondition(userOrgUnits)));
    }
  }

  private Condition isLatestApprovedSubmissionInUserResponsibleTeamCondition(List<Integer> userOrgUnits) {
    return exists(
        select(field("rd2.organisation_id"))
            .from(remiTable("submission_details").as("sd2"))
            .join(remiTable("resource_details").as("rd2"))
            .on(field("sd2.id").eq(field("rd2.submission_detail_id")))
            .where(field("sd2.resource_id").eq(field("s.resource_id"))
                .and(field("rd2.organisation_id").in(userOrgUnits)))
            .and(field("sd2.status").eq(SubmissionDetailStatus.APPROVED.name()))
            .orderBy(field("sd2.approved_timestamp").desc())
            .limit(1));
  }

  private Condition getVisibleSubmissions() {
    return field("s.status")
        .in(
            SubmissionDetailStatus.DRAFT.name(),
            SubmissionDetailStatus.SUBMITTED.name()
        );
  }

  private Condition getIsInitialSubmissionWithExtantLicenceCondition(List<Integer> userExtantLicences) {
    return getIsInitialSubmissionCondition()
        .and(DSL.arrayAggDistinct(field("gdl.licence_id"))
            .filterWhere(field("gdl.licence_id").in(userExtantLicences))
            .isNotNull());
  }

  private Condition getExistsExtantLicenceOnLatestApprovedSubmissionCondition(List<Integer> userExtantLicences) {
    // checks if there is an extant licence on the latest approved submission on the resource.
    return exists(
        select(DSL.arrayAgg(field("gdl.licence_id"))
            .filterWhere(field("gdl.licence_id").in(userExtantLicences)))
            .from(remiTable("submission_details").as("sd2"))
            .where(field("sd2.resource_id").eq(field("s.resource_id")))
            .and(field("sd2.status").eq(SubmissionDetailStatus.APPROVED.name()))
            .orderBy(field("sd2.approved_timestamp").desc())
            .limit(1));
  }

  private List<String> getLicenceIdsAssociatedWithUser(List<Integer> userOrgUnits) {
    //gets all the licences associated with the user's industry teams.
    return context
        .selectDistinct(field("gdl.licence_id"))
        .from(remiTable("general_details_licences").as("gdl"))
        .join(remiTable("general_details").as("gd"))
        .on(field("gd.id").eq(field("gdl.general_details_id")))
        .join(remiTable("resource_details").as("rd"))
        .on(field("rd.submission_detail_id").eq(field("gd.submission_detail_id")))
        .where(field("rd.organisation_id").in(userOrgUnits))
        .fetchInto(String.class);
  }

  private Condition isInitialIndustrySubmissionInUserResponsibleTeamCondition(List<Integer> userOrgUnits) {
    //checks if its a first submission on a resource, if it is, checks the responsible organisation on the submission
    return getIsInitialSubmissionCondition()
            .and(field("rd.organisation_id").in(userOrgUnits));
  }

  private Condition getIsInitialSubmissionCondition() {
    return field("s.submission_type")
        .in(SubmissionType.CREATE_PROSPECT.name(),
            SubmissionType.CREATE_DISCOVERY.name());
  }

  private Condition isIndustrySubmissionCondition() {
    return field("s.submitter_type").eq(SubmitterType.INDUSTRY.name());
  }

  private Table<Record> remiTable(String tableName) {
    return jooqSchemaProviderService.remiTable(tableName);
  }
}
