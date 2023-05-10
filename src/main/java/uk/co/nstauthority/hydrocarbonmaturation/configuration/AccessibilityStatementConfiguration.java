package uk.co.nstauthority.hydrocarbonmaturation.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessibilityStatementConfiguration {
  private final AccessibilityStatementConfigurationProperties accessibilityStatementConfigurationProperties;

  @Autowired
  public AccessibilityStatementConfiguration(
      AccessibilityStatementConfigurationProperties accessibilityStatementConfigurationProperties) {
    this.accessibilityStatementConfigurationProperties = accessibilityStatementConfigurationProperties;
  }

  public AccessibilityStatementConfigurationProperties getAccessibilityStatementConfigurationProperties() {
    return accessibilityStatementConfigurationProperties;
  }
}
