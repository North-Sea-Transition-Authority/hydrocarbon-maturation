package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.referenceblock.ReferenceBlockApi;
import uk.co.fivium.energyportalapi.generated.client.ReferenceBlockProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.ReferenceBlocksProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.ReferenceBlock;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@Service
public class ReferenceBlockQueryService {
  public static final ReferenceBlocksProjectionRoot REFERENCE_BLOCKS_PROJECTION_ROOT =
      new ReferenceBlocksProjectionRoot().blockRef();
  public static final ReferenceBlockProjectionRoot REFERENCE_BLOCK_PROJECTION_ROOT =
      new ReferenceBlockProjectionRoot().blockRef();
  private final ReferenceBlockApi referenceBlockApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public ReferenceBlockQueryService(ReferenceBlockApi referenceBlockApi, EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.referenceBlockApi = referenceBlockApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  List<ReferenceBlockView> getReferenceBlocksBy(String searchReference) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        referenceBlockApi.searchReferenceBlocks(
                searchReference,
                REFERENCE_BLOCKS_PROJECTION_ROOT,
                requestPurpose.purpose(),
                logCorrelationId.id()
            ).stream()
            .map(this::getReferenceBlockView)
            .toList());
  }

  public List<ReferenceBlockView> getReferenceBlocksByRefsIn(List<String> quadBlocksRefs) {
    return quadBlocksRefs.stream()
        .map(this::getReferenceBlock)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(this::getReferenceBlockView)
        .toList();
  }

  private Optional<ReferenceBlock> getReferenceBlock(String blockRef) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        referenceBlockApi.findReferenceBlockByExactBlogRef(
            blockRef,
            REFERENCE_BLOCK_PROJECTION_ROOT,
            requestPurpose.purpose(),
            logCorrelationId.id()
        )
    );
  }

  private ReferenceBlockView getReferenceBlockView(ReferenceBlock referenceBlock) {
    return new ReferenceBlockView(referenceBlock.getBlockRef());
  }
}
