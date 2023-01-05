package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class ResourceRestController {

  private final SearchSelectorService searchSelectorService;
  private final SubmissionQueryService submissionQueryService;

  @Autowired
  public ResourceRestController(SearchSelectorService searchSelectorService,
                                SubmissionQueryService submissionQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.submissionQueryService = submissionQueryService;
  }

  @GetMapping("/prospects/{submissionType}")
  public RestSearchResult searchProspects(@RequestParam("term") String searchTerm,
                                          @PathVariable("submissionType") SubmissionType submissionType) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestProspectsByNameForSubmissionType(
            searchTerm,
            submissionType)
    );
  }

  @GetMapping("/discoveries")
  public RestSearchResult searchDiscoveries(@RequestParam("term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestDiscoveriesByName(searchTerm)
    );
  }
}
