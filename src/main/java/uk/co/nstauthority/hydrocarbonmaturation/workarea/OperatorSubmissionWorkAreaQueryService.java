package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.List;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;

@Service
class OperatorSubmissionWorkAreaQueryService implements SubmissionWorkAreaQueryStrategy {

  private final SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService;

  @Autowired
  OperatorSubmissionWorkAreaQueryService(SubmissionWorkAreaQueryCommonService submissionWorkAreaQueryCommonService) {
    this.submissionWorkAreaQueryCommonService = submissionWorkAreaQueryCommonService;
  }

  @Override
  public List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems(User user) {
    return submissionWorkAreaQueryCommonService.getWorkAreaQueryResults(getConditions());
  }

  private List<Condition> getConditions() {
    var workAreaVisibleSubmissionStatuses = submissionWorkAreaQueryCommonService.getVisibleSubmissions();
    var isIndustrySubmissionCondition = isIndustrySubmissionCondition();
    return List.of(workAreaVisibleSubmissionStatuses, isIndustrySubmissionCondition);
  }

  private Condition isIndustrySubmissionCondition() {
    return SUBMISSION_DETAILS.SUBMITTER_TYPE.eq(SubmitterType.INDUSTRY.name());
  }
}
