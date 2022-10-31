package uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api")
public class ReferenceBlockRestController {

  private final SearchSelectorService searchSelectorService;
  private final ReferenceBlockSearchService referenceBlockSearchService;

  @Autowired
  public ReferenceBlockRestController(SearchSelectorService searchSelectorService,
                                      ReferenceBlockSearchService referenceBlockSearchService) {
    this.searchSelectorService = searchSelectorService;
    this.referenceBlockSearchService = referenceBlockSearchService;
  }

  @GetMapping("/quadrant-blocks")
  public RestSearchResult searchQuadrantBlocks(@RequestParam("term") String searchReference) {
    var results = searchSelectorService.search(searchReference, referenceBlockSearchService::getReferenceBlocksBy);

    if (!Objects.isNull(results)) {
      var sortedResults = results.getResults().stream()
          .sorted(Comparator.comparing(RestSearchItem::text))
          .toList();
      return new RestSearchResult(sortedResults);
    }
    return new RestSearchResult(Collections.emptyList());
  }
}
