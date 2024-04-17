package uk.co.nstauthority.hydrocarbonmaturation.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class MetricsProvider {

  private static final String METRIC_PREFIX = "remi";

  private final Timer workAreaQueryTimer;

  private final Timer workAreaRequestTimer;

  public MetricsProvider(MeterRegistry registry) {
    this.workAreaQueryTimer = registry.timer("%s.workAreaJooqQueryTimer".formatted(METRIC_PREFIX));
    this.workAreaRequestTimer = registry.timer("%s.workAreaRequestTimer".formatted(METRIC_PREFIX));
  }

  public Timer getWorkAreaQueryTimer() {
    return workAreaQueryTimer;
  }

  public Timer getWorkAreaRequestTimer() {
    return workAreaRequestTimer;
  }
}

