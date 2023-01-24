package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api/prospect-groups")
@AccessibleByServiceUsers
public class ResourceGroupRestController {

  private final SearchSelectorService searchSelectorService;
  private final ResourceGroupQueryService resourceGroupQueryService;

  @Autowired
  public ResourceGroupRestController(SearchSelectorService searchSelectorService,
                                     ResourceGroupQueryService resourceGroupQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.resourceGroupQueryService = resourceGroupQueryService;
  }

  @GetMapping
  public RestSearchResult searchProspects(@RequestParam("term") String searchTerm) {
    return searchSelectorService.search(searchTerm, resourceGroupQueryService::getProspectsAndGroupsByName);
  }
}
