package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class StartDiscoveryService {

  private final SubmissionCreateService submissionCreateService;
  private final StartDiscoveryFormValidator startDiscoveryFormValidator;
  private final ResourceDetailsFormValidator resourceDetailsFormValidator;
  private final UserService userService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  StartDiscoveryService(SubmissionCreateService submissionCreateService,
                        StartDiscoveryFormValidator startDiscoveryFormValidator,
                        ResourceDetailsFormValidator resourceDetailsFormValidator,
                        UserService userService,
                        RegulatorTeamService regulatorTeamService) {
    this.submissionCreateService = submissionCreateService;
    this.startDiscoveryFormValidator = startDiscoveryFormValidator;
    this.resourceDetailsFormValidator = resourceDetailsFormValidator;
    this.userService = userService;
    this.regulatorTeamService = regulatorTeamService;
  }

  SubmissionDetail createNewDiscoveryFromProspect(StartDiscoveryForm form) {
    return submissionCreateService.createNewDiscoveryFromProspect(form);
  }

  SubmissionDetail createNewDiscovery(ResourceDetailsForm form) {
    return submissionCreateService.createDiscovery(form);
  }

  BindingResult validateNewDiscovery(ResourceDetailsForm form, BindingResult bindingResult) {
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL,
            regulatorTeamService.isRegulatorUser(userService.getUser()),
            ResourceType.DISCOVERY,
            null)
    );
    return bindingResult;
  }

  BindingResult validateDiscoveryFromProspect(StartDiscoveryForm form, BindingResult bindingResult) {
    startDiscoveryFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
