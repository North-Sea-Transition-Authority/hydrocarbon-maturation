package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
public class ProspectRestController {

  private final SearchSelectorService searchSelectorService;
  private final ProspectQueryService prospectQueryService;

  @Autowired
  public ProspectRestController(SearchSelectorService searchSelectorService,
                                ProspectQueryService prospectQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.prospectQueryService = prospectQueryService;
  }

  @GetMapping("/prospects")
  public RestSearchResult searchProspects(@RequestParam("term") String searchTerm) {
    var searchableList = prospectQueryService.getLatestProspectSubmissionByName(searchTerm);
    var results = searchSelectorService.search(searchTerm, searchableList).getResults().stream()
        .sorted(Comparator.comparing(RestSearchItem::text))
        .toList();

    return new RestSearchResult(results);
  }
}
