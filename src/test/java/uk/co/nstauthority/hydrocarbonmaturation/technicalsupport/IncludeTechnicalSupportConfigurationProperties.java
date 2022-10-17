package uk.co.nstauthority.hydrocarbonmaturation.technicalsupport;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties(TechnicalSupportConfigurationProperties.class)
@Import(TechnicalSupportConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeTechnicalSupportConfigurationProperties {
}
