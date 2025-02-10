package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.referenceblock.fullreferenceblock.FullReferenceBlockApi;
import uk.co.fivium.energyportalapi.generated.client.FullReferenceBlocksProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.FullReferenceBlock;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
public class ReferenceBlockQueryService {
  public static final FullReferenceBlocksProjectionRoot REFERENCE_BLOCKS_PROJECTION_ROOT =
      new FullReferenceBlocksProjectionRoot()
          .blockRef()
          .quadrantNo()
          .blockNo()
          .shoreType().root();

  private final FullReferenceBlockApi fullReferenceBlockApi;

  @Autowired
  public ReferenceBlockQueryService(FullReferenceBlockApi fullReferenceBlockApi) {
    this.fullReferenceBlockApi = fullReferenceBlockApi;
  }

  List<ReferenceBlockView> getReferenceBlocksContaining(String searchReference, OnshoreOffshore onshoreOffshore) {
    return getReferenceBlocksContaining(searchReference).stream()
        .filter(referenceBlockView -> referenceBlockView.onshoreOffshore() == onshoreOffshore)
        .toList();
  }

  public List<ReferenceBlockView> getReferenceBlocksContaining(String searchReference) {
    return fullReferenceBlockApi.searchFullReferenceBlocksByReferenceContains(
        searchReference,
            REFERENCE_BLOCKS_PROJECTION_ROOT,
            new RequestPurpose("Search full reference blocks"),
            CorrelationIdUtil.getLogCorrelationId()
        ).stream()
        .map(this::getReferenceBlockView)
        .toList();
  }

  public List<ReferenceBlockView> getReferenceBlocksByRefsIn(List<String> quadBlocksRefs) {
    return fullReferenceBlockApi.searchFullReferenceBlocksByReferences(
        quadBlocksRefs,
            REFERENCE_BLOCKS_PROJECTION_ROOT,
            new RequestPurpose("Search full reference blocks by references"),
            CorrelationIdUtil.getLogCorrelationId()
        ).stream()
        .map(this::getReferenceBlockView)
        .toList();
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
