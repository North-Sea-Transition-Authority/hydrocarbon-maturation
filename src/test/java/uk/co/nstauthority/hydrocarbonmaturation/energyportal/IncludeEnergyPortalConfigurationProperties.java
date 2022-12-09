package uk.co.nstauthority.hydrocarbonmaturation.energyportal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(value = {
    EnergyPortalConfiguration.class
})
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeEnergyPortalConfigurationProperties {
}
