package uk.co.nstauthority.hydrocarbonmaturation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EntityScan("uk.co.fivium, uk.co.nstauthority.hydrocarbonmaturation")
public class HydrocarbonMaturationApplication {

  public static void main(String[] args) {
    SpringApplication.run(HydrocarbonMaturationApplication.class, args);
  }

}
