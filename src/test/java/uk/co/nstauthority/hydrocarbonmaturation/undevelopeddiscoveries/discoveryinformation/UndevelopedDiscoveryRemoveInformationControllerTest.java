package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NotificationBannerTestUtil.notificationBanner;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.AbstractUndevelopedDiscoveryControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoverySummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;

@ContextConfiguration(classes = UndevelopedDiscoveryRemoveInformationController.class)
class UndevelopedDiscoveryRemoveInformationControllerTest extends AbstractUndevelopedDiscoveryControllerTest {

  @MockBean
  private UndevelopedDiscoveryInformationSummaryService summaryService;

  @MockBean
  private UndevelopedDiscoveryInformationService informationService;

  @MockBean
  private ViewResourceRecordService resourceRecordService;

  private final UUID INFORMATION_ID = UUID.randomUUID();

  @Test
  void getRemoveInformationConfirmation_assertModelProperties() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var information = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        undevelopedDiscovery
    );
    var testDisplayName = "test display name";
    var testSummaryCards = List.of(SummaryCard.simpleSummaryCard(SummaryDataView.newWithKeyValue("test", "test")));

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(informationService.getUndevelopedDiscoveryInformationOrThrow(INFORMATION_ID))
        .thenReturn(information);
    when(summaryService.getSummaryItem(information, List.of()))
        .thenReturn(SummaryItem.withCards(testDisplayName, testSummaryCards));

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                    .getRemoveInformationConfirmation(RESOURCE_ID, INFORMATION_ID)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", UndevelopedDiscoveryRemoveInformationController.PAGE_TITLE),
        entry("pageCaption", testDisplayName),
        entry("summaryCards", testSummaryCards),
        entry("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(RESOURCE_ID, null)))
    );
  }

  @SecurityTest
  void getRemoveInformationConfirmation_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .getRemoveInformationConfirmation(RESOURCE_ID, INFORMATION_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getRemoveInformationConfirmation_noUser() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .getRemoveInformationConfirmation(RESOURCE_ID, INFORMATION_ID))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getRemoveInformationConfirmation_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenThrow(new RemiEntityNotFoundException("not correct type"));

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .getRemoveInformationConfirmation(RESOURCE_ID, INFORMATION_ID)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void getRemoveInformationConfirmation_informationNotExtant() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);

    when(informationService.getUndevelopedDiscoveryInformationOrThrow(INFORMATION_ID))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .getRemoveInformationConfirmation(RESOURCE_ID, INFORMATION_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getRemoveInformationConfirmation_informationDoesNotBelongToDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var otherUndevelopedDiscovery = new UndevelopedDiscovery();
    otherUndevelopedDiscovery.setId(UUID.randomUUID());

    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setId(UUID.randomUUID());
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(otherUndevelopedDiscovery);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(informationService.getUndevelopedDiscoveryInformationOrThrow(INFORMATION_ID))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .getRemoveInformationConfirmation(RESOURCE_ID, INFORMATION_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void removeInformation() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var summaryUrl = ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
        .getUndevelopedDiscoverySummary(RESOURCE_ID, null));

    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setId(UUID.randomUUID());
    undevelopedDiscoveryInformation.setInformationDate(LocalDate.of(2002, 1, 11));
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(undevelopedDiscovery);

    var resourceRecord = new ResourceRecordSummaryView(null, "name", "coi", "RT9", null, null, null, null, null, null, null);

    var expectedBanner = NotificationBanner.builder()
        .withBannerType(NotificationBannerType.SUCCESS)
        .withTitle("Undeveloped discovery information deleted")
        .withContent(
            "The undeveloped discovery information entry for 'name' on date '11 January 2002' has been deleted.")
        .build();

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(informationService.getUndevelopedDiscoveryInformationOrThrow(INFORMATION_ID))
        .thenReturn(undevelopedDiscoveryInformation);
    when(resourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(resourceRecord);

    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
            .removeInformation(RESOURCE_ID, INFORMATION_ID, null)))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(summaryUrl))
        .andExpect(notificationBanner(expectedBanner));

    verify(informationService, times(1))
        .deleteInformation(undevelopedDiscoveryInformation);
  }

  @SecurityTest
  void removeInformation_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .removeInformation(RESOURCE_ID, INFORMATION_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(informationService, never())
        .deleteInformation(any());
  }

  @SecurityTest
  void removeInformation_noUser() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .removeInformation(RESOURCE_ID, INFORMATION_ID, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());

    verify(informationService, never())
        .deleteInformation(any());
  }

  @Test
  void removeInformation_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenThrow(new RemiEntityNotFoundException("not correct type"));

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .removeInformation(RESOURCE_ID, INFORMATION_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());

    verify(informationService, never())
        .deleteInformation(any());
  }

  @Test
  void removeInformation_informationNotExtant() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);

    when(informationService.getUndevelopedDiscoveryInformationOrThrow(INFORMATION_ID))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .removeInformation(RESOURCE_ID, INFORMATION_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(informationService, never())
        .deleteInformation(any());
  }

  @Test
  void removeInformation_informationDoesNotBelongToDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var otherUndevelopedDiscovery = new UndevelopedDiscovery();
    otherUndevelopedDiscovery.setId(UUID.randomUUID());

    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setId(UUID.randomUUID());
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(otherUndevelopedDiscovery);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(informationService.getUndevelopedDiscoveryInformationOrThrow(INFORMATION_ID))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .removeInformation(RESOURCE_ID, INFORMATION_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(informationService, never())
        .deleteInformation(any());
  }
}
