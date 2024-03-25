package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;

class IsProjectExtantInterceptorRuleTest extends AbstractInterceptorRuleTest{

  private IsProjectExtantInterceptorRule rule;

  @BeforeEach
  void setUp() {
    rule = new IsProjectExtantInterceptorRule();
  }

  @Test
  void validate_extant() {
    when(project.getProjectStatus()).thenReturn(ProjectStatus.EXTANT);

    var annotation = getAnnotation(
        TestControllerExtant.class,
        IsExtantProject.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        UserTestUtil.Builder().build(),
        project
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @Test
  void validate_deleted() {
    when(project.getProjectStatus()).thenReturn(ProjectStatus.DELETED);

    var annotation = getAnnotation(
        TestControllerExtant.class,
        IsExtantProject.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        UserTestUtil.Builder().build(),
        project
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    verifyNoInteractions(response);
  }

  @Controller
  @RequestMapping("/route")
  @IsExtantProject
  private static class TestControllerExtant {
    @GetMapping
    String get() {
      return "some data";
    }
  }
}