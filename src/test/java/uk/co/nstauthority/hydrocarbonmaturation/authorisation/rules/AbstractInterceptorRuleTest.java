package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.AnnotationUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
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
