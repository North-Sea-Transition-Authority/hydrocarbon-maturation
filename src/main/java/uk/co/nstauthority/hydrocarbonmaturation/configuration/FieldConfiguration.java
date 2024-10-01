package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "remi.fields")
@Validated
public record FieldConfiguration(
    @NotNull boolean allowSubmissions
) { }
