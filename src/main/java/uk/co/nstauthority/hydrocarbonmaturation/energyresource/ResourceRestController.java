package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchResourceRecordsQueryService;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class ResourceRestController {

  private final SearchSelectorService searchSelectorService;
  private final SubmissionQueryService submissionQueryService;
  private final FieldQueryService fieldQueryService;
  private final ReservoirQueryService reservoirQueryService;
  private final SearchResourceRecordsQueryService searchResourceRecordsQueryService;

  @Autowired
  public ResourceRestController(SearchSelectorService searchSelectorService,
                                SubmissionQueryService submissionQueryService,
                                FieldQueryService fieldQueryService,
                                ReservoirQueryService reservoirQueryService,
                                SearchResourceRecordsQueryService searchResourceRecordsQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.submissionQueryService = submissionQueryService;
    this.fieldQueryService = fieldQueryService;
    this.reservoirQueryService = reservoirQueryService;
    this.searchResourceRecordsQueryService = searchResourceRecordsQueryService;
  }

  @GetMapping("/prospects/{submissionType}")
  public RestSearchResult searchProspects(@RequestParam("term") String searchTerm,
                                          @PathVariable("submissionType") SubmissionType submissionType,
                                          User user) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestProspectsByNameForSubmissionType(
            searchTerm,
            submissionType,
            user)
    );
  }

  @GetMapping("/discoveries/{submissionType}")
  public RestSearchResult searchDiscoveries(@RequestParam("term") String searchTerm,
                                            @PathVariable("submissionType") SubmissionType submissionType,
                                            User user) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(
            searchTerm,
            user)
    );
  }

  @GetMapping("/discoveries/field-discoveries")
  public RestSearchResult searchDiscoveriesForFields(@RequestParam("term") String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        submissionQueryService.getLatestActiveViewDiscoveriesByName(
            searchTerm)
    );
  }

  @GetMapping("/resources/prospects-and-discoveries")
  public RestSearchResult searchProspectsAndDiscoveries(@RequestParam("term") String searchTerm, User user) {
    return searchResourceRecordsQueryService.searchProspectsAndDiscoveries(searchTerm, user);
  }

  @GetMapping("/resources/fields")
  public RestSearchResult searchFieldsByFieldName(@RequestParam("term") String searchTerm, User user) {
    return searchResourceRecordsQueryService.searchFields(searchTerm, user);
  }

  @GetMapping("field/{submissionType}")
  public RestSearchResult searchFields(@RequestParam("term") String searchTerm,
                                       @PathVariable("submissionType") SubmissionType submissionType,
                                       User user) {
    return searchSelectorService.search(
        searchTerm,
        fieldQueryService.getFieldByNameForSubmissionType(
            searchTerm,
            submissionType,
            user)
    );
  }

  @GetMapping("field/{submissionDetailId}/discovery-reservoirs")
  public RestSearchResult searchDiscoveryReservoirs(@RequestParam("term") String searchTerm,
                                                    @PathVariable String submissionDetailId) {
    return searchSelectorService.search(
        searchTerm,
        reservoirQueryService.getReservoirByNameForSubmissionDetail(searchTerm, submissionDetailId)
    );
  }

  @GetMapping("field/shelved-or-cop")
  public RestSearchResult searchShelvedAndCopFields(@RequestParam("term") String searchTerm, User user) {
    return searchSelectorService.search(
        searchTerm,
        fieldQueryService.getShelvedOrCopFieldByName(searchTerm, user)
    );
  }
}
