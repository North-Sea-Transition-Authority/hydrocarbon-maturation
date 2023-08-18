package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ExtendWith(MockitoExtension.class)
class AgeSeriesSearchServiceTest {

  private static AgeSeriesSearchService ageSeriesSearchService;

  @BeforeAll
  static void setup() {
    ageSeriesSearchService = new AgeSeriesSearchService(new SearchSelectorService());
  }

  @Test
  void getAgeSeriesSearchableResults_withNoSearchTerm() {
    assertThat(ageSeriesSearchService.getAgeSeriesSearchableResults(""))
        .extracting(AgeSeriesDto::ageSeriesValue)
        .isEqualTo(AgeSeries.getAgeSeriesOptions().keySet().stream().toList());
  }

  @Test
  void getAgeSeriesSearchableResults_withSpecificFormationSearchTerm() {
    var ageSeries = AgeSeries.PLEISTOCENE;
    assertThat(ageSeriesSearchService.getAgeSeriesSearchableResults(ageSeries.name()))
        .extracting(AgeSeriesDto::ageSeriesValue, AgeSeriesDto::ageSeriesDisplay)
        .containsExactly(Tuple.tuple(ageSeries.name(), ageSeries.getDisplayName()));
  }

  @Test
  void getAgeSeriesSearchableResults_partialAndCaseInsensitiveSearchTerm() {
    assertThat(ageSeriesSearchService.getAgeSeriesSearchableResults("AsSiC"))
        .extracting(AgeSeriesDto::ageSeriesValue, AgeSeriesDto::ageSeriesDisplay)
        .contains(Tuple.tuple(AgeSeries.MIDDLE_JURASSIC.name(), AgeSeries.MIDDLE_JURASSIC.getDisplayName()));
  }

  @Test
  void getAgeSeriesSearchableResults_withNonFormationSearchTerm() {
    assertThat(ageSeriesSearchService.getAgeSeriesSearchableResults("Not an ageSeries")).isEmpty();
  }

  @Test
  void getPreSelectedAgeSeries_withNoFormation() {
    assertThat(ageSeriesSearchService.getPreSelectedAgeSeries(null)).isEmpty();
  }

  @Test
  void getPreSelectedAgeSeries() {
    var ageSeries = AgeSeries.LOWER_DEVONIAN;
    assertThat(ageSeriesSearchService.getPreSelectedAgeSeries(ageSeries))
        .containsEntry(ageSeries.name(), ageSeries.getDisplayName());
  }
}
