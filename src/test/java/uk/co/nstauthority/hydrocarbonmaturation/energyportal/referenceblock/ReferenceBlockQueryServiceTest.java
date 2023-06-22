package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.referenceblock.fullreferenceblock.FullReferenceBlockApi;
import uk.co.fivium.energyportalapi.generated.types.FullReferenceBlock;
import uk.co.fivium.energyportalapi.generated.types.ShoreType;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

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
  void getReferenceBlocksContaining_filtersOnshore() {
    var searchReference = "25/";

    var offshoreFullReferenceBlock = new FullReferenceBlock("blockRef1", "1", 1, ShoreType.OFFSHORE);
    var onshoreFullReferenceBlock = new FullReferenceBlock("blockRef2", "2", 2, ShoreType.ONSHORE);

    when(referenceBlockApi.searchFullReferenceBlocksByReferenceContains(
        eq(searchReference),
        eq(ReferenceBlockQueryService.REFERENCE_BLOCKS_PROJECTION_ROOT),
        any(),
        any())
    ).thenReturn(List.of(offshoreFullReferenceBlock, onshoreFullReferenceBlock));

    assertThat(referenceBlockQueryService.getReferenceBlocksContaining(searchReference, OnshoreOffshore.ONSHORE))
        .extracting(
            ReferenceBlockView::quadAndBlockRef,
            ReferenceBlockView::quadrant,
            ReferenceBlockView::blockNo,
            ReferenceBlockView::onshoreOffshore
        ).containsExactly(
            Tuple.tuple(
                onshoreFullReferenceBlock.getBlockRef(),
                onshoreFullReferenceBlock.getQuadrantNo(),
                onshoreFullReferenceBlock.getBlockNo(),
                OnshoreOffshore.valueOf(onshoreFullReferenceBlock.getShoreType().name())
            )
        );
  }

  @Test
  void getReferenceBlocksContaining_filtersOffshore() {
    var searchReference = "25/";

    var offshoreFullReferenceBlock = new FullReferenceBlock("blockRef1", "1", 1, ShoreType.OFFSHORE);
    var onshoreFullReferenceBlock = new FullReferenceBlock("blockRef2", "2", 2, ShoreType.ONSHORE);

    when(referenceBlockApi.searchFullReferenceBlocksByReferenceContains(
        eq(searchReference),
        eq(ReferenceBlockQueryService.REFERENCE_BLOCKS_PROJECTION_ROOT),
        any(),
        any())
    ).thenReturn(List.of(offshoreFullReferenceBlock, onshoreFullReferenceBlock));

    assertThat(referenceBlockQueryService.getReferenceBlocksContaining(searchReference, OnshoreOffshore.OFFSHORE))
        .extracting(
            ReferenceBlockView::quadAndBlockRef,
            ReferenceBlockView::quadrant,
            ReferenceBlockView::blockNo,
            ReferenceBlockView::onshoreOffshore
        ).containsExactly(
            Tuple.tuple(
                offshoreFullReferenceBlock.getBlockRef(),
                offshoreFullReferenceBlock.getQuadrantNo(),
                offshoreFullReferenceBlock.getBlockNo(),
                OnshoreOffshore.valueOf(offshoreFullReferenceBlock.getShoreType().name())
            )
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
}