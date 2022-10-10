package uk.co.nstauthority.hydrocarbonmaturation.mvc.error;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "service.error")
@Validated
public record ErrorConfigurationProperties(
    @NotNull boolean stackTraceEnabled
) {}
