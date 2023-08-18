package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

  public Map<String, String> getPreSelectedAgeSeries(AgeSeries ageSeries) {
    if (ageSeries != null) {
      return searchSelectorService.buildPrePopulatedSelections(
          Collections.singletonList(ageSeries.name()),
          Map.of(
              ageSeries.name(),
              ageSeries.getDisplayName()
          ));
    }
    return Map.of();
  }
}
