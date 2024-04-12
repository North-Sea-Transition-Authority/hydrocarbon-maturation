package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties(AnalyticsConfigurationProperties.class)
@Import(AnalyticsConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeAnalyticsConfigurationProperties {
}