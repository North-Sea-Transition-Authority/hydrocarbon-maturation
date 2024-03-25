package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.UUID;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.AnnotationUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
abstract class AbstractInterceptorRuleTest {

  protected static final UUID SUBMISSION_DETAILS_ID = UUID.randomUUID();

  @Mock
  protected HttpServletRequest request;

  @Mock
  protected HttpServletResponse response;

  @Autowired
  protected User users;

  @Mock
  protected SubmissionDetail submissionDetail;

  @Mock
  protected Project project;

  protected <A extends Annotation> A getAnnotation(Class<?> controllerClass, Class<A> annotationClass) {
    var annotation = AnnotationUtils.findAnnotation(controllerClass, annotationClass);
    assertThat(annotation).isPresent();
    return annotation.get();
  }

  protected <A extends Annotation> A getAnnotation(Method method, Class<A> annotationClass) {
    var annotation = AnnotationUtils.findAnnotation(method, annotationClass);
    assertThat(annotation).isPresent();
    return annotation.get();
  }

}
