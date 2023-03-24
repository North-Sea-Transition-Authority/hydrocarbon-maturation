package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AllowedSubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionTypeInterceptorRuleTest extends AbstractInterceptorRuleTest {

  @InjectMocks
  private SubmissionTypeInterceptorRule rule;

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY",
          "UPDATE_DISCOVERY",
          "RELINQUISHMENT_DISCOVERY"},
      mode = EnumSource.Mode.INCLUDE)
  void validate_discovery_whenDiscovery(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDiscovery.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY",
          "UPDATE_DISCOVERY",
          "RELINQUISHMENT_DISCOVERY"},
      mode = EnumSource.Mode.EXCLUDE)
  void validate_discovery_whenNotDiscovery(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDiscovery.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_PROSPECT",
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY"
      },
      mode = EnumSource.Mode.INCLUDE)
  void validate_createResourceSubmissions_whenCreateResource(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType())
        .thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerCreateProspect.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_PROSPECT",
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY"
      },
      mode = EnumSource.Mode.EXCLUDE)
  void validate_createResourceSubmissions_whenNotCreateResourceSubmission(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerCreateProspect.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_PROSPECT",
          "PROSPECT_UPDATE",
          "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void validate_prospect_whenProspect(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerProspect.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_PROSPECT",
          "PROSPECT_UPDATE",
          "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.EXCLUDE)
  void validate_prospect_whenNotProspect(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerProspect.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY",
          "UPDATE_DISCOVERY",
          "RELINQUISHMENT_DISCOVERY",
          "POST_DRILL_DRY_HOLE"},
      mode = EnumSource.Mode.INCLUDE)
  void validate_discoveryAndPostDrill_whenDiscoveryAndPostDrill(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDiscoveryAndPostDrill.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY",
          "UPDATE_DISCOVERY",
          "RELINQUISHMENT_DISCOVERY",
          "POST_DRILL_DRY_HOLE"},
      mode = EnumSource.Mode.EXCLUDE)
  void validate_discoveryAndPostDrill_whenNotDiscoveryAndPostDrill(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDiscoveryAndPostDrill.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void validate_none_whenAny(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerNone.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY",
          "UPDATE_DISCOVERY",
          "RELINQUISHMENT_DISCOVERY",
          "CREATE_PROSPECT",
          "PROSPECT_UPDATE",
          "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void validate_discoveryAndProspect_whenDiscoveryOrProspect(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDiscoveryAndProspect.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_DISCOVERY_FROM_PROSPECT",
          "CREATE_DISCOVERY",
          "UPDATE_DISCOVERY",
          "RELINQUISHMENT_DISCOVERY",
          "CREATE_PROSPECT",
          "PROSPECT_UPDATE",
          "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.EXCLUDE)
  void validate_discoveryAndProspect_whenNotDiscoveryOrProspect(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDiscoveryAndProspect.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"POST_DRILL_DRY_HOLE"},
      mode = EnumSource.Mode.EXCLUDE)
  void validate_dryHole_whenNotDryHole(SubmissionType submissionType) {
    when(submissionDetail.getSubmissionType()).thenReturn(submissionType);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDryHole.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void validate_dryHole_whenDryHole() {
    when(submissionDetail.getSubmissionType()).thenReturn(SubmissionType.POST_DRILL_DRY_HOLE);

    var annotation = getAnnotation(
        SubmissionTypeInterceptorRuleTest.TestControllerDryHole.class,
        HasSubmissionType.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        ServiceUserDetailTestUtil.Builder().build(),
        submissionDetail
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @Controller
  @RequestMapping("/route1")
  @HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.DISCOVERY_TYPES)
  private static class TestControllerDiscovery {
    @GetMapping
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("/route2")
  @HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.CREATE_RESOURCE_SUBMISSIONS)
  private static class TestControllerCreateProspect {
    @GetMapping
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("/route3")
  @HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.PROSPECT_TYPES)
  private static class TestControllerProspect {
    @GetMapping
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("/route4")
  @HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.DISCOVERY_AND_POST_DRILL)
  private static class TestControllerDiscoveryAndPostDrill {
    @GetMapping
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("/route5")
  @HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.NONE)
  private static class TestControllerNone {
    @GetMapping
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("/route6")
  @HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.DISCOVERY_AND_PROSPECT)
  private static class TestControllerDiscoveryAndProspect {
    @GetMapping
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("/route7")
  @HasSubmissionType(allowedSubmissionTypes = AllowedSubmissionTypes.POST_DRILL_DRY_HOLE)
  private static class TestControllerDryHole {
    @GetMapping
    String get() {
      return "some data";
    }
  }
}