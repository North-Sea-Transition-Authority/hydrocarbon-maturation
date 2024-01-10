package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.selectOne;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.List;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Fields;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.ResourceDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Resources;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Service
class OperatorSubmissionWorkAreaQueryService implements SubmissionWorkAreaQueryStrategy {

  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");
  private static final ResourceDetails RD_2 = RESOURCE_DETAILS.as("rd2");
  private static final Resources RESOURCES_2 = RESOURCES.as("r2");
  private static final Fields FIELDS_2 = FIELDS.as("f2");

  private final IndustryTeamService industryTeamService;
  private final SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService;

  @Autowired
  OperatorSubmissionWorkAreaQueryService(IndustryTeamService industryTeamService,
                                         SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService) {
    this.industryTeamService = industryTeamService;
    this.submissionWorkAreaQueryCommonService = submissionWorkAreaQueryCommonService;
  }

  @Override
  public List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems(User user) {
    return submissionWorkAreaQueryCommonService.getWorkAreaQueryResults(getOperatorConditions(user));
  }

  private List<Condition> getOperatorConditions(User user) {
    var submissionTeamMembershipCondition = getSubmissionForResponsibleOrganisation(user);
    var workAreaVisibleSubmissionStatuses = submissionWorkAreaQueryCommonService.getVisibleSubmissions();

    return List.of(submissionTeamMembershipCondition, workAreaVisibleSubmissionStatuses);
  }

  private Condition getSubmissionForResponsibleOrganisation(User user) {
    var userOrgUnits = industryTeamService.getOrganisationUnitIdsForUser(user);
    return isIndustrySubmissionCondition()
        .and(isLatestApprovedSubmissionInUserResponsibleTeamCondition(userOrgUnits)
            .or(isInitialIndustrySubmissionInUserResponsibleTeamCondition(userOrgUnits)));
  }

  private Condition isInitialIndustrySubmissionInUserResponsibleTeamCondition(List<Integer> userOrgUnits) {
    //checks if its a first submission on a resource, if it is, checks the responsible organisation on the submission
    return getIsInitialSubmissionCondition()
        .and(
            (RESOURCE_DETAILS.ORGANISATION_ID.in(userOrgUnits))
                .or(FIELDS.RESPONSIBLE_ORGANISATION_ID.in(userOrgUnits))
        );
  }

  private Condition getIsInitialSubmissionCondition() {
    return SUBMISSION_DETAILS.SUBMISSION_TYPE
        .in(SubmissionType.CREATE_PROSPECT.name(),
            SubmissionType.CREATE_DISCOVERY.name(),
            SubmissionType.CREATE_CSR.name(),
            SubmissionType.CREATE_FDP.name(),
            SubmissionType.CREATE_FDPA.name(),
            SubmissionType.CREATE_COP.name()
        );
  }

  private Condition isLatestApprovedSubmissionInUserResponsibleTeamCondition(List<Integer> userOrgUnits) {
    return exists(
        selectOne()
            .from(SD_2)
            .leftJoin(RD_2).on(SD_2.ID.eq(RD_2.SUBMISSION_DETAIL_ID))
            .join(SUBMISSIONS_2).on(SUBMISSIONS_2.ID.eq(SD_2.SUBMISSION_ID))
            .join(RESOURCES_2).on(RESOURCES_2.ID.eq(SUBMISSIONS_2.RESOURCE_ID))
            .leftJoin(FIELDS_2).on(FIELDS_2.RESOURCE_ID.eq(RESOURCES_2.ID))
            .where(RESOURCES_2.ID.eq(SUBMISSIONS_2.RESOURCE_ID)
                .and(
                    (RD_2.ORGANISATION_ID.in(userOrgUnits))
                    .or(FIELDS_2.RESPONSIBLE_ORGANISATION_ID.in(userOrgUnits))
                )
            )
            .and(SD_2.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name()))
            .orderBy(SD_2.ACCEPTED_TIMESTAMP.desc())
            .limit(1));
  }

  private Condition isIndustrySubmissionCondition() {
    return SUBMISSION_DETAILS.SUBMITTER_TYPE.eq(SubmitterType.INDUSTRY.name());
  }
}
