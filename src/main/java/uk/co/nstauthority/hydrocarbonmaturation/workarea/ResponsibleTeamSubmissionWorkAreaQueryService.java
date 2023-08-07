package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.falseCondition;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.List;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;

@Service
class ResponsibleTeamSubmissionWorkAreaQueryService implements SubmissionWorkAreaQueryStrategy {

  private final SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService;
  private final ResponsibleTeamService responsibleTeamService;

  @Autowired
  ResponsibleTeamSubmissionWorkAreaQueryService(SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService,
                                                ResponsibleTeamService responsibleTeamService) {
    this.submissionWorkAreaQueryCommonService = submissionWorkAreaQueryCommonService;
    this.responsibleTeamService = responsibleTeamService;
  }

  @Override
  public List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems(ServiceUserDetail user) {
    return submissionWorkAreaQueryCommonService.getWorkAreaQueryResults(getConditions(user));
  }

  private List<Condition> getConditions(ServiceUserDetail user) {
    var submissionTeamMembershipCondition = getConditionForResponsibleTeam(user);
    var workAreaVisibleSubmissionStatuses = submissionWorkAreaQueryCommonService.getVisibleSubmissions();

    return List.of(submissionTeamMembershipCondition, workAreaVisibleSubmissionStatuses);
  }

  private Condition getConditionForResponsibleTeam(ServiceUserDetail user) {
    var responsibleTeamTypeList = responsibleTeamService.getResponsibleTeamTypesForUser(user)
        .stream().map(Enum::name).toList();

    if (!responsibleTeamTypeList.isEmpty()) {
      return REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM
          .in(responsibleTeamTypeList)
          .and(SUBMISSION_DETAILS.SUBMITTER_TYPE.eq(SubmitterType.REGULATOR.name())
              .or(SUBMISSION_DETAILS.STATUS.eq(SubmissionDetailStatus.SUBMITTED.name())));
    }

    return falseCondition();
  }
}
