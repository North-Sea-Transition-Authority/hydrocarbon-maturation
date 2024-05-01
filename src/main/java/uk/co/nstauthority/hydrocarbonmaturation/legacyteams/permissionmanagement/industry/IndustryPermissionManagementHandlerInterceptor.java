package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IndustryRolesAllowed;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.PermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.AbstractHandlerInterceptor;

@Component
public class IndustryPermissionManagementHandlerInterceptor extends AbstractHandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndustryPermissionManagementHandlerInterceptor.class);

  private static final Set<Class<? extends Annotation>> SUPPORTED_SECURITY_ANNOTATIONS = Set.of(
      IndustryRolesAllowed.class
  );

  private final TeamMemberService teamMemberService;

  private final UserService userService;

  @Autowired
  public IndustryPermissionManagementHandlerInterceptor(TeamMemberService teamMemberService,
                                                         UserService userService) {
    this.teamMemberService = teamMemberService;
    this.userService = userService;
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) {

    if (handler instanceof HandlerMethod handlerMethod
        && hasAnnotations(handlerMethod, SUPPORTED_SECURITY_ANNOTATIONS)
    ) {

      var teamId = PermissionManagementHandlerInterceptor.extractTeamIdFromRequest(request, handlerMethod);

      var user = userService.getUser();

      if (hasAnnotation(handlerMethod, IndustryRolesAllowed.class)) {
        checkIsMemberOfTeamWithIndustryRole(teamId, user, getIndustryTeamRoles(handlerMethod));
      }
    }

    return true;
  }

  private IndustryTeamRole[] getIndustryTeamRoles(HandlerMethod handlerMethod) {
    return getAnnotationTyped(handlerMethod, IndustryRolesAllowed.class).roles();
  }

  private void checkIsMemberOfTeamWithIndustryRole(TeamId teamId, User user, IndustryTeamRole[] roles) {
    var roleNames = Arrays.stream((roles))
        .map(IndustryTeamRole::name)
        .collect(Collectors.toSet());

    if (!teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, roleNames)) {

      var errorMessage = "User with ID %s does not have any role [%s] for team with ID %s"
          .formatted(user.wuaId(), String.join(", ", roleNames), teamId.uuid());

      LOGGER.warn(errorMessage);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
    }
  }

}
