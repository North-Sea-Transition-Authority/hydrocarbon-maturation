package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class StartDiscoveryService {

  private final SubmissionCreateService submissionCreateService;
  private final StartDiscoveryFormValidator startDiscoveryFormValidator;
  private final ResourceDetailsFormValidator resourceDetailsFormValidator;
  private final TeamQueryService teamQueryService;

  @Autowired
  StartDiscoveryService(SubmissionCreateService submissionCreateService,
                        StartDiscoveryFormValidator startDiscoveryFormValidator,
                        ResourceDetailsFormValidator resourceDetailsFormValidator, TeamQueryService teamQueryService) {
    this.submissionCreateService = submissionCreateService;
    this.startDiscoveryFormValidator = startDiscoveryFormValidator;
    this.resourceDetailsFormValidator = resourceDetailsFormValidator;
    this.teamQueryService = teamQueryService;
  }

  SubmissionDetail createNewDiscoveryFromProspect(StartDiscoveryForm form, User user) {
    return submissionCreateService.createNewDiscoveryFromProspect(form, user);
  }

  SubmissionDetail createNewDiscovery(ResourceDetailsForm form, User user) {
    return submissionCreateService.createDiscovery(form, user);
  }

  BindingResult validateNewDiscovery(ResourceDetailsForm form, BindingResult bindingResult, User user) {
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL,
            teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION),
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
