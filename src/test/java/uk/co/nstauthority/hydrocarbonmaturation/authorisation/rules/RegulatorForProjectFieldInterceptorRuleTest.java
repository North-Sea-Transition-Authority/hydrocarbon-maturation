package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorForProjectField;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class RegulatorForProjectFieldInterceptorRuleTest extends AbstractInterceptorRuleTest{
  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @Mock
  private TeamQueryService teamQueryService;
  @InjectMocks
  private RegulatorForProjectFieldInterceptorRule rule;

  private final User USER = UserTestUtil.Builder().build();

  @BeforeEach
  void setUp() {
    project = ProjectTestUtils.createProject(FieldTestUtils.createField(8000), UUID.randomUUID());
  }

  @Test
  void validate_isProjectAnalyst() {
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
        .thenReturn(true);

    var annotation = getAnnotation(
        RegulatorForProjectFieldInterceptorRuleTest.TestControllerExtant.class,
        IsRegulatorForProjectField.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        USER,
        project
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  @SuppressWarnings("unchecked")
  void validate_isRegulator_andIsResponsibleForField(ResponsibleTeamType responsibleTeamType) {
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
        .thenReturn(false);

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(project.getField().getResource()))
        .thenReturn(regulatorManagedInformation);

    when(teamQueryService.userHasStaticRole(USER.wuaId(), responsibleTeamType.getTeamType(), responsibleTeamType.getRole()))
        .thenReturn(true);

    var annotation = getAnnotation(
        RegulatorForProjectFieldInterceptorRuleTest.TestControllerExtant.class,
        IsRegulatorForProjectField.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        USER,
        project
    );

    assertThat(interceptorResult.hasRulePassed()).isTrue();
    verifyNoInteractions(response);
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  @SuppressWarnings("unchecked")
  void validate_isRegulator_andIsNotResponsibleForField(ResponsibleTeamType responsibleTeamType) {
    when(teamQueryService.userHasStaticRole(USER.wuaId(), TeamType.REGULATOR, Role.PROJECT_ANALYST))
            .thenReturn(false);

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(project.getField().getResource()))
            .thenReturn(regulatorManagedInformation);

    when(teamQueryService.userHasStaticRole(USER.wuaId(), responsibleTeamType.getTeamType(), responsibleTeamType.getRole()))
            .thenReturn(false);

    var annotation = getAnnotation(
        RegulatorForProjectFieldInterceptorRuleTest.TestControllerExtant.class,
        IsRegulatorForProjectField.class
    );
    var interceptorResult = rule.check(
        annotation,
        request,
        response,
        USER,
        project
    );

    assertThat(interceptorResult.hasRulePassed()).isFalse();
    assertThat(interceptorResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Controller
  @RequestMapping("/route")
  @IsRegulatorForProjectField
  private static class TestControllerExtant {
    @GetMapping
    String get() {
      return "some data";
    }
  }
}