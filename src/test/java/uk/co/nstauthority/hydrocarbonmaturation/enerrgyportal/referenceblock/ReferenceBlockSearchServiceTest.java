package uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

// TODO HM-54 test will be rewritten once ReferenceBlockSearchService uses energy portal api
@ExtendWith(MockitoExtension.class)
class ReferenceBlockSearchServiceTest {

  @InjectMocks
  ReferenceBlockSearchService referenceBlockSearchService;

  @Test
  void getReferenceBlocksBy() {
    var searchReference = "25";

    var viewList = referenceBlockSearchService.getReferenceBlocksBy(searchReference);

    assertThat(viewList)
        .extracting(ReferenceBlockView::name)
        .containsExactly("25/10", "25/15");
  }

  @Test
  void getReferenceBlocksByRefsIn() {
    var blockRefs = List.of("25/10", "25/15", "25/12");

    var viewList = referenceBlockSearchService.getReferenceBlocksByRefsIn(blockRefs);

    assertThat(viewList)
        .extracting(ReferenceBlockView::name)
        .containsExactly("25/10", "25/15");
  }
}