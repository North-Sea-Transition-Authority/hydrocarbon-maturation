package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceForm;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.newresource.NewResourceFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
class StartProspectService {

  private final SubmissionCreateService submissionCreateService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;
  private final NewResourceFormValidator newResourceFormValidator;

  @Autowired
  StartProspectService(SubmissionCreateService submissionCreateService, UserDetailService userDetailService,
                       RegulatorTeamService regulatorTeamService, NewResourceFormValidator newResourceFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
    this.newResourceFormValidator = newResourceFormValidator;
  }

  SubmissionDetail createInitialProspect(NewResourceForm form) {
    return submissionCreateService.createInitialProspect(form);
  }

  BindingResult validate(NewResourceForm form, BindingResult bindingResult) {
    newResourceFormValidator.validate(form, bindingResult,
        new NewResourceFormValidatorHints(
            regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail()),
            ResourceType.PROSPECT)
    );
    return bindingResult;
  }
}
