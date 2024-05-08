package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.Errors;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = UndevelopedDiscoveryTrackingDetailsController.class)
class UndevelopedDiscoveryTrackingDetailsControllerTest extends AbstractUndevelopedDiscoveryControllerTest {

  @MockBean
  private UndevelopedDiscoveryTrackingDetailsFormValidator formValidator;

  @Test
  void getUndevelopedDiscoveryTrackingDetails_assertModelProperties() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryTrackingDetailsForm();

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource))
        .thenReturn(Optional.of(undevelopedDiscovery));
    when(undevelopedDiscoveryService.discoveryToTrackingDetailsForm(undevelopedDiscovery))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
                    .getUndevelopedDiscoveryTrackingDetails(RESOURCE_ID)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", UndevelopedDiscoveryTrackingDetailsController.PAGE_TITLE),
        entry("form", form),
        entry("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(RESOURCE_ID)))
    );
  }

  @SecurityTest
  void getUndevelopedDiscoveryTrackingDetails_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(
                on(UndevelopedDiscoveryTrackingDetailsController.class).getUndevelopedDiscoveryTrackingDetails(
                    RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getUndevelopedDiscoveryTrackingDetails_noUser() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(
                on(UndevelopedDiscoveryTrackingDetailsController.class).getUndevelopedDiscoveryTrackingDetails(
                    RESOURCE_ID))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getUndevelopedDiscoveryTrackingDetails_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

    mockMvc.perform(
            get(ReverseRouter.route(
                on(UndevelopedDiscoveryTrackingDetailsController.class).getUndevelopedDiscoveryTrackingDetails(
                    RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void saveTrackingDetails_validationFailed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryTrackingDetailsForm();

    when(undevelopedDiscoveryService.discoveryToTrackingDetailsForm(undevelopedDiscovery))
        .thenReturn(form);
    when(formValidator.isValidTrackingDetails(any(Errors.class)))
        .thenReturn(false);
    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);

    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
            .saveTrackingDetails(RESOURCE_ID, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/undevelopeddiscoveries/includeInUndevelopedDiscoveries"));

    verify(undevelopedDiscoveryService, never()).saveUndevelopedDiscoveryTrackingDetails(any(), any());
  }

  @Test
  void saveTrackingDetails_validationPassed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryTrackingDetailsForm();
    form.setIsUndevelopedDiscovery(Boolean.TRUE);

    when(undevelopedDiscoveryService.discoveryToTrackingDetailsForm(undevelopedDiscovery))
        .thenReturn(form);
    when(formValidator.isValidTrackingDetails(any(Errors.class)))
        .thenReturn(true);
    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);

    var summaryUrl = ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
        .getUndevelopedDiscoverySummary(RESOURCE_ID));

    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
            .saveTrackingDetails(RESOURCE_ID, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(summaryUrl));

    verify(undevelopedDiscoveryService, times(1))
        .saveUndevelopedDiscoveryTrackingDetails(resource, form);
  }

  @SecurityTest
  void saveTrackingDetails_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
                .saveTrackingDetails(RESOURCE_ID, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveTrackingDetails_noUser() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
                .saveTrackingDetails(RESOURCE_ID, null, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void saveTrackingDetails_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
                .saveTrackingDetails(RESOURCE_ID, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }
}
