package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;


import java.util.Comparator;
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
public class DryWellRestController {

  private final SearchSelectorService searchSelectorService;
  private final DryWellQueryService dryWellQueryService;

  @Autowired
  public DryWellRestController(SearchSelectorService searchSelectorService, DryWellQueryService dryWellQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.dryWellQueryService = dryWellQueryService;
  }

  @GetMapping("/wells")
  public RestSearchResult searchDryWells(@RequestParam("term") String term) {
    var searchableList = dryWellQueryService.getWellsByRef(term);
    var results = searchSelectorService.search(term, searchableList)
        .getResults()
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::text))
        .toList();

    return new RestSearchResult(results);
  }
}
