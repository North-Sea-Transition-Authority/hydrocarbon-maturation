package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@Service
public class ReservoirSubUnitSearchService {

  private final SearchSelectorService searchSelectorService;

  @Autowired
  public ReservoirSubUnitSearchService(SearchSelectorService searchSelectorService) {
    this.searchSelectorService = searchSelectorService;
  }

  List<ReservoirSubUnitDto> getSubUnitSearchableResults(String searchTerm) {
    return ReservoirSubUnit.getSubUnits().entrySet().stream()
        .filter(e -> e.getValue().toLowerCase().contains(searchTerm.toLowerCase()))
        .map(e -> new ReservoirSubUnitDto(e.getKey(), e.getValue()))
        .toList();
  }

  public Map<String, String> getPreSelectedSubUnit(String subUnit) {
    if (subUnit != null) {
      if (SearchSelectorService.isManualEntry(subUnit)) {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(subUnit),
            Map.of(subUnit, subUnit)
        );
      } else {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(subUnit),
            Map.of(
                EnumUtils.getEnumValue(ReservoirSubUnit.class, subUnit).name(),
                EnumUtils.getEnumValue(ReservoirSubUnit.class, subUnit).getDisplayName()
            ));
      }
    }
    return Map.of();
  }
}
