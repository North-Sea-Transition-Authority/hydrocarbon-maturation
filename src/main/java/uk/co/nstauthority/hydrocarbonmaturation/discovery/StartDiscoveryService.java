package uk.co.nstauthority.hydrocarbonmaturation.discovery;

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
class StartDiscoveryService {

  private final SubmissionCreateService submissionCreateService;
  private final StartDiscoveryFormValidator startDiscoveryFormValidator;
  private final NewResourceFormValidator newResourceFormValidator;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  StartDiscoveryService(SubmissionCreateService submissionCreateService,
                        StartDiscoveryFormValidator startDiscoveryFormValidator,
                        NewResourceFormValidator newResourceFormValidator,
                        UserDetailService userDetailService,
                        RegulatorTeamService regulatorTeamService) {
    this.submissionCreateService = submissionCreateService;
    this.startDiscoveryFormValidator = startDiscoveryFormValidator;
    this.newResourceFormValidator = newResourceFormValidator;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
  }

  SubmissionDetail createNewDiscoveryFromProspect(StartDiscoveryForm form) {
    return submissionCreateService.createNewDiscoveryFromProspect(form);
  }

  SubmissionDetail createNewDiscovery(NewResourceForm form) {
    return submissionCreateService.createDiscovery(form);
  }

  BindingResult validateNewDiscovery(NewResourceForm form, BindingResult bindingResult) {
    newResourceFormValidator.validate(form, bindingResult,
        new NewResourceFormValidatorHints(
            regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail()),
            ResourceType.DISCOVERY)
    );
    return bindingResult;
  }

  BindingResult validateDiscoveryFromProspect(StartDiscoveryForm form, BindingResult bindingResult) {
    startDiscoveryFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
