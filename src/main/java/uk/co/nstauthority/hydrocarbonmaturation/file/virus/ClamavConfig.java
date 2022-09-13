package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "clamav")
class ClamavConfig {
  private String host;
  private int port;
  private int timeout;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public int getTimeout() {
    return timeout;
  }
}