package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class SubmissionHandlerInterceptorTest {

  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private UserService userService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private HandlerMethod handlerMethod;

  @Mock
  private SubmissionStatusInterceptorRule submissionStatusInterceptorRule;

  private Map<String, String> uriVariables;

  private SubmissionHandlerInterceptor submissionHandlerInterceptor;

  @BeforeEach
  void setUp() {
    var securityRules = new ArrayList<SubmissionInterceptorSecurityRule>();
    securityRules.add(submissionStatusInterceptorRule);
    this.uriVariables = new HashMap<>();

    this.submissionHandlerInterceptor = new SubmissionHandlerInterceptor(
        submissionDetailService,
        userService,
        securityRules
    );

    when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(uriVariables);
  }

  @Test
  void preHandle_noEndpointSecurityController() throws NoSuchMethodException {
    uriVariables.put("submissionDetailId", SUBMISSION_DETAIL_ID.toString());

    var controller = TestControllers.NoEndpointSecurityController.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);

    assertThatThrownBy(() -> submissionHandlerInterceptor.preHandle(request, response, handlerMethod)).isInstanceOf(IllegalStateException.class);

    verifyNoInteractions(submissionDetailService);
  }

  @Test
  void preHandle_endpointDisabledController() throws NoSuchMethodException, IOException {
    uriVariables.put("submissionDetailId", SUBMISSION_DETAIL_ID.toString());

    var controller = TestControllers.EndpointSecurityDisabledController.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);

    var interceptorResult = submissionHandlerInterceptor.preHandle(request, response, handlerMethod);
    assertThat(interceptorResult).isTrue();

    verifyNoInteractions(submissionDetailService);
  }

  @Test
  void preHandle_controllerWithoutSubmissionDetailId() throws NoSuchMethodException {
    var controller = TestControllers.ControllerWithoutSubmissionDetailId.class;
    var method = controller.getDeclaredMethod("get");
    when(handlerMethod.getMethod()).thenReturn(method);

    assertThatThrownBy(() -> submissionHandlerInterceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(ResponseStatusException.class)
        .matches(e -> ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND));

    verifyNoInteractions(submissionDetailService);
  }

  @Test
  void preHandle_controllerWhenSubmissionDetailNotFound() throws NoSuchMethodException {
    uriVariables.put("submissionDetailId", SUBMISSION_DETAIL_ID.toString());

    var controller = TestControllers.ControllerWithSubmissionDetailId.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);
    when(submissionDetailService.getSubmissionDetail(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> submissionHandlerInterceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(ResponseStatusException.class)
        .matches(e -> ((ResponseStatusException) e).getStatusCode().equals(HttpStatus.NOT_FOUND));

  }

  @Test
  void preHandle_controllerWhenSubmissionDetailFound() throws NoSuchMethodException, IOException {
    uriVariables.put("submissionDetailId", SUBMISSION_DETAIL_ID.toString());

    when(submissionDetailService.getSubmissionDetail(SUBMISSION_DETAIL_ID))
        .thenReturn(Optional.of(SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT)));

    var controller = TestControllers.ControllerWithSubmissionDetailId.class;
    var method = controller.getDeclaredMethod("get", UUID.class);
    when(handlerMethod.getMethod()).thenReturn(method);

    var interceptorResult = submissionHandlerInterceptor.preHandle(request, response, handlerMethod);
    assertThat(interceptorResult).isTrue();

    verify(submissionStatusInterceptorRule).supports();
  }
}
