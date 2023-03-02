package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
class SubmissionSummaryFormService {
  private final SubmissionReadyService submissionReadyService;
  private final SubmissionSummaryFormValidator submissionSummaryFormValidator;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  SubmissionSummaryFormService(SubmissionReadyService submissionReadyService,
                               SubmissionSummaryFormValidator submissionSummaryFormValidator,
                               UserDetailService userDetailService,
                               RegulatorTeamService regulatorTeamService) {
    this.submissionReadyService = submissionReadyService;
    this.submissionSummaryFormValidator = submissionSummaryFormValidator;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  BindingResult validate(SubmissionSummaryForm form,
                         BindingResult bindingResult,
                         SubmissionDetail submissionDetail) {
    submissionSummaryFormValidator.validate(
        form,
        bindingResult,
        new SubmitValidatorHints(
            isRegulator(),
            submissionReadyService.isSubmissionSubmittable(submissionDetail))

    );
    return bindingResult;
  }

  boolean isRegulator() {
    return regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail());
  }
}
