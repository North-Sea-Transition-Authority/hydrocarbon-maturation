package uk.co.nstauthority.hydrocarbonmaturation;

import static org.mockito.Mockito.doCallRealMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserArgumentResolver;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorOrAreaUsersInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRoleInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ProjectHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SubmissionHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.IsProjectExtantInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorForProjectFieldInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.branding.IncludeServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.ExternalApiAuthenticationEntryPoint;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.IncludeAnalyticsConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.IncludeEnergyPortalConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EpaRequestHandler;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.hibernate.HibernateQueryCounter;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.MetricsProvider;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.QueryCounter;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ErrorSummaryHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.PostAuthenticationRequestMdcFilter;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.RequestLogFilter;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ValidationTypeArgumentResolver;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.access.TeamManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.IncludeTechnicalSupportConfigurationProperties;

@WebMvcTest
@Import(value = {
    WebMvcConfiguration.class,
    AbstractControllerTest.TestConfig.class,
    AccessibleByRegulatorOrAreaUsersInterceptor.class,
    HasAnyRoleInterceptor.class,
    SubmissionStatusInterceptorRule.class,
    UserArgumentResolver.class,
    ValidationTypeArgumentResolver.class,
    ErrorSummaryHandlerInterceptor.class,
    ProjectHandlerInterceptor.class,
    IsProjectExtantInterceptorRule.class,
    RegulatorForProjectFieldInterceptorRule.class,
    MetricsProvider.class,
    TeamManagementHandlerInterceptor.class,
    RequestLogFilter.class,
    PostAuthenticationRequestMdcFilter.class,
    HibernateQueryCounter.class,
    EpaRequestHandler.class
})
@ActiveProfiles("test")
@IncludeServiceBrandingConfigurationProperties
@IncludeTechnicalSupportConfigurationProperties
@IncludeEnergyPortalConfigurationProperties
@IncludeAnalyticsConfigurationProperties
@WithDefaultPageControllerAdvice
public abstract class AbstractControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  protected TeamQueryService teamQueryService;

  @MockBean
  protected TeamManagementService teamManagementService;

  @MockBean
  protected UserService userService;

  @MockBean
  protected SubmissionDetailService submissionDetailService;

  @MockBean
  protected ProjectService projectService;

  @MockBean
  protected RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @MockBean
  protected ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @MockBean
  protected MetricsProvider metricsProvider;

  @MockBean
  protected InternalTeamService internalTeamService;

  @Autowired
  protected ControllerHelperService controllerHelperService;

  @Autowired
  protected TeamManagementHandlerInterceptor teamManagementHandlerInterceptor;

  protected User user;

  @BeforeEach
  void setupAbstractControllerTest() {
    doCallRealMethod().when(userService).getUser();
    user = UserTestUtil.Builder().build();
  }

  @TestConfiguration
  public static class TestConfig {

    @Bean
    public ControllerHelperService controllerHelperService() {
      return new ControllerHelperService();
    }

    @Bean
    public ProbabilityDistributionTemplateService probabilityDistributionService() {
      return new ProbabilityDistributionTemplateService();
    }

    @Bean
    public SubmissionHandlerInterceptor submissionHandlerInterceptor(
        SubmissionDetailService submissionDetailService,
        UserService userService,
        List<SubmissionInterceptorSecurityRule> securityRules) {
      return new SubmissionHandlerInterceptor(submissionDetailService, userService, securityRules);
    }

    @Bean
    public ExternalApiAuthenticationEntryPoint externalApiAuthenticationEntryPoint() {
      return new ExternalApiAuthenticationEntryPoint(new ObjectMapper());
    }

    @Bean
    public QueryCounter queryCounter() {
      return new QueryCounter();
    }
  }
}