package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "accessibility")
@Validated
public record AccessibilityStatementConfigurationProperties(
    @NotNull String statementPreparedDate,
    @NotNull String statementLastReviewDate,
    @NotNull String serviceLastTestDate,
    @NotNull String serviceLastTestedBy,
    @NotNull String designSystemLastTestedOnDate) {
}
