package uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class ReferenceBlockRestController {

  private final SearchSelectorService searchSelectorService;
  private final ReferenceBlockQueryService referenceBlockQueryService;

  @Autowired
  public ReferenceBlockRestController(SearchSelectorService searchSelectorService,
                                      ReferenceBlockQueryService referenceBlockQueryService) {
    this.searchSelectorService = searchSelectorService;
    this.referenceBlockQueryService = referenceBlockQueryService;
  }

  @GetMapping("/quadrant-blocks")
  public RestSearchResult searchQuadrantBlocks(@RequestParam("term") String searchReference) {
    var referenceBlockViews = referenceBlockQueryService.getReferenceBlocksContaining(searchReference);
    return getRestSearchResult(searchReference, referenceBlockViews);
  }

  @GetMapping("/quadrant-blocks/onshore")
  public RestSearchResult searchOnshoreQuadrantBlocks(@RequestParam("term") String searchReference) {
    var referenceBlockViews = referenceBlockQueryService.getReferenceBlocksContaining(searchReference, OnshoreOffshore.ONSHORE);
    return getRestSearchResult(searchReference, referenceBlockViews);
  }

  @GetMapping("/quadrant-blocks/offshore")
  public RestSearchResult searchOffshoreQuadrantBlocks(@RequestParam("term") String searchReference) {
    var referenceBlockViews = referenceBlockQueryService.getReferenceBlocksContaining(searchReference, OnshoreOffshore.OFFSHORE);
    return getRestSearchResult(searchReference, referenceBlockViews);
  }

  private RestSearchResult getRestSearchResult(String searchReference, List<ReferenceBlockView> referenceBlockViews) {
    return searchSelectorService.search(
        searchReference,
        referenceBlockViews
    );
  }
}
