package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import com.google.common.collect.Sets;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.AbstractHandlerInterceptor;

@Component
public class HasPermissionInterceptor extends AbstractHandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(HasPermissionInterceptor.class);

  private static final Set<Class<? extends Annotation>> SUPPORTED_SECURITY_ANNOTATIONS = Set.of(
      HasPermission.class
  );

  private final UserService userService;

  private final PermissionService permissionService;

  @Autowired
  public HasPermissionInterceptor(UserService userService, PermissionService permissionService) {
    this.userService = userService;
    this.permissionService = permissionService;
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) {

    if (handler instanceof HandlerMethod handlerMethod
        && hasAnnotations(handlerMethod, SUPPORTED_SECURITY_ANNOTATIONS)
    ) {

      var requiredPermissions = Sets.newHashSet(
          ((HasPermission) getAnnotation(handlerMethod, HasPermission.class)).permissions()
      );

      var user = userService.getUser();

      if (!permissionService.hasPermission(user, requiredPermissions)) {

        var requiredPermissionNames = requiredPermissions
            .stream()
            .map(RolePermission::name)
            .toList();

        var errorMessage = "User with ID %s doesn't have any of the required permissions %s"
            .formatted(user.wuaId(), StringUtils.join(requiredPermissionNames));

        LOGGER.warn(errorMessage);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
      }
    }

    return true;
  }
}
