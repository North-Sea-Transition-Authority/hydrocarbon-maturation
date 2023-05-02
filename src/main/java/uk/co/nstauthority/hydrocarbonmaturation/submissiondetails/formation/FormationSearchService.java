package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@Service
public class FormationSearchService {

  private final SearchSelectorService searchSelectorService;

  @Autowired
  public FormationSearchService(SearchSelectorService searchSelectorService) {
    this.searchSelectorService = searchSelectorService;
  }

  List<FormationDto> getFormationSearchableResults(String searchTerm) {
    return Formation.getFormationNames().entrySet().stream()
        .filter(e -> e.getValue().toLowerCase().contains(searchTerm.toLowerCase()))
        .map(e -> new FormationDto(e.getKey(), e.getValue()))
        .toList();
  }

  public Map<String, String> getPreSelectedFormation(String formation) {
    if (formation != null) {
      if (SearchSelectorService.isManualEntry(formation)) {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(formation),
            Map.of(formation, formation)
        );
      } else {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(formation),
            Map.of(
                EnumUtils.getEnumValue(Formation.class, formation).name(),
                EnumUtils.getEnumValue(Formation.class, formation).getDisplayName()
            ));
      }
    }
    return Map.of();
  }
}
