package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IndustryRolesAllowed;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.AbstractHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.PermissionManagementHandlerInterceptor;

@Component
public class IndustryPermissionManagementHandlerInterceptor extends AbstractHandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndustryPermissionManagementHandlerInterceptor.class);

  private static final Set<Class<? extends Annotation>> SUPPORTED_SECURITY_ANNOTATIONS = Set.of(
      IndustryRolesAllowed.class
  );

  private final TeamMemberService teamMemberService;

  private final UserDetailService userDetailService;

  @Autowired
  public IndustryPermissionManagementHandlerInterceptor(TeamMemberService teamMemberService,
                                                         UserDetailService userDetailService) {
    this.teamMemberService = teamMemberService;
    this.userDetailService = userDetailService;
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) {

    if (handler instanceof HandlerMethod handlerMethod
        && hasAnnotations(handlerMethod, SUPPORTED_SECURITY_ANNOTATIONS)
    ) {

      var teamId = PermissionManagementHandlerInterceptor.extractTeamIdFromRequest(request, handlerMethod);

      var user = userDetailService.getUserDetail();

      if (hasAnnotation(handlerMethod, IndustryRolesAllowed.class)) {
        checkIsMemberOfTeamWithIndustryRole(teamId, user, getIndustryTeamRoles(handlerMethod));
      }
    }

    return true;
  }

  private IndustryTeamRole[] getIndustryTeamRoles(HandlerMethod handlerMethod) {
    return getAnnotationTyped(handlerMethod, IndustryRolesAllowed.class).roles();
  }

  private void checkIsMemberOfTeamWithIndustryRole(TeamId teamId, ServiceUserDetail user, IndustryTeamRole[] roles) {
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
