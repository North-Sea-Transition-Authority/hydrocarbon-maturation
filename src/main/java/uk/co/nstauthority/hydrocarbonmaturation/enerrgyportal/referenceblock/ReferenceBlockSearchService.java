package uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReferenceBlockSearchService {
  // TODO HM-54 use energy portal api to dynamically get quadrant blocks
  public static final List<String> DUMMY_LIST_OF_QUAD_BLOCKS = List.of("ND27a", "30/11", "29/10", "28/12", "25/10", "25/15");

  List<ReferenceBlockView> getReferenceBlocksBy(String searchReference) {
    return DUMMY_LIST_OF_QUAD_BLOCKS.stream()
        .filter(quadBlock -> {
          var quadBlockRef = quadBlock.toLowerCase();
          var reference = searchReference.trim().toLowerCase();
          return quadBlockRef.contains(reference);
        })
        .map(this::getReferenceBlockView)
        .toList();
  }

  public List<ReferenceBlockView> getReferenceBlocksByRefsIn(List<String> quadBlocksRefs) {
    return DUMMY_LIST_OF_QUAD_BLOCKS.stream()
        .filter(quadBlocksRefs::contains)
        .map(this::getReferenceBlockView)
        .toList();
  }

  private ReferenceBlockView getReferenceBlockView(String quadBlockRef) {
    return new ReferenceBlockView(quadBlockRef, quadBlockRef);
  }
}
