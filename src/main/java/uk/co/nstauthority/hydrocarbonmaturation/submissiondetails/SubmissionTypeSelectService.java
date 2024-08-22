package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;

@Service
public class SubmissionTypeSelectService {

  private final SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;
  private final TeamQueryService teamQueryService;

  @Autowired
  public SubmissionTypeSelectService(SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator,
                                     TeamQueryService teamQueryService) {
    this.submissionTypeSelectFormValidator = submissionTypeSelectFormValidator;
    this.teamQueryService = teamQueryService;
  }

  public BindingResult validate(SubmissionTypeSelectForm form, BindingResult bindingResult) {
    submissionTypeSelectFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  public List<SubmissionType> getSubmissionTypeOptions(ResourceType resourceType, User user) {
    var userRoles = teamQueryService.getTeamRolesForUser(user.wuaId()).stream()
        .map(TeamRole::getRole)
        .collect(Collectors.toSet());
    return SubmissionType.getSubmissionTypesForResourceType(resourceType).stream()
        .filter(submissionType -> submissionType.getRoles().stream().anyMatch(userRoles::contains))
        .toList();
  }
}
