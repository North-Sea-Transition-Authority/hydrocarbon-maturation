package uk.co.nstauthority.hydrocarbonmaturation.energyportal.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.QueryCounter;

@ExtendWith(MockitoExtension.class)
class EnergyPortalApiWrapperTest {

  @Mock
  private ServiceConfigurationProperties serviceConfigurationProperties;

  @Mock
  private QueryCounter queryCounter;

  @InjectMocks
  private EnergyPortalApiWrapper energyPortalApiWrapper;

  private UUID correlationId;

  @BeforeEach
  void setUp() {
    when(serviceConfigurationProperties.mnemonic())
        .thenReturn("mnemonic");

    correlationId = UUID.randomUUID();
    CorrelationIdUtil.setCorrelationIdOnMdc(correlationId.toString());
  }

  @Test
  void makeRequest_verifyLogCorrelationId() {
    var logCorrelationId = energyPortalApiWrapper.makeRequest(this::returnLogCorrelationId);

    assertThat(logCorrelationId).isEqualTo(correlationId.toString());
    verify(queryCounter, times(1)).incrementEpa();
  }

  @Test
  void makeRequest_verifyRequestPurpose() {
    var requestPurpose = energyPortalApiWrapper.makeRequest(this::returnRequestPurpose);

    assertThat(requestPurpose).isEqualTo("%s: %s.%s".formatted(
        serviceConfigurationProperties.mnemonic(),
        this.getClass().getName(),
        "makeRequest_verifyRequestPurpose"
    ));
  }

  private String returnLogCorrelationId(LogCorrelationId logCorrelationId, RequestPurpose requestPurpose) {
    return logCorrelationId.id();
  }

  private String returnRequestPurpose(LogCorrelationId logCorrelationId, RequestPurpose requestPurpose) {
    return requestPurpose.purpose();
  }
}
