package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@Service
public class AgeSeriesSearchService {

  private final SearchSelectorService searchSelectorService;

  @Autowired
  public AgeSeriesSearchService(SearchSelectorService searchSelectorService) {
    this.searchSelectorService = searchSelectorService;
  }

  List<AgeSeriesDto> getAgeSeriesSearchableResults(String searchTerm) {
    return AgeSeries.getAgeSeriesOptions().entrySet().stream()
        .filter(e -> e.getValue().toLowerCase().contains(searchTerm.toLowerCase()))
        .map(e -> new AgeSeriesDto(e.getKey(), e.getValue()))
        .toList();
  }

  public Map<String, String> getPreSelectedAgeSeries(String ageSeries) {
    if (ageSeries != null) {
      if (SearchSelectorService.isManualEntry(ageSeries)) {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(ageSeries),
            Map.of(ageSeries, ageSeries)
        );
      } else {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(ageSeries),
            Map.of(
                EnumUtils.getEnumValue(AgeSeries.class, ageSeries).name(),
                EnumUtils.getEnumValue(AgeSeries.class, ageSeries).getDisplayName()
            ));
      }
    }
    return Map.of();
  }
}
