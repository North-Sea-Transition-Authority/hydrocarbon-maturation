package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;

@ExtendWith(MockitoExtension.class)
class SearchResourceRecordsQueryServiceTest {
  @Mock
  private SearchResourceRecordsService searchResourceRecordsService;
  @Mock
  private ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;
  @InjectMocks
  private SearchResourceRecordsQueryService searchResourceRecordsQueryService;

  @Test
  void search_assertResults() {
    var queryServiceStrategy = mock(ResourceRecordQueryServiceStrategy.class);
    when(resourceRecordQueryServiceFactory.getQueryServiceStrategy())
        .thenReturn(queryServiceStrategy);
    var searchResults = List.of("Field 2", "Field 1", "Field");
    when(searchResourceRecordsService
        .getResourceRecordSearchResultsDistinctResourceNames(any(SearchResourceRecordsForm.class), eq(queryServiceStrategy)))
        .thenReturn(searchResults);

    var expectedSearchResult = new RestSearchResult(List.of(
        new RestSearchItem("Field", "Field"),
        new RestSearchItem("Field 1", "Field 1"),
        new RestSearchItem("Field 2", "Field 2")
    ));

    assertThat(searchResourceRecordsQueryService.search("Field"))
        .usingRecursiveComparison()
        .isEqualTo(expectedSearchResult);
  }
}