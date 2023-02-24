package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;


import java.util.Comparator;
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
public class WellRestController {

  private final SearchSelectorService searchSelectorService;
  private final WellQueryService wellQueryService;

  @Autowired
  public WellRestController(SearchSelectorService searchSelectorService, WellQueryService wellQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.wellQueryService = wellQueryService;
  }

  @GetMapping("/wells")
  public RestSearchResult searchWells(@RequestParam("term") String term) {
    //todo HM-77 remove this
    var searchableList = wellQueryService.getWellsByRef(term);
    var results = searchSelectorService.search(term, searchableList)
        .getResults()
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::text))
        .toList();

    return new RestSearchResult(results);
  }

  @GetMapping("/wons-wells")
  public RestSearchResult searchWonsWells(@RequestParam("term") String term) {
    return searchSelectorService.search(
        term,
        wellQueryService::getWellsByRegistrationNumber);
  }
}
