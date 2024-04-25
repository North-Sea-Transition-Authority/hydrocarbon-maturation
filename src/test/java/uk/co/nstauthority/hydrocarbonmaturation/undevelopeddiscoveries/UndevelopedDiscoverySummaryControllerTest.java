package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchProspectAndDiscoveryRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;

@ContextConfiguration(classes = UndevelopedDiscoverySummaryController.class)
class UndevelopedDiscoverySummaryControllerTest extends AbstractUndevelopedDiscoveryControllerTest {

  @MockBean
  private UndevelopedDiscoverySummaryService undevelopedDiscoverySummaryService;

  @MockBean
  private ViewResourceRecordService viewResourceRecordService;

  @Mock
  private ResourceRecordSummaryView resourceRecordSummaryView;

  @Test
  void getUndevelopedDiscoverySummary_assertModelProperties() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var resourceName = "Resource name";
    var summaryDataView = SummaryDataView.newWithKeyValue("Included...", "Yes");
    var summaryCard = new SummaryCard("Tracking details", SummaryCardType.SIMPLE_SUMMARY, summaryDataView,
        List.of());
    var summarySections = List.of(
        new SummarySection(10, List.of(SummaryItem.withCard("Tracking details", summaryCard)), ""));

    when(resourceService.getResourceOfType(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(Optional.of(resource));
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource))
        .thenReturn(Optional.of(undevelopedDiscovery));
    when(resourceRecordSummaryView.resourceNameAndCentreOfInterest())
        .thenReturn(resourceName);
    when(viewResourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(resourceRecordSummaryView);
    when(undevelopedDiscoverySummaryService.getSummarySections(undevelopedDiscovery))
        .thenReturn(summarySections);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(
                    on(UndevelopedDiscoverySummaryController.class).getUndevelopedDiscoverySummary(RESOURCE_ID)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", UndevelopedDiscoverySummaryController.PAGE_TITLE),
        entry("summarySections", summarySections)
    );

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            Map.entry(ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)),
                SearchProspectAndDiscoveryRecordsController.PAGE_TITLE),
            Map.entry(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)),
                resourceName)
        );
  }

  @SecurityTest
  void getUndevelopedDiscoverySummary_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(
                on(UndevelopedDiscoverySummaryController.class).getUndevelopedDiscoverySummary(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getUndevelopedDiscoverySummary_noUser() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(
                on(UndevelopedDiscoverySummaryController.class).getUndevelopedDiscoverySummary(RESOURCE_ID))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getUndevelopedDiscoverySummary_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    when(resourceService.getResourceOfType(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(
                on(UndevelopedDiscoverySummaryController.class).getUndevelopedDiscoverySummary(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}
