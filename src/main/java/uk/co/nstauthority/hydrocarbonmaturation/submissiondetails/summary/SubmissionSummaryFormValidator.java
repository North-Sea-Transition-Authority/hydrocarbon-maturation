package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Service
public class SubmissionSummaryFormValidator {

  private final InternalTeamService internalTeamService;
  private final SubmissionReadyService submissionReadyService;

  @Autowired
  SubmissionSummaryFormValidator(InternalTeamService internalTeamService,
                                 SubmissionReadyService submissionReadyService) {
    this.internalTeamService = internalTeamService;
    this.submissionReadyService = submissionReadyService;
  }

  public boolean isValid(Errors errors,
                         SubmissionDetail submissionDetail,
                         User user) {
    var isRegulatorOrAreaTeamUser = internalTeamService.isRegulatorOrAreaTeamUser(user);
    var isCompleted = submissionReadyService.isSubmissionSubmittable(submissionDetail, user);

    if (!isCompleted && isRegulatorOrAreaTeamUser) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "completeWithoutMandatoryInformation",
          "completeWithoutMandatoryInformation.required",
          "Confirm you want to complete this submission without providing all required information");
    }

    return !errors.hasErrors();
  }

}
