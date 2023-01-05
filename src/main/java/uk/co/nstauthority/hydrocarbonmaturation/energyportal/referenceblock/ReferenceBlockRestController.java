package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class ReferenceBlockRestController {

  private final SearchSelectorService searchSelectorService;
  private final ReferenceBlockQueryService referenceBlockQueryService;

  @Autowired
  public ReferenceBlockRestController(SearchSelectorService searchSelectorService,
                                      ReferenceBlockQueryService referenceBlockQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.referenceBlockQueryService = referenceBlockQueryService;
  }

  @GetMapping("/quadrant-blocks")
  public RestSearchResult searchQuadrantBlocks(@RequestParam("term") String searchReference) {
    var results = searchSelectorService.search(searchReference, referenceBlockQueryService::getReferenceBlocksBy);

    if (!Objects.isNull(results)) {
      var sortedResults = results.getResults().stream()
          .sorted(Comparator.comparing(RestSearchItem::text))
          .toList();
      return new RestSearchResult(sortedResults);
    }
    return new RestSearchResult(Collections.emptyList());
  }
}
