package uk.co.nstauthority.hydrocarbonmaturation.field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@RestController
@RequestMapping("/api")
@HasPermission(permissions = {
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class DeterminedFieldRestController {
  private final SearchSelectorService searchSelectorService;
  private final NewFieldService newFieldService;

  @Autowired
  public DeterminedFieldRestController(SearchSelectorService searchSelectorService, NewFieldService newFieldService) {
    this.searchSelectorService = searchSelectorService;
    this.newFieldService = newFieldService;
  }

  @GetMapping("/determined-fields")
  public RestSearchResult searchDeterminedFields(@RequestParam(value = "term") String term) {
    return searchSelectorService.search(
        term,
        newFieldService::getDeterminedFields
    );
  }
}
