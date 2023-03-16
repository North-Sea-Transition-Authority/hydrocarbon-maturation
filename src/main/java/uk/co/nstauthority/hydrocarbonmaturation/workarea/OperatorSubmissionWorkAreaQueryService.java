package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.List;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.ResourceDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Service
class OperatorSubmissionWorkAreaQueryService implements SubmissionWorkAreaQueryStrategy {

  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final ResourceDetails RD_2 = RESOURCE_DETAILS.as("rd2");
  private final UserDetailService userDetailService;
  private final IndustryTeamService industryTeamService;
  private final SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService;

  @Autowired
  OperatorSubmissionWorkAreaQueryService(UserDetailService userDetailService,
                                         IndustryTeamService industryTeamService,
                                         SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService) {
    this.userDetailService = userDetailService;
    this.industryTeamService = industryTeamService;
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
        .and(RESOURCE_DETAILS.ORGANISATION_ID.in(userOrgUnits));
  }

  private Condition getIsInitialSubmissionCondition() {
    return SUBMISSION_DETAILS.SUBMISSION_TYPE
        .in(SubmissionType.CREATE_PROSPECT.name(),
            SubmissionType.CREATE_DISCOVERY.name());
  }

  private Condition isLatestApprovedSubmissionInUserResponsibleTeamCondition(List<Integer> userOrgUnits) {
    return exists(
        select(RD_2.ORGANISATION_ID)
            .from(SD_2)
            .join(RD_2)
            .on(SD_2.ID.eq(RD_2.SUBMISSION_DETAIL_ID))
            .where(SD_2.RESOURCE_ID.eq(SUBMISSION_DETAILS.RESOURCE_ID)
                .and(RD_2.ORGANISATION_ID.in(userOrgUnits)))
            .and(SD_2.STATUS.eq(SubmissionDetailStatus.APPROVED.name()))
            .orderBy(SD_2.APPROVED_TIMESTAMP.desc())
            .limit(1));
  }

  private Condition isIndustrySubmissionCondition() {
    return SUBMISSION_DETAILS.SUBMITTER_TYPE.eq(SubmitterType.INDUSTRY.name());
  }
}
