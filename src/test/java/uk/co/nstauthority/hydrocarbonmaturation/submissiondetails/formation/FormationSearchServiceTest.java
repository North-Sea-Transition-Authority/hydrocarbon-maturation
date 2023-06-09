package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@ExtendWith(MockitoExtension.class)
class FormationSearchServiceTest {

  private static FormationSearchService formationSearchService;

  @BeforeAll
  static void setup() {
    formationSearchService = new FormationSearchService(new SearchSelectorService());
  }

  @Test
  void getFormationSearchableResults_withNoSearchTerm() {
    assertThat(formationSearchService.getFormationSearchableResults(""))
        .extracting(FormationDto::formationValue)
        .isEqualTo(Formation.getFormationNames().keySet().stream().toList());
  }

  @Test
  void getFormationSearchableResults_withSpecificFormationSearchTerm() {
    var formation = Formation.AUK;
    assertThat(formationSearchService.getFormationSearchableResults(formation.name()))
        .extracting(FormationDto::formationValue, FormationDto::formationDisplay)
        .containsExactly(Tuple.tuple(formation.name(), formation.getDisplayName()));
  }

  @Test
  void getFormationSearchableResults_partialAndCaseInsensitiveSearchTerm() {
    assertThat(formationSearchService.getFormationSearchableResults("aSg"))
        .extracting(FormationDto::formationValue, FormationDto::formationDisplay)
        .contains(Tuple.tuple(Formation.ASGARD.name(), Formation.ASGARD.getDisplayName()));
  }

  @Test
  void getFormationSearchAbleResults_withNonFormationSearchTerm() {
    assertThat(formationSearchService.getFormationSearchableResults("Not a formation")).isEmpty();
  }

  @Test
  void getPreSelectedFormation_withNoFormation() {
    assertThat(formationSearchService.getPreSelectedFormation(null)).isEmpty();
  }

  @Test
  void getPreSelectedFormation_withManualFormation() {
    var manualFormation = SearchSelectorService.getValueWithManualEntryPrefix("manual formation");
    assertThat(formationSearchService.getPreSelectedFormation(manualFormation))
        .containsEntry(manualFormation, "manual formation");
  }

  @Test
  void getPreSelectedFormation_withEnum() {
    var formation = Formation.AUK;
    assertThat(formationSearchService.getPreSelectedFormation(formation.name()))
        .containsEntry(formation.name(), formation.getDisplayName());
  }
}
