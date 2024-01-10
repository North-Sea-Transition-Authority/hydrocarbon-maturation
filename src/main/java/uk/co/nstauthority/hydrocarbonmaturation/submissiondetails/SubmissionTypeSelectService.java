package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@Service
public class SubmissionTypeSelectService {

  private final SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;
  private final UserService userService;
  private final PermissionService permissionService;

  @Autowired
  public SubmissionTypeSelectService(SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator,
                                     UserService userService, PermissionService permissionService) {
    this.submissionTypeSelectFormValidator = submissionTypeSelectFormValidator;
    this.userService = userService;
    this.permissionService = permissionService;
  }

  public BindingResult validate(SubmissionTypeSelectForm form, BindingResult bindingResult) {
    submissionTypeSelectFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  public List<SubmissionType> getSubmissionTypeOptions(ResourceType resourceType) {
    var user = userService.getUser();
    return SubmissionType.getSubmissionTypesForResourceType(resourceType).stream()
        .filter(submissionType -> permissionService.hasPermission(user, submissionType.getPermissions()))
        .toList();
  }
}
