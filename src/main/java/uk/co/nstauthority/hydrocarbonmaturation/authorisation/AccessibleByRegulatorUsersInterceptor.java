package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.lang.annotation.Annotation;
import java.util.Set;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.AbstractHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Component
public class AccessibleByRegulatorUsersInterceptor extends AbstractHandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessibleByRegulatorUsersInterceptor.class);

  private static final Set<Class<? extends Annotation>> SUPPORTED_SECURITY_ANNOTATIONS = Set.of(
      AccessibleByRegulatorUsers.class
  );

  private final UserService userService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;

  @Autowired
  public AccessibleByRegulatorUsersInterceptor(UserService userService,
                                               RegulatorTeamService regulatorTeamService,
                                               AreaTeamService areaTeamService) {
    this.userService = userService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) {

    if (handler instanceof HandlerMethod handlerMethod
        && hasAnnotations(handlerMethod, SUPPORTED_SECURITY_ANNOTATIONS)
    ) {

      var user = userService.getUser();

      if (!(regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user))) {

        var errorMessage = "User with ID %s is not a regulator user".formatted(user.wuaId());

        LOGGER.warn(errorMessage);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
      }
    }

    return true;
  }
}
