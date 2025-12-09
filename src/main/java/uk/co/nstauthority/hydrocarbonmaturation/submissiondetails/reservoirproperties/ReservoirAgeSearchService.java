package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@Service
public class ReservoirAgeSearchService {

  private final SearchSelectorService searchSelectorService;

  @Autowired
  public ReservoirAgeSearchService(SearchSelectorService searchSelectorService) {
    this.searchSelectorService = searchSelectorService;
  }

  List<ReservoirAgeDto> getAgeSeriesSearchableResults(String searchTerm) {
    return ReservoirAge.getOptions().entrySet().stream()
        .filter(e -> e.getValue().toLowerCase().contains(searchTerm.toLowerCase()))
        .map(e -> new ReservoirAgeDto(e.getKey(), e.getValue()))
        .toList();
  }

  public Map<String, String> getPreSelectedAgeSeries(ReservoirAge reservoirAge) {
    if (reservoirAge != null) {
      return searchSelectorService.buildPrePopulatedSelections(
          Collections.singletonList(reservoirAge.name()),
          Map.of(
              reservoirAge.name(),
              reservoirAge.getDisplayName()
          ));
    }
    return Map.of();
  }
}
