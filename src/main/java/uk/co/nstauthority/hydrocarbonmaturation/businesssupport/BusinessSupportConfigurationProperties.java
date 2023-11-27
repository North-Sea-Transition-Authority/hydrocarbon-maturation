package uk.co.nstauthority.hydrocarbonmaturation.businesssupport;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "business-support.contact")
@Validated
public record BusinessSupportConfigurationProperties(
    @NotNull String name,
    @NotNull String emailAddress
) { }
