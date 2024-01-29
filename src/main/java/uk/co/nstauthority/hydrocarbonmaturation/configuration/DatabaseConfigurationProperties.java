package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "database")
@Validated
public record DatabaseConfigurationProperties(
    @NotNull String schema
) {
}
