package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DryWellQueryServiceTest {

  private DryWellQueryService dryWellQueryService;

  @BeforeEach
  void setUp() {
    dryWellQueryService = new DryWellQueryService();
  }

  @Test
  void getWellsByRef_singleWellFound() {
    var listOfWells = dryWellQueryService.getWellsByRef("ND2");

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
    var listOfWells = dryWellQueryService.getWellsByRef("25/");

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
    var listOfWells = dryWellQueryService.getDryWellByName("29/10");

    assertThat(listOfWells)
        .extracting(
            ReferenceWellView::reference,
            ReferenceWellView::id)
        .containsExactly(
            tuple("29/10", "29/10")
        );
  }
}