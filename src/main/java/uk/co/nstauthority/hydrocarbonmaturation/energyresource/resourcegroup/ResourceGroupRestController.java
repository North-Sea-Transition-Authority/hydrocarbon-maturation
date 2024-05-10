package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorOrAreaUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api/resource-groups")
@AccessibleByRegulatorOrAreaUsers
public class ResourceGroupRestController {

  private final SearchSelectorService searchSelectorService;
  private final ResourceGroupQueryService resourceGroupQueryService;

  @Autowired
  public ResourceGroupRestController(SearchSelectorService searchSelectorService,
                                     ResourceGroupQueryService resourceGroupQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.resourceGroupQueryService = resourceGroupQueryService;
  }

  @GetMapping("/prospect/{resourceId}")
  public RestSearchResult searchProspects(@RequestParam("term") String searchTerm,
                                          @PathVariable("resourceId") UUID currentResourceId) {
    return searchSelectorService.search(searchTerm,
        resourceGroupQueryService.getProspectsAndGroupsByName(searchTerm, currentResourceId));
  }

  @GetMapping("/discovery/{resourceId}")
  public RestSearchResult searchDiscoveries(@RequestParam("term") String searchTerm,
                                            @PathVariable("resourceId") UUID currentResourceId) {
    return searchSelectorService.search(
        searchTerm,
        resourceGroupQueryService.getDiscoveriesAndGroupsByName(searchTerm, currentResourceId)
    );
  }
}
