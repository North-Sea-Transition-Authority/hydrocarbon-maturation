package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;

@ExtendWith(MockitoExtension.class)
class SearchResourceRecordsServiceTest {
  @Mock
  private ResourceRecordQueryService resourceRecordQueryService;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @InjectMocks
  private SearchResourceRecordsService underTest;

  @Nested
  class WhenGettingResourceRecordSearchResults {

    @Mock
    private SearchResourceRecordsForm form;

    @Test
    void buildsResourceRecordSearchResultItem_AssertItemsAreBuiltCorrectly() {
      var licencesDisplayed1 = List.of("L1", "L2");
      var result1 =
          buildAndMockResourceRecordQueryResult("resource-name1", "1/12", 1, "Org A", List.of(1, 2), licencesDisplayed1);
      var licencesDisplayed2 = List.of("L3", "L4");
      var result2 =
          buildAndMockResourceRecordQueryResult("resource-name2", null, 2, "Org B", List.of(3, 4), licencesDisplayed2);
      when(resourceRecordQueryService.getResourceRecordSearchResults(form))
          .thenReturn(List.of(result1, result2));

      var results = underTest.getResourceRecordSearchResults(form);

      assertThat(results)
          .extracting(
              ResourceRecordSearchResultItem::headingText,
              ResourceRecordSearchResultItem::captionText,
              ResourceRecordSearchResultItem::actionUrl
          )
          .containsExactly(
              tuple(
                  "resource-name1 (1/12)",
                  result1.getResourceType().getDisplayName(),
                  getActionUrl()
              ),
              tuple(
                  "resource-name2",
                  result2.getResourceType().getDisplayName(),
                  getActionUrl()
              )
          );

      assertThat(results.get(0).modelProperties().getProperties())
          .containsExactly(
              entry("organisationResponsible", "Org A"),
              entry("licences", licencesDisplayed1)
          );

      assertThat(results.get(1).modelProperties().getProperties())
          .containsExactly(
              entry("organisationResponsible", "Org B"),
              entry("licences", licencesDisplayed2)
          );
    }

    @NotNull
    private String getActionUrl() {
      return ReverseRouter.route(on(SearchResourceRecordsController.class)
          .showSearchResourceRecordsScreen());
    }

    @NotNull
    private ResourceRecordQueryResult buildAndMockResourceRecordQueryResult(String resourceName,
                                                                            String centreOfInterest,
                                                                            int organisationResponsibleId,
                                                                            String orgName,
                                                                            List<Integer> licencesTheResourceIsOn,
                                                                            List<String> licencesDisplayed) {
      when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(organisationResponsibleId)).thenReturn(orgName);
      when(submissionDetailDisplayService.getLicencesForDisplay(licencesTheResourceIsOn)).thenReturn(licencesDisplayed);

      return new ResourceRecordQueryResult(
          UUID.randomUUID(),
          "PROSPECT",
          resourceName,
          centreOfInterest,
          organisationResponsibleId,
          licencesTheResourceIsOn,
          Timestamp.from(Instant.now().minusSeconds(1)),
          Timestamp.from(Instant.now())
      );
    }

  }
}