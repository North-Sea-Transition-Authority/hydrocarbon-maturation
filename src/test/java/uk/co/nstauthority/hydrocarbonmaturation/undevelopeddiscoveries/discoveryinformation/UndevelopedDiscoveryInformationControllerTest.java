package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.AbstractUndevelopedDiscoveryControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoverySummaryController;

@ContextConfiguration(classes = UndevelopedDiscoveryInformationController.class)
class UndevelopedDiscoveryInformationControllerTest extends AbstractUndevelopedDiscoveryControllerTest {

  @MockBean
  private UndevelopedDiscoveryInformationFormValidator formValidator;

  @MockBean
  private ViewResourceRecordService resourceRecordService;

  @MockBean
  private UndevelopedDiscoveryInformationService undevelopedDiscoveryInformationService;

  @Test
  void getUndevelopedDiscoveryInformation_assertModelProperties() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var resourceName = "resource name";

    when(resourceService.getResourceOfType(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(Optional.of(resource));
    when(resourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(new ResourceRecordSummaryView(null, resourceName, null, null, null, null, null, null, null));

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                    .getUndevelopedDiscoveryInformation(RESOURCE_ID)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", UndevelopedDiscoveryInformationController.PAGE_TITLE),
        entry("pageCaption", resourceName),
        entry("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(RESOURCE_ID))),
        entry("viscosityRangeMap", DisplayableEnumOptionUtil.getDisplayableOptions(ViscosityRange.class)),
        entry("verticalHeterogeneityRangeMap", DisplayableEnumOptionUtil.getDisplayableOptions(
            VerticalHeterogeneityRange.class)),
        entry("arealDensityRangeMap", DisplayableEnumOptionUtil.getDisplayableOptions(ArealDensityRange.class)),
        entry("structuralComplexityRangeMap", DisplayableEnumOptionUtil.getDisplayableOptions(
            StructuralComplexityRange.class))
    );

    @SuppressWarnings(value = "unchecked")
    var modelForm = (UndevelopedDiscoveryInformationForm) modelMap.get("form");
    assertThat(modelForm)
        .usingRecursiveComparison()
        .isEqualTo(new UndevelopedDiscoveryInformationForm());
  }

  @SecurityTest
  void getUndevelopedDiscoveryInformation_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getUndevelopedDiscoveryInformation(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getUndevelopedDiscoveryInformation_noUser() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
            .getUndevelopedDiscoveryInformation(RESOURCE_ID))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getUndevelopedDiscoveryInformation_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    when(resourceService.getResourceOfType(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getUndevelopedDiscoveryInformation(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void saveDiscoveryInformation_validationFailed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryInformationForm();
    
    when(formValidator.isValid(eq(form), any(Errors.class), eq(undevelopedDiscovery)))
        .thenReturn(false);
    when(resourceService.getResourceOfType(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(Optional.of(resource));
    when(resourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(new ResourceRecordSummaryView(null, "resourceName", null, null, null, null, null, null, null));
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);

    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
            .saveDiscoveryInformation(RESOURCE_ID, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/undevelopeddiscoveries/undevelopedDiscoveryInformationForm"));

    verify(undevelopedDiscoveryInformationService, never())
        .saveUndevelopedDiscoveryInformation(any(), any());
  }

  @Test
  void saveDiscoveryInformation_validationPassed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryInformationForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(undevelopedDiscovery)))
        .thenReturn(true);
    when(resourceService.getResourceOfType(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(Optional.of(resource));
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);

    var summaryUrl = ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
        .getUndevelopedDiscoverySummary(RESOURCE_ID));
    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
            .saveDiscoveryInformation(RESOURCE_ID, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(summaryUrl));

    verify(undevelopedDiscoveryInformationService, times(1))
        .saveUndevelopedDiscoveryInformation(form, undevelopedDiscovery);
  }

  @SecurityTest
  void saveDiscoveryInformation_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .saveDiscoveryInformation(RESOURCE_ID, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveDiscoveryInformation_noUser() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .saveDiscoveryInformation(RESOURCE_ID, null, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void saveDiscoveryInformation_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    when(resourceService.getResourceOfType(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .saveDiscoveryInformation(RESOURCE_ID, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}
