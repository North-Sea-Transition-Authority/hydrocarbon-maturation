package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserArgumentResolver;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorOrAreaUsersInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermissionInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasTeamPermissionInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ProjectHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SubmissionHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.PermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.IndustryPermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorPermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.access.TeamManagementHandlerInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  private static final String ASSETS_PATH = "/assets/**";
  private static final String SUBMISSION_DETAILS_PATH = "/submission-details/**";
  private static final String REVIEW_ACTION_SUBMISSION_PATH = "/review/**/*";
  private static final String REVIEW_VIEW_SUBMISSION_PATH = "/review/resource/**";
  private static final String PROJECT_TRACKER_PATH = "/view-resource-record/*/project-tracker/*/**";
  private static final String CREATE_PROJECT_PATH = "/view-resource-record/*/project-tracker/create-project";

  private final PermissionManagementHandlerInterceptor permissionManagementHandlerInterceptor;
  private final RegulatorPermissionManagementHandlerInterceptor regulatorPermissionManagementHandlerInterceptor;
  private final IndustryPermissionManagementHandlerInterceptor industryPermissionManagementHandlerInterceptor;
  private final HasPermissionInterceptor hasPermissionInterceptor;
  private final HasTeamPermissionInterceptor hasTeamPermissionInterceptor;
  private final SubmissionHandlerInterceptor submissionHandlerInterceptor;
  private final AccessibleByRegulatorOrAreaUsersInterceptor accessibleByRegulatorOrAreaUsersInterceptor;
  private final ValidationTypeArgumentResolver validationTypeArgumentResolver;
  private final UserArgumentResolver userArgumentResolver;
  private final ErrorSummaryHandlerInterceptor errorSummaryHandlerInterceptor;
  private final ProjectHandlerInterceptor projectHandlerInterceptor;
  private final TeamManagementHandlerInterceptor teamManagementHandlerInterceptor;

  @Autowired
  public WebMvcConfiguration(PermissionManagementHandlerInterceptor permissionManagementHandlerInterceptor,
                             RegulatorPermissionManagementHandlerInterceptor regulatorPermissionManagementHandlerInterceptor,
                             IndustryPermissionManagementHandlerInterceptor industryPermissionManagementHandlerInterceptor,
                             HasPermissionInterceptor hasPermissionInterceptor,
                             HasTeamPermissionInterceptor hasTeamPermissionInterceptor,
                             SubmissionHandlerInterceptor submissionHandlerInterceptor,
                             AccessibleByRegulatorOrAreaUsersInterceptor accessibleByRegulatorOrAreaUsersInterceptor,
                             ValidationTypeArgumentResolver validationTypeArgumentResolver,
                             UserArgumentResolver userArgumentResolver,
                             ErrorSummaryHandlerInterceptor errorSummaryHandlerInterceptor,
                             ProjectHandlerInterceptor projectHandlerInterceptor,
                             TeamManagementHandlerInterceptor teamManagementHandlerInterceptor) {
    this.permissionManagementHandlerInterceptor = permissionManagementHandlerInterceptor;
    this.regulatorPermissionManagementHandlerInterceptor = regulatorPermissionManagementHandlerInterceptor;
    this.industryPermissionManagementHandlerInterceptor = industryPermissionManagementHandlerInterceptor;
    this.hasPermissionInterceptor = hasPermissionInterceptor;
    this.hasTeamPermissionInterceptor = hasTeamPermissionInterceptor;
    this.submissionHandlerInterceptor = submissionHandlerInterceptor;
    this.accessibleByRegulatorOrAreaUsersInterceptor = accessibleByRegulatorOrAreaUsersInterceptor;
    this.validationTypeArgumentResolver = validationTypeArgumentResolver;
    this.userArgumentResolver = userArgumentResolver;
    this.errorSummaryHandlerInterceptor = errorSummaryHandlerInterceptor;
    this.projectHandlerInterceptor = projectHandlerInterceptor;
    this.teamManagementHandlerInterceptor = teamManagementHandlerInterceptor;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(ASSETS_PATH)
        .addResourceLocations("classpath:/public/assets/")
        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
        .resourceChain(false)
        .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(validationTypeArgumentResolver);
    resolvers.add(userArgumentResolver);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new ResponseBufferSizeHandlerInterceptor())
        .excludePathPatterns(ASSETS_PATH);
    registry.addInterceptor(permissionManagementHandlerInterceptor)
        .addPathPatterns("/permission-management/**");
    registry.addInterceptor(hasTeamPermissionInterceptor)
        .addPathPatterns("/permission-management/**");
    registry.addInterceptor(regulatorPermissionManagementHandlerInterceptor)
        .addPathPatterns("/permission-management/regulator/**");
    registry.addInterceptor(industryPermissionManagementHandlerInterceptor)
        .addPathPatterns("/permission-management/industry/**");
    registry.addInterceptor(submissionHandlerInterceptor)
        .addPathPatterns(SUBMISSION_DETAILS_PATH, REVIEW_ACTION_SUBMISSION_PATH)
        .excludePathPatterns(REVIEW_VIEW_SUBMISSION_PATH);
    registry.addInterceptor(projectHandlerInterceptor)
        .addPathPatterns(PROJECT_TRACKER_PATH)
            .excludePathPatterns(CREATE_PROJECT_PATH);
    registry.addInterceptor(hasPermissionInterceptor)
        .excludePathPatterns(ASSETS_PATH);
    registry.addInterceptor(accessibleByRegulatorOrAreaUsersInterceptor)
        .excludePathPatterns(ASSETS_PATH);
    registry.addInterceptor(errorSummaryHandlerInterceptor)
        .excludePathPatterns(ASSETS_PATH);
    registry.addInterceptor(teamManagementHandlerInterceptor)
        .addPathPatterns("/team-management/**");
  }

  @Bean
  public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
    return new ResourceUrlEncodingFilter();
  }
}