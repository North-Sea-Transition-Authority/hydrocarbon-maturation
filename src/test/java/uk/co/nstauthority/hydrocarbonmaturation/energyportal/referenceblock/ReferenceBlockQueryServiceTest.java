package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.referenceblock.fullreferenceblock.FullReferenceBlockApi;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@ExtendWith(MockitoExtension.class)
class ReferenceBlockQueryServiceTest {
  @Mock
  private FullReferenceBlockApi referenceBlockApi;
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
  void getReferenceBlocksContaining() {
    var searchReference = "25/";

    referenceBlockQueryService.getReferenceBlocksContaining(searchReference);

    verify(referenceBlockApi, times(1)).searchFullReferenceBlocksByReferenceContains(
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

    verify(referenceBlockApi, times(1)).searchFullReferenceBlocksByReferences(
        eq(blockRefs),
        eq(ReferenceBlockQueryService.REFERENCE_BLOCKS_PROJECTION_ROOT),
        any(),
        any()
    );
  }

  @Test
  void getReferenceBlock() {
    var referenceBlock = "25/10";

    referenceBlockQueryService.getReferenceBlock(referenceBlock);

    verify(referenceBlockApi, times(1)).findFullReferenceBlockByReference(
        eq(referenceBlock),
        eq(ReferenceBlockQueryService.REFERENCE_BLOCK_PROJECTION_ROOT),
        any(),
        any()
    );
  }
}