package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "fms")
@Validated
public record FeedbackConfigurationProperties(
    @NotNull String urlBase,
    @NotNull Long connectionTimeout,
    @NotNull String saveFeedbackUrl,
    @NotNull String serviceName,
    @NotNull String presharedKey
) {}
