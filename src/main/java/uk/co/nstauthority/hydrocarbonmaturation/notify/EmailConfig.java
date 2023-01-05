package uk.co.nstauthority.hydrocarbonmaturation.notify;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email")
public record EmailConfig(String mode, String testRecipientList, String callbackEmail) { }