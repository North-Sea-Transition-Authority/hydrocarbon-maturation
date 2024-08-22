package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoverySummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private List<SummarySectionService<UndevelopedDiscovery>> summarySectionServices;

  private UndevelopedDiscoverySummaryService undevelopedDiscoverySummaryService;

  private UndevelopedDiscovery undevelopedDiscovery;

  @BeforeEach
  void setUp() {
    var section1 = mock(UndevelopedDiscoveryTrackingDetailsSummaryService.class);
    var section2 = mock(SummarySectionService.class);
    var section3 = mock(SummarySectionService.class);
    summarySectionServices = List.of(section1, section2, section3);

    undevelopedDiscoverySummaryService = new UndevelopedDiscoverySummaryService(summarySectionServices);

    undevelopedDiscovery = new UndevelopedDiscovery();
  }

  @Test
  void getSummarySection_allSectionsEmpty_thenEmptyList() {
    assertThat(undevelopedDiscoverySummaryService.getSummarySections(undevelopedDiscovery, USER))
        .isEmpty();
  }

  @Test
  void getSummarySection_singleSectionPresent_thenSectionInList() {
    var summarySection = new SummarySection(10, null, null);
    when(summarySectionServices.get(0).getSummarySection(undevelopedDiscovery, USER))
        .thenReturn(Optional.of(summarySection));

    assertThat(undevelopedDiscoverySummaryService.getSummarySections(undevelopedDiscovery, USER))
        .contains(summarySection);
  }

  @Test
  void getSummarySection_multipleSectionsPresent_thenSectionsInOrderedList() {
    var summarySection1 = new SummarySection(25, null, null);
    when(summarySectionServices.get(0).getSummarySection(undevelopedDiscovery, USER))
        .thenReturn(Optional.of(summarySection1));
    var summarySection2 = new SummarySection(5, null, null);
    when(summarySectionServices.get(1).getSummarySection(undevelopedDiscovery, USER))
        .thenReturn(Optional.of(summarySection2));
    var summarySection3 = new SummarySection(1, null, null);
    when(summarySectionServices.get(2).getSummarySection(undevelopedDiscovery, USER))
        .thenReturn(Optional.of(summarySection3));

    assertThat(undevelopedDiscoverySummaryService.getSummarySections(undevelopedDiscovery, USER))
        .containsExactly(
            summarySection3,
            summarySection2,
            summarySection1
        );
  }
}
