package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class SubmissionSummaryFormValidator implements SubmissionValidator {

  private final InternalTeamService internalTeamService;
  private final SubmissionReadyService submissionReadyService;

  @Autowired
  SubmissionSummaryFormValidator(InternalTeamService internalTeamService,
                                 SubmissionReadyService submissionReadyService) {
    this.internalTeamService = internalTeamService;
    this.submissionReadyService = submissionReadyService;
  }

  @Override
  public boolean isValid(SubmissionForm submissionForm,
                         Errors errors,
                         SubmissionDetail submissionDetail,
                         ValidationType validationType) {
    var isRegulatorOrAreaTeamUser = internalTeamService.isRegulatorOrAreaTeamUser();
    var isCompleted = submissionReadyService.isSubmissionSubmittable(submissionDetail);

    if (!isCompleted && isRegulatorOrAreaTeamUser) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "completeWithoutMandatoryInformation",
          "completeWithoutMandatoryInformation.required",
          "Confirm you want to complete this submission without providing all required information");
    }

    return !errors.hasErrors();
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    // not used here
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    // not used here
  }

}
