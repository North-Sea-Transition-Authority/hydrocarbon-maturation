package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.IsProjectExtantInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.ProjectInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;

@ExtendWith(MockitoExtension.class)
class ProjectHandlerInterceptorTest {
  private static final UUID PROJECT_ID = UUID.randomUUID();

  @Mock
  private ProjectService projectService;
  @Mock
  private UserService userService;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private HandlerMethod handlerMethod;
  @Mock
  private IsProjectExtantInterceptorRule isProjectExtantInterceptorRule;

  private Map<String, String> uriVariables;
  private ProjectHandlerInterceptor projectHandlerInterceptor;

  @BeforeEach
  void setUp() {
    var securityRules = new ArrayList<ProjectInterceptorSecurityRule>();
    securityRules.add(isProjectExtantInterceptorRule);
    this.uriVariables = new HashMap<>();

    this.projectHandlerInterceptor = new ProjectHandlerInterceptor(
        projectService,
        userService,
        securityRules
    );

    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(uriVariables);
  }

  @Test
  void preHandle_noEndpointSecurityController() throws NoSuchMethodException {
    uriVariables.put("projectId", PROJECT_ID.toString());

    var controller = TestControllers.NoEndpointSecurityController.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);

    assertThatThrownBy(() -> projectHandlerInterceptor.preHandle(request, response, handlerMethod)).isInstanceOf(IllegalStateException.class);

    verifyNoInteractions(projectService);
  }

  @Test
  void preHandle_endpointDisabledController() throws NoSuchMethodException, IOException {
    uriVariables.put("projectId", PROJECT_ID.toString());

    var controller = TestControllers.EndpointSecurityDisabledController.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);

    var interceptorResult = projectHandlerInterceptor.preHandle(request, response, handlerMethod);
    assertThat(interceptorResult).isTrue();

    verifyNoInteractions(projectService);
  }

  @Test
  void preHandle_controllerWithoutProjectId() throws NoSuchMethodException {
    var controller = TestControllers.ControllerWithoutProjectId.class;
    var method = controller.getDeclaredMethod("get");
    when(handlerMethod.getMethod()).thenReturn(method);

    assertThatThrownBy(() -> projectHandlerInterceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(ResponseStatusException.class)
        .matches(e -> ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND));

    verifyNoInteractions(projectService);
  }

  @Test
  void preHandle_controllerWhenProjectNotFound() throws NoSuchMethodException {
    uriVariables.put("projectId", PROJECT_ID.toString());

    var controller = TestControllers.ControllerWithProjectId.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);
    when(projectService.getProject(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> projectHandlerInterceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(ResponseStatusException.class)
        .matches(e -> ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND));
  }

  @Test
  void preHandle_controllerWhenProjectFound() throws NoSuchMethodException, IOException {
    uriVariables.put("projectId", PROJECT_ID.toString());

    when(projectService.getProject(PROJECT_ID))
        .thenReturn(Optional.of(ProjectTestUtils.createProject(FieldTestUtils.createField(8000), PROJECT_ID)));

    var controller = TestControllers.ControllerWithProjectId.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);

    var interceptorResult = projectHandlerInterceptor.preHandle(request, response, handlerMethod);
    assertThat(interceptorResult).isTrue();

    verify(isProjectExtantInterceptorRule).supports();
  }
}