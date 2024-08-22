package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class OrganisationUnitRestController {
  public static final String REQUEST_PURPOSE = "Searching for responsible organisations for the resource";

  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public OrganisationUnitRestController(OrganisationUnitQueryService organisationUnitQueryService,
                                        SearchSelectorService searchSelectorService) {
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.searchSelectorService = searchSelectorService;
  }

  @GetMapping("/organisation-units")
  public RestSearchResult searchOrganisationUnits(@RequestParam(value = "term") String term, User user) {
    return searchSelectorService.search(
        term,
        organisationUnitQueryService.getOrganisationSearchableResults(term, user)
    );
  }
}
