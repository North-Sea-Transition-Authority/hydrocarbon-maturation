package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class ReservoirAgeRestController {

  private final SearchSelectorService searchSelectorService;
  private final ReservoirAgeSearchService reservoirAgeSearchService;

  @Autowired
  public ReservoirAgeRestController(SearchSelectorService searchSelectorService,
                                    ReservoirAgeSearchService reservoirAgeSearchService) {
    this.searchSelectorService = searchSelectorService;
    this.reservoirAgeSearchService = reservoirAgeSearchService;
  }

  @GetMapping("/reservoir-age")
  public RestSearchResult getReservoirAgeList(@RequestParam(value = "term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        reservoirAgeSearchService::getAgeSeriesSearchableResults);
  }
}
