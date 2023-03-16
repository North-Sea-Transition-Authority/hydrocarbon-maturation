package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.falseCondition;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.List;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
class RegulatorSubmissionWorkAreaQueryService implements SubmissionWorkAreaQueryStrategy {

  private final UserDetailService userDetailService;
  private final SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  RegulatorSubmissionWorkAreaQueryService(UserDetailService userDetailService,
                                          SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService,
                                          RegulatorTeamService regulatorTeamService) {
    this.userDetailService = userDetailService;
    this.submissionWorkAreaQueryCommonService = submissionWorkAreaQueryCommonService;
    this.regulatorTeamService = regulatorTeamService;
  }

  @Override
  public List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems() {
    var user = userDetailService.getUserDetail();
    return submissionWorkAreaQueryCommonService.getWorkAreaQueryResults(getRegulatorConditions(user));
  }

  private List<Condition> getRegulatorConditions(ServiceUserDetail user) {
    var submissionTeamMembershipCondition = getSubmissionsForResponsibleTeam(user);
    var workAreaVisibleSubmissionStatuses = submissionWorkAreaQueryCommonService.getVisibleSubmissions();

    return List.of(submissionTeamMembershipCondition, workAreaVisibleSubmissionStatuses);
  }

  private Condition getSubmissionsForResponsibleTeam(ServiceUserDetail user) {
    if (regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION)) {
      return REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM
          .eq(ResponsibleTeamType.EXPLORATION.name())
          .and(SUBMISSION_DETAILS.SUBMITTER_TYPE.eq(SubmitterType.NSTA.name())
              .or(SUBMISSION_DETAILS.STATUS.eq(SubmissionDetailStatus.SUBMITTED.name())));
    }
    return falseCondition();
  }
}
