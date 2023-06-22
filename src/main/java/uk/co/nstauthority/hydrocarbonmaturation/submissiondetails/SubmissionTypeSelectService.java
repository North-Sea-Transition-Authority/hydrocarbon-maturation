package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@Service
public class SubmissionTypeSelectService {

  private final SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;
  private final UserDetailService userDetailService;
  private final PermissionService permissionService;

  @Autowired
  public SubmissionTypeSelectService(SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator,
                                     UserDetailService userDetailService, PermissionService permissionService) {
    this.submissionTypeSelectFormValidator = submissionTypeSelectFormValidator;
    this.userDetailService = userDetailService;
    this.permissionService = permissionService;
  }

  public BindingResult validate(SubmissionTypeSelectForm form, BindingResult bindingResult) {
    submissionTypeSelectFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  public Map<String, String> getSubmissionTypeOptions(ResourceType resourceType) {
    var userDetail = userDetailService.getUserDetail();
    return SubmissionType.getSubmissionTypesForResourceType(resourceType).stream()
        .filter(submissionType -> permissionService.hasPermission(userDetail, submissionType.getPermissions()))
        .collect(StreamUtil.toLinkedHashMap(Enum::name, SubmissionType::getDisplayName));
  }
}
