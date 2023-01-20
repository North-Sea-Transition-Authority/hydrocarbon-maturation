package uk.co.nstauthority.hydrocarbonmaturation;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorUsersInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermissionInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SubmissionHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionInterceptorSecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.branding.IncludeServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.IncludeEnergyPortalConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.PermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryPermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorPermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.IncludeTechnicalSupportConfigurationProperties;

@WebMvcTest
@Import(value = {
    WebMvcConfiguration.class,
    AbstractControllerTest.TestConfig.class,
    PermissionManagementHandlerInterceptor.class,
    RegulatorPermissionManagementHandlerInterceptor.class,
    IndustryPermissionManagementHandlerInterceptor.class,
    AccessibleByRegulatorUsersInterceptor.class,
    HasPermissionInterceptor.class,
    PermissionService.class,
    SubmissionStatusInterceptorRule.class})
@ActiveProfiles("test")
@IncludeServiceBrandingConfigurationProperties
@IncludeTechnicalSupportConfigurationProperties
@IncludeEnergyPortalConfigurationProperties
@WithDefaultPageControllerAdvice
public abstract class AbstractControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  protected TeamMemberService teamMemberService;

  @MockBean
  protected TeamService teamService;

  @MockBean
  protected UserDetailService userDetailService;

  @MockBean
  protected SubmissionDetailService submissionDetailService;

  @MockBean
  protected RegulatorTeamService regulatorTeamService;

  protected static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  protected SubmissionDetail submissionDetail;

  @BeforeEach
  void setupAbstractControllerTest() {
    doCallRealMethod().when(userDetailService).getUserDetail();
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, SUBMISSION_DETAIL_ID);
    when(submissionDetailService.getSubmissionDetail(SUBMISSION_DETAIL_ID)).thenReturn(Optional.of(submissionDetail));
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
        UserDetailService userDetailService,
        List<SubmissionInterceptorSecurityRule> securityRules
    ) {
      return new SubmissionHandlerInterceptor(submissionDetailService, userDetailService, securityRules);
    }
  }

}