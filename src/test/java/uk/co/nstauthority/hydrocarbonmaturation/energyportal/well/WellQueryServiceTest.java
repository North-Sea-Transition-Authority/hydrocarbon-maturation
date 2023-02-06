package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WellQueryServiceTest {

  private WellQueryService wellQueryService;

  @BeforeEach
  void setUp() {
    wellQueryService = new WellQueryService();
  }

  @Test
  void getWellsByRef_singleWellFound() {
    var listOfWells = wellQueryService.getWellsByRef("ND2");

    assertThat(listOfWells)
        .extracting(
            ReferenceWellView::reference,
            ReferenceWellView::id)
        .containsExactly(
            tuple("ND27a", "ND27a")
        );
  }

  @Test
  void getWellsByRef_twoWellsFound() {
    var listOfWells = wellQueryService.getWellsByRef("25/");

    assertThat(listOfWells)
        .extracting(
            ReferenceWellView::reference,
            ReferenceWellView::id)
        .containsExactly(
            tuple("25/10", "25/10"),
            tuple("25/15", "25/15")
        );
  }

  @Test
  void getDryWellByName() {
    var listOfWells = wellQueryService.getWellByName("29/10");

    assertThat(listOfWells)
        .extracting(
            ReferenceWellView::reference,
            ReferenceWellView::id)
        .containsExactly(
            tuple("29/10", "29/10")
        );
  }
}