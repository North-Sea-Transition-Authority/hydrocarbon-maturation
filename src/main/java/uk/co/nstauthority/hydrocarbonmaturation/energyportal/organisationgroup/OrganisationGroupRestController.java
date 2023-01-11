package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class OrganisationGroupRestController {

  public static final String REQUEST_PURPOSE = "Searching organisation groups";
  private final OrganisationGroupQueryService organisationGroupService;

  public OrganisationGroupRestController(OrganisationGroupQueryService organisationGroupService) {
    this.organisationGroupService = organisationGroupService;
  }

  @GetMapping("/organisation-group")
  public RestSearchResult getOrganisationGroupSearchResults(@RequestParam(value = "term", required = false) String term) {
    var queryResults = organisationGroupService
        .getOrganisationGroupsByName(term, REQUEST_PURPOSE);

    return organisationGroupService.organisationGroupsToSearchResult(queryResults);
  }
}
