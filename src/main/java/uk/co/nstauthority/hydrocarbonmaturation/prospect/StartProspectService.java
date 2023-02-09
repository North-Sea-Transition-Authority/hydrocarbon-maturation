package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class StartProspectService {

  private final SubmissionCreateService submissionCreateService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;
  private final ResourceDetailsFormValidator resourceDetailsFormValidator;

  @Autowired
  StartProspectService(SubmissionCreateService submissionCreateService, UserDetailService userDetailService,
                       RegulatorTeamService regulatorTeamService, ResourceDetailsFormValidator resourceDetailsFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
    this.resourceDetailsFormValidator = resourceDetailsFormValidator;
  }

  SubmissionDetail createInitialProspect(ResourceDetailsForm form) {
    return submissionCreateService.createInitialProspect(form);
  }

  BindingResult validate(ResourceDetailsForm form, BindingResult bindingResult) {
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL,
            regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail()),
            ResourceType.PROSPECT,
            null)
    );
    return bindingResult;
  }
}
