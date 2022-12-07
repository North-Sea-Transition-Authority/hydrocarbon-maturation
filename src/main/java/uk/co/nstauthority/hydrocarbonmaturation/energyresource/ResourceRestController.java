package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api")
public class ResourceRestController {

  private final SearchSelectorService searchSelectorService;
  private final SubmissionQueryService submissionQueryService;

  @Autowired
  public ResourceRestController(SearchSelectorService searchSelectorService,
                                SubmissionQueryService submissionQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.submissionQueryService = submissionQueryService;
  }

  @GetMapping("/prospects")
  public RestSearchResult searchProspects(@RequestParam("term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestSubmissionByNameAndResourceType(searchTerm, ResourceType.PROSPECT)
    );
  }

  @GetMapping("/discoveries")
  public RestSearchResult searchDiscoveries(@RequestParam("term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestSubmissionByNameAndResourceType(searchTerm, ResourceType.DISCOVERY)
    );
  }
}
