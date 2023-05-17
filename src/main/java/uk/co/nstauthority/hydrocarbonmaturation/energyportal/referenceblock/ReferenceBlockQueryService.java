package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.referenceblock.fullreferenceblock.FullReferenceBlockApi;
import uk.co.fivium.energyportalapi.generated.client.FullReferenceBlockProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.FullReferenceBlocksProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.FullReferenceBlock;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
public class ReferenceBlockQueryService {
  public static final FullReferenceBlockProjectionRoot REFERENCE_BLOCK_PROJECTION_ROOT =
      new FullReferenceBlockProjectionRoot()
          .blockRef()
          .quadrantNo()
          .blockNo()
          .shoreType().root();

  public static final FullReferenceBlocksProjectionRoot REFERENCE_BLOCKS_PROJECTION_ROOT =
      new FullReferenceBlocksProjectionRoot()
          .blockRef()
          .quadrantNo()
          .blockNo()
          .shoreType().root();

  private final FullReferenceBlockApi fullReferenceBlockApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public ReferenceBlockQueryService(FullReferenceBlockApi fullReferenceBlockApi, EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.fullReferenceBlockApi = fullReferenceBlockApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  List<ReferenceBlockView> getReferenceBlocksContaining(String searchReference) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        fullReferenceBlockApi.searchFullReferenceBlocksByReferenceContains(
                searchReference,
                REFERENCE_BLOCKS_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId
            ).stream()
            .map(this::getReferenceBlockView)
            .toList());
  }

  public List<ReferenceBlockView> getReferenceBlocksByRefsIn(List<String> quadBlocksRefs) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        fullReferenceBlockApi.searchFullReferenceBlocksByReferences(
            quadBlocksRefs,
            REFERENCE_BLOCKS_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId
        ).stream()
            .map(this::getReferenceBlockView)
            .toList());
  }

  public Optional<FullReferenceBlock> getReferenceBlock(String blockRef) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        fullReferenceBlockApi.findFullReferenceBlockByReference(
            blockRef,
            REFERENCE_BLOCK_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId
        )
    );
  }

  private ReferenceBlockView getReferenceBlockView(FullReferenceBlock fullReferenceBlock) {
    return new ReferenceBlockView(
        fullReferenceBlock.getBlockRef(),
        fullReferenceBlock.getQuadrantNo(),
        fullReferenceBlock.getBlockNo(),
        OnshoreOffshore.fromShoreType(fullReferenceBlock.getShoreType())
    );
  }
}
