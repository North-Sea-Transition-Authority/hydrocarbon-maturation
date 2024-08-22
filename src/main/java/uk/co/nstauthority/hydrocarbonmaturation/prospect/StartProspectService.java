package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
class StartProspectService {

  private final SubmissionCreateService submissionCreateService;
  private final ResourceDetailsFormValidator resourceDetailsFormValidator;
  private final TeamQueryService teamQueryService;

  @Autowired
  StartProspectService(SubmissionCreateService submissionCreateService,
                       ResourceDetailsFormValidator resourceDetailsFormValidator,
                       TeamQueryService teamQueryService) {
    this.submissionCreateService = submissionCreateService;
    this.resourceDetailsFormValidator = resourceDetailsFormValidator;
    this.teamQueryService = teamQueryService;
  }

  SubmissionDetail createInitialProspect(ResourceDetailsForm form, User user) {
    return submissionCreateService.createInitialProspect(form, user);
  }

  BindingResult validate(ResourceDetailsForm form, BindingResult bindingResult, User user) {
    resourceDetailsFormValidator.validate(form, bindingResult,
        new ResourceDetailsFormValidatorHints(
            ValidationType.FULL,
            teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION),
            ResourceType.PROSPECT,
            null)
    );
    return bindingResult;
  }
}
