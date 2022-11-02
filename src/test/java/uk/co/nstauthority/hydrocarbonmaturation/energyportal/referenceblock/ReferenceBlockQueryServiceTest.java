package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.referenceblock.ReferenceBlockApi;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@ExtendWith(MockitoExtension.class)
class ReferenceBlockQueryServiceTest {
  @Mock
  private ReferenceBlockApi referenceBlockApi;
  private final ServiceConfigurationProperties serviceConfigurationProperties = new ServiceConfigurationProperties(
      "name",
      "mnemonic"
  );

  private final EnergyPortalApiWrapper energyPortalApiWrapper = new EnergyPortalApiWrapper(
      serviceConfigurationProperties
  );

  private ReferenceBlockQueryService referenceBlockQueryService;

  @BeforeEach
  void setUp() {
    referenceBlockQueryService = new ReferenceBlockQueryService(
        referenceBlockApi,
        energyPortalApiWrapper
    );
  }

  @Test
  void getReferenceBlocksBy() {
    var searchReference = "25/";

    referenceBlockQueryService.getReferenceBlocksBy(searchReference);

    verify(referenceBlockApi, times(1)).searchReferenceBlocks(
        eq(searchReference),
        eq(ReferenceBlockQueryService.REFERENCE_BLOCKS_PROJECTION_ROOT),
        any(),
        any()
    );
  }

  @Test
  void getReferenceBlocksByRefsIn() {
    var blockRefs = List.of("25/10", "25/15", "25/12");

    referenceBlockQueryService.getReferenceBlocksByRefsIn(blockRefs);

    verify(referenceBlockApi, times(3)).findReferenceBlockByExactBlogRef(
        anyString(),
        eq(ReferenceBlockQueryService.REFERENCE_BLOCK_PROJECTION_ROOT),
        any(),
        any()
    );
  }
}