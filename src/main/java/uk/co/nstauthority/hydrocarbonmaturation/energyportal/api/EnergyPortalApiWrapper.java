package uk.co.nstauthority.hydrocarbonmaturation.energyportal.api;

import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.QueryCounter;

@Component
public class EnergyPortalApiWrapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(EnergyPortalApiWrapper.class);

  static final String API_REQUEST_PREFIX = "EPA-request";

  private final ServiceConfigurationProperties serviceConfigurationProperties;
  private final QueryCounter queryCounter;

  @Autowired
  public EnergyPortalApiWrapper(ServiceConfigurationProperties serviceConfigurationProperties,
                                QueryCounter queryCounter) {
    this.serviceConfigurationProperties = serviceConfigurationProperties;
    this.queryCounter = queryCounter;
  }

  public <T> T makeRequest(BiFunction<LogCorrelationId, RequestPurpose, T> request) {
    queryCounter.incrementEpa();

    var logCorrelationId = getLogCorrelationId();
    var requestPurpose = getRequestPurpose();
    LOGGER.debug("{} ({})", logCorrelationId, requestPurpose);

    return request.apply(logCorrelationId, requestPurpose);
  }

  private RequestPurpose getRequestPurpose() {

    var callingMethod = StackWalker.getInstance()
        .walk(frames -> frames
            .skip(2) // the first frame is this method, second is the BiFunction request so skip to get the real caller
            .findFirst()
            .map(stackFrame -> "%s.%s".formatted(stackFrame.getClassName(), stackFrame.getMethodName()))
        )
        .orElseThrow(() -> new RuntimeException("Failed to find a stack frame for request purpose"));

    return new RequestPurpose("%s: %s".formatted(getServiceIdentifier(), callingMethod));
  }

  private LogCorrelationId getLogCorrelationId() {
    return new LogCorrelationId(CorrelationIdUtil.getCorrelationIdFromMdc());
  }

  private String getServiceIdentifier() {
    return serviceConfigurationProperties.mnemonic();
  }
}
