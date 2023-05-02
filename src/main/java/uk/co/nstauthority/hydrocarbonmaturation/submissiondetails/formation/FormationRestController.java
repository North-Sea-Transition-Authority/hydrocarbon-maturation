package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

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
public class FormationRestController {

  private final SearchSelectorService searchSelectorService;
  private final FormationSearchService formationSearchService;

  @Autowired
  public FormationRestController(SearchSelectorService searchSelectorService, FormationSearchService formationSearchService) {
    this.searchSelectorService = searchSelectorService;
    this.formationSearchService = formationSearchService;
  }

  @GetMapping("/formation")
  public RestSearchResult getFormationListWithManualEntry(@RequestParam(value = "term") String searchTerm) {
    return searchSelectorService.getSearchResultsWithManualEntry(
        searchTerm,
        formationSearchService::getFormationSearchableResults);
  }
}
