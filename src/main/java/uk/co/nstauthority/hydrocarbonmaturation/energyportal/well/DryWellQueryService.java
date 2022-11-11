package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;


import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DryWellQueryService {

  // TODO HM-15 use wons api to dynamically get wells
  public static final List<String> DUMMY_LIST_OF_WELLS = List.of("ND27a", "30/11", "29/10", "28/12", "25/10", "25/15");

  List<ReferenceWellView> getWellsByRef(String searchReference) {
    return DUMMY_LIST_OF_WELLS.stream()
        .filter(well -> {
          var quadBlockRef = well.toLowerCase();
          var reference = searchReference.trim().toLowerCase();
          return quadBlockRef.contains(reference);
        })
        .map(ReferenceWellView::fromWellReference)
        .toList();
  }

  public List<ReferenceWellView> getDryWellByName(String dryWell) {
    return DUMMY_LIST_OF_WELLS
        .stream()
        .filter(dryWell::contains)
        .map(ReferenceWellView::fromWellReference)
        .toList();
  }
}
