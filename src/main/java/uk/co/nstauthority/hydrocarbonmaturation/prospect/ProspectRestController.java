package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;

@RestController
@RequestMapping("/api")
public class ProspectRestController {

  private final SearchSelectorService searchSelectorService;
  private final UpdateProspectService updateProspectService;

  @Autowired
  public ProspectRestController(SearchSelectorService searchSelectorService,
                                UpdateProspectService updateProspectService) {
    this.searchSelectorService = searchSelectorService;
    this.updateProspectService = updateProspectService;
  }

  @GetMapping("/prospects")
  public RestSearchResult searchProspects(@RequestParam("term") String searchTerm) {
    List<GeneralDetail> searchableList = updateProspectService.getLatestProspectByName(searchTerm);
    var results = searchSelectorService.search(searchTerm, searchableList)
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::text))
        .toList();

    return new RestSearchResult(results);
  }
}
