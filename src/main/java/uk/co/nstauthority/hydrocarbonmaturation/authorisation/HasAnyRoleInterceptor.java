package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import com.google.common.collect.Sets;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.AbstractHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;

@Component
public class HasAnyRoleInterceptor extends AbstractHandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(HasAnyRoleInterceptor.class);

  private static final Set<Class<? extends Annotation>> SUPPORTED_SECURITY_ANNOTATIONS = Set.of(
      HasAnyRoles.class,
      HasAnyRole.class
  );

  private final UserService userService;
  private final TeamQueryService teamQueryService;

  @Autowired
  public HasAnyRoleInterceptor(UserService userService, TeamQueryService teamQueryService) {
    this.userService = userService;
    this.teamQueryService = teamQueryService;
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) {

    if (handler instanceof HandlerMethod handlerMethod
        && hasAnnotations(handlerMethod, SUPPORTED_SECURITY_ANNOTATIONS)
    ) {

      Set<HasAnyRole> roles = new HashSet<>();

      if (hasAnnotation(handlerMethod, HasAnyRoles.class)) {
        roles = Sets.newHashSet(
            ((HasAnyRoles) getAnnotation(handlerMethod, HasAnyRoles.class)).value()
        );
      } else if (hasAnnotation(handlerMethod, HasAnyRole.class)) {
        roles = Sets.newHashSet(((HasAnyRole) getAnnotation(handlerMethod, HasAnyRole.class)));
      }

      var user = userService.getUser();

      var userHasRole = roles.stream()
          .anyMatch(role -> teamQueryService.userHasRoleInTeamType(user.wuaId(), role.teamType(), role.role()));

      if (!userHasRole) {

        var requiredRoles = String.join(", ",
            roles
            .stream()
            .map(HasAnyRole::role)
            .map(Role::name)
            .distinct()
            .toList());

        var errorMessage = "User with ID %s doesn't have any of the required roles: %s"
            .formatted(user.wuaId(), requiredRoles);

        LOGGER.warn(errorMessage);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
      }
    }

    return true;
  }
}
