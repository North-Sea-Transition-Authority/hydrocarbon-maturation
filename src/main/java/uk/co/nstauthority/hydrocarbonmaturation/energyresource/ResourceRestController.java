package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class ResourceRestController {

  private final SearchSelectorService searchSelectorService;
  private final SubmissionQueryService submissionQueryService;
  private final FieldQueryService fieldQueryService;

  @Autowired
  public ResourceRestController(SearchSelectorService searchSelectorService,
                                SubmissionQueryService submissionQueryService,
                                FieldQueryService fieldQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.submissionQueryService = submissionQueryService;
    this.fieldQueryService = fieldQueryService;
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

  @GetMapping("/discoveries/{submissionType}")
  public RestSearchResult searchDiscoveries(@RequestParam("term") String searchTerm,
                                            @PathVariable("submissionType") SubmissionType submissionType) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(
            searchTerm,
            submissionType)
    );
  }

  @GetMapping("/discoveries/field-discoveries")
  public RestSearchResult searchDiscoveriesNotInGroupOrPreferredView(@RequestParam("term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestNotRelinquishedAndNotInGroupOrPreferredViewDiscoveriesByName(
            searchTerm)
    );
  }

  @GetMapping("/resources")
  public RestSearchResult searchResources(@RequestParam("term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService::getLatestResourceSubmissionsByName
    );
  }

  @GetMapping("field/{submissionType}")
  public RestSearchResult searchFields(@RequestParam("term") String searchTerm,
                                       @PathVariable("submissionType") SubmissionType submissionType,
                                       ServiceUserDetail user) {
    return searchSelectorService.search(
        searchTerm,
        fieldQueryService.getFieldByNameForSubmissionType(
            searchTerm,
            submissionType,
            user)
    );
  }
}
