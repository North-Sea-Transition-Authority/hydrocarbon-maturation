package uk.co.nstauthority.hydrocarbonmaturation.field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@RestController
@RequestMapping("/api")
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
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
