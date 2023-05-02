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
public class ReservoirSubUnitRestController {

  private final SearchSelectorService searchSelectorService;
  private final ReservoirSubUnitSearchService reservoirSubUnitSearchService;

  @Autowired
  public ReservoirSubUnitRestController(SearchSelectorService searchSelectorService,
                                        ReservoirSubUnitSearchService reservoirSubUnitSearchService) {
    this.searchSelectorService = searchSelectorService;
    this.reservoirSubUnitSearchService = reservoirSubUnitSearchService;
  }

  @GetMapping("/reservoir-sub-unit")
  public RestSearchResult getSubUnitListWithManualEntry(@RequestParam(value = "term") String searchTerm) {
    return searchSelectorService.getSearchResultsWithManualEntry(
        searchTerm,
        reservoirSubUnitSearchService::getSubUnitSearchableResults);
  }
}
