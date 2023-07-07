package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class LicenceRestController {

  private final SearchSelectorService searchSelectorService;
  private final LicenceQueryService licenceQueryService;

  @Autowired
  public LicenceRestController(SearchSelectorService searchSelectorService, LicenceQueryService licenceQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.licenceQueryService = licenceQueryService;
  }

  @GetMapping("/licence")
  public RestSearchResult searchLiveLicences(@RequestParam("term") String searchReference) {
    var licences = licenceQueryService.getLiveLicencesBy(searchReference);
    return searchSelectorService.search(
        searchReference,
        licences
    );
  }

  @GetMapping("/licence/extant")
  public RestSearchResult searchExtantLicences(@RequestParam("term") String searchReference) {
    var licences = licenceQueryService.getLicencesByReferenceAndStatus(searchReference, LicenceStatus.EXTANT);
    return searchSelectorService.search(
        searchReference,
        licences);
  }
}
