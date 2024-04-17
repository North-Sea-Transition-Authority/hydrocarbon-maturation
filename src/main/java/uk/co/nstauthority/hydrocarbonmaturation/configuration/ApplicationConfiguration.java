package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import static net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider.Configuration.builder;

import io.micrometer.core.instrument.MeterRegistry;
import java.time.Clock;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.MetricsProvider;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ApplicationConfiguration {

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  public LockProvider lockProvider(JdbcTemplate jdbcTemplate, DatabaseConfigurationProperties databaseConfigurationProperties) {
    return new JdbcTemplateLockProvider(builder()
        .withTableName("%s.shedlock".formatted(databaseConfigurationProperties.schema()))
        .withJdbcTemplate(jdbcTemplate)
        .usingDbTime()
        .build()
    );
  }

  @Bean
  public MetricsProvider metricsProvider(MeterRegistry registry) {
    return new MetricsProvider(registry);
  }
}
