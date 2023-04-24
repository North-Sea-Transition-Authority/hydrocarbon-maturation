package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorUsersInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermissionInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasTeamPermissionInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SubmissionHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.StringToResourceSupportingDocumentTypeConverter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.StringToSupportingDocumentTypeConverter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.PermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryPermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorPermissionManagementHandlerInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  private static final String ASSETS_PATH = "/assets/**";
  private static final String SUBMISSION_DETAILS_PATH = "/submission-details/**";
  private static final String REVIEW_ACTION_SUBMISSION_PATH = "/review/**/*";
  private static final String REVIEW_VIEW_SUBMISSION_PATH = "/review/resource/**";

  private final PermissionManagementHandlerInterceptor permissionManagementHandlerInterceptor;
  private final RegulatorPermissionManagementHandlerInterceptor regulatorPermissionManagementHandlerInterceptor;
  private final IndustryPermissionManagementHandlerInterceptor industryPermissionManagementHandlerInterceptor;
  private final HasPermissionInterceptor hasPermissionInterceptor;
  private final HasTeamPermissionInterceptor hasTeamPermissionInterceptor;
  private final SubmissionHandlerInterceptor submissionHandlerInterceptor;
  private final AccessibleByRegulatorUsersInterceptor accessibleByRegulatorUsersInterceptor;

  @Autowired
  public WebMvcConfiguration(PermissionManagementHandlerInterceptor permissionManagementHandlerInterceptor,
                             RegulatorPermissionManagementHandlerInterceptor regulatorPermissionManagementHandlerInterceptor,
                             IndustryPermissionManagementHandlerInterceptor industryPermissionManagementHandlerInterceptor,
                             HasPermissionInterceptor hasPermissionInterceptor,
                             HasTeamPermissionInterceptor hasTeamPermissionInterceptor,
                             SubmissionHandlerInterceptor submissionHandlerInterceptor,
                             AccessibleByRegulatorUsersInterceptor accessibleByRegulatorUsersInterceptor) {
    this.permissionManagementHandlerInterceptor = permissionManagementHandlerInterceptor;
    this.regulatorPermissionManagementHandlerInterceptor = regulatorPermissionManagementHandlerInterceptor;
    this.industryPermissionManagementHandlerInterceptor = industryPermissionManagementHandlerInterceptor;
    this.hasPermissionInterceptor = hasPermissionInterceptor;
    this.hasTeamPermissionInterceptor = hasTeamPermissionInterceptor;
    this.submissionHandlerInterceptor = submissionHandlerInterceptor;
    this.accessibleByRegulatorUsersInterceptor = accessibleByRegulatorUsersInterceptor;
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
    resolvers.add(new ValidationTypeArgumentResolver());
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
    registry.addInterceptor(hasPermissionInterceptor)
        .excludePathPatterns(ASSETS_PATH);
    registry.addInterceptor(accessibleByRegulatorUsersInterceptor)
        .excludePathPatterns(ASSETS_PATH);
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToSupportingDocumentTypeConverter());
    registry.addConverter(new StringToResourceSupportingDocumentTypeConverter());
  }

  @Bean
  public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
    return new ResourceUrlEncodingFilter();
  }
}