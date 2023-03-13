package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;

import java.util.List;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Service
class OperatorSubmissionWorkAreaQueryService implements SubmissionWorkAreaQueryStrategy {

  private final UserDetailService userDetailService;
  private final IndustryTeamService industryTeamService;
  private final JooqSchemaProviderService jooqSchemaProviderService;
  private final SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService;

  @Autowired
  OperatorSubmissionWorkAreaQueryService(UserDetailService userDetailService,
                                         IndustryTeamService industryTeamService,
                                         JooqSchemaProviderService jooqSchemaProviderService,
                                         SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService) {
    this.userDetailService = userDetailService;
    this.industryTeamService = industryTeamService;
    this.jooqSchemaProviderService = jooqSchemaProviderService;
    this.submissionWorkAreaQueryCommonService = submissionWorkAreaQueryCommonService;
  }

  @Override
  public List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems() {
    var user = userDetailService.getUserDetail();
    return submissionWorkAreaQueryCommonService.getWorkAreaQueryResults(getOperatorConditions(user));
  }

  private List<Condition> getOperatorConditions(ServiceUserDetail userDetail) {
    var submissionTeamMembershipCondition = getSubmissionForResponsibleOrganisation(userDetail);
    var workAreaVisibleSubmissionStatuses = submissionWorkAreaQueryCommonService.getVisibleSubmissions();

    return List.of(submissionTeamMembershipCondition, workAreaVisibleSubmissionStatuses);
  }

  private Condition getSubmissionForResponsibleOrganisation(ServiceUserDetail userDetail) {
    var userOrgUnits = industryTeamService.getOrganisationUnitIdsForUser(userDetail);
    return isIndustrySubmissionCondition()
        .and(isLatestApprovedSubmissionInUserResponsibleTeamCondition(userOrgUnits)
            .or(isInitialIndustrySubmissionInUserResponsibleTeamCondition(userOrgUnits)));
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

  private Condition isIndustrySubmissionCondition() {
    return field("s.submitter_type").eq(SubmitterType.INDUSTRY.name());
  }

  private Table<Record> remiTable(String tableName) {
    return jooqSchemaProviderService.remiTable(tableName);
  }
}
