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
public class AgeSeriesRestController {

  private final SearchSelectorService searchSelectorService;
  private final AgeSeriesSearchService ageSeriesSearchService;

  @Autowired
  public AgeSeriesRestController(SearchSelectorService searchSelectorService, AgeSeriesSearchService ageSeriesSearchService) {
    this.searchSelectorService = searchSelectorService;
    this.ageSeriesSearchService = ageSeriesSearchService;
  }

  @GetMapping("/age-series")
  public RestSearchResult getAgeSeriesList(@RequestParam(value = "term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        ageSeriesSearchService::getAgeSeriesSearchableResults);
  }
}
