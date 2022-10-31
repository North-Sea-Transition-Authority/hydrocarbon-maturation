package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController("/api")
public class LicenceApplicationRestController {

  private final LicenceApplicationQueryService licenceApplicationQueryService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public LicenceApplicationRestController(LicenceApplicationQueryService licenceApplicationQueryService,
                                          SearchSelectorService searchSelectorService) {
    this.licenceApplicationQueryService = licenceApplicationQueryService;
    this.searchSelectorService = searchSelectorService;
  }

  @GetMapping("/licence-applications")
  public RestSearchResult searchLicenceApplicationsWithManualEntry(@RequestParam(value = "term") String term) {
    return searchSelectorService.getSearchResultsWithManualEntry(
        term,
        licenceApplicationQueryService::getLicenceApplicationSearchableResults);
  }
}
