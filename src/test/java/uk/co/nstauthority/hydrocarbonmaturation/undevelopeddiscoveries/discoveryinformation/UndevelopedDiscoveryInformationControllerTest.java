package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.AbstractUndevelopedDiscoveryControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoverySummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;

@ContextConfiguration(classes = UndevelopedDiscoveryInformationController.class)
class UndevelopedDiscoveryInformationControllerTest extends AbstractUndevelopedDiscoveryControllerTest {

  @MockitoBean
  private UndevelopedDiscoveryInformationFormValidator formValidator;

  @MockitoBean
  private ViewResourceRecordService resourceRecordService;

  @MockitoBean
  private UndevelopedDiscoveryInformationService undevelopedDiscoveryInformationService;

  private UndevelopedDiscoveryInformation undevelopedDiscoveryInformation;

  @Test
  void getUndevelopedDiscoveryInformation_assertModelProperties() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var resourceName = "resource name";
    var centreOfInterest = "coi";
    var licenceApplicationReference = "RT9";
    var resourceType = ResourceType.DISCOVERY;
    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, resourceType))
        .thenReturn(resource);
    when(resourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(
            new ResourceRecordSummaryView(resourceType, null, resourceName, centreOfInterest, licenceApplicationReference, null, null,
                null, null, null, null, null, null));

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
        entry("pageCaption", "resource name (coi) (Round RT9)"),
        entry("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(RESOURCE_ID, null))),
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

    doThrow(new RemiEntityNotFoundException("resource not of type"))
        .when(resourceService)
        .getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY);

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getUndevelopedDiscoveryInformation(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void saveDiscoveryInformation_validationFailed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryInformationForm();
    var resourceType = ResourceType.DISCOVERY;

    when(formValidator.isValid(eq(form), any(Errors.class), eq(undevelopedDiscovery), eq(null)))
        .thenReturn(false);
    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, resourceType))
        .thenReturn(resource);
    when(resourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(
            new ResourceRecordSummaryView(resourceType,null, "resourceName", "coi", "RT9", null, null, null, null, null, null,
                null, null));
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
        .saveUndevelopedDiscoveryInformation(any(), any(), any());
  }

  @Test
  void saveDiscoveryInformation_validationPassed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryInformationForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(undevelopedDiscovery), eq(null)))
        .thenReturn(true);
    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);

    var summaryUrl = ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
        .getUndevelopedDiscoverySummary(RESOURCE_ID, null));
    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
            .saveDiscoveryInformation(RESOURCE_ID, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(summaryUrl));

    verify(undevelopedDiscoveryInformationService, times(1))
        .saveUndevelopedDiscoveryInformation(form, undevelopedDiscovery, null);
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

    doThrow(new RemiEntityNotFoundException("resource not of type"))
        .when(resourceService)
        .getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY);

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .saveDiscoveryInformation(RESOURCE_ID, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getExistingUndevelopedDiscoveryInformation_assertModelProperties() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var resourceName = "resource name";

    var informationId = UUID.randomUUID();
    undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setId(informationId);
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(undevelopedDiscovery);
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);

    var returnedForm = new UndevelopedDiscoveryInformationForm();
    var resourceType = ResourceType.DISCOVERY;

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(resourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(
            new ResourceRecordSummaryView(
                resourceType,
                null,
                resourceName,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null));
    when(undevelopedDiscoveryInformationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .thenReturn(undevelopedDiscoveryInformation);
    when(undevelopedDiscoveryInformationService.entityToForm(undevelopedDiscoveryInformation))
        .thenReturn(returnedForm);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
                get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                    .getExistingUndevelopedDiscoveryInformation(RESOURCE_ID, informationId)))
                    .with(user(USER)))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
    ).getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", UndevelopedDiscoveryInformationController.PAGE_TITLE),
        entry("pageCaption", resourceName),
        entry("cancelUrl", ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
            .getUndevelopedDiscoverySummary(RESOURCE_ID, null))),
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
        .isEqualTo(returnedForm);
  }

  @SecurityTest
  void getExistingUndevelopedDiscoveryInformation_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getExistingUndevelopedDiscoveryInformation(RESOURCE_ID, UUID.randomUUID())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getExistingUndevelopedDiscoveryInformation_noUser() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getExistingUndevelopedDiscoveryInformation(RESOURCE_ID, UUID.randomUUID()))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getExistingUndevelopedDiscoveryInformation_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    doThrow(new RemiEntityNotFoundException("resource not of type"))
        .when(resourceService)
        .getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY);

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getExistingUndevelopedDiscoveryInformation(RESOURCE_ID, UUID.randomUUID())))
                .with(user(USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  void getExistingUndevelopedDiscoveryInformation_informationIsNotExtant() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var informationId = UUID.randomUUID();
    undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setId(informationId);
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);

    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(undevelopedDiscoveryInformationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getExistingUndevelopedDiscoveryInformation(RESOURCE_ID, informationId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getExistingUndevelopedDiscoveryInformation_informationDoesNotBelongToDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.TRUE);
    var differentUndevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource,
        Boolean.TRUE);
    differentUndevelopedDiscovery.setId(UUID.randomUUID());

    var informationId = UUID.randomUUID();
    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(differentUndevelopedDiscovery);
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(undevelopedDiscoveryInformationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            get(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getExistingUndevelopedDiscoveryInformation(RESOURCE_ID, informationId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateDiscoveryInformation_validationFailed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryInformationForm();

    var informationId = UUID.randomUUID();
    var undevelopedDiscoveryInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        undevelopedDiscovery
    );

    var resourceType = ResourceType.DISCOVERY;
    when(formValidator.isValid(eq(form), any(Errors.class), eq(undevelopedDiscovery), eq(informationId)))
        .thenReturn(false);
    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, resourceType))
        .thenReturn(resource);
    when(resourceRecordService.getResourceRecordOrThrow(RESOURCE_ID))
        .thenReturn(
            new ResourceRecordSummaryView(
                resourceType,
                null,
                "resourceName",
                "coi",
                "RT9",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null));
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(undevelopedDiscoveryInformationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
            .updateDiscoveryInformation(RESOURCE_ID, informationId, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/undevelopeddiscoveries/undevelopedDiscoveryInformationForm"));

    verify(undevelopedDiscoveryInformationService, never())
        .saveUndevelopedDiscoveryInformation(any(), any(), any());
  }

  @Test
  void updateDiscoveryInformation_validationPassed() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);
    var form = new UndevelopedDiscoveryInformationForm();

    var informationId = UUID.randomUUID();
    var undevelopedDiscoveryInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        undevelopedDiscovery
    );

    when(formValidator.isValid(eq(form), any(Errors.class), eq(undevelopedDiscovery), eq(informationId)))
        .thenReturn(true);
    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(undevelopedDiscoveryInformationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .thenReturn(undevelopedDiscoveryInformation);

    var summaryUrl = ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class)
        .getUndevelopedDiscoverySummary(RESOURCE_ID, null));
    mockMvc.perform(post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
            .updateDiscoveryInformation(RESOURCE_ID, informationId, null, null)))
            .flashAttr("form", form)
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(summaryUrl));

    verify(undevelopedDiscoveryInformationService, times(1))
        .saveUndevelopedDiscoveryInformation(form, undevelopedDiscovery, informationId);
  }

  @SecurityTest
  void updateDiscoveryInformation_userIsNotReservesAndResourcesAdvisor() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .updateDiscoveryInformation(RESOURCE_ID, UUID.randomUUID(), null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void updateDiscoveryInformation_noUser() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .updateDiscoveryInformation(RESOURCE_ID, UUID.randomUUID(), null, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void updateDiscoveryInformation_resourceIsNotDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    doThrow(new RemiEntityNotFoundException("resource not of type"))
        .when(resourceService)
        .getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY);

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .updateDiscoveryInformation(RESOURCE_ID, UUID.randomUUID(), null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateDiscoveryInformation_informationIsNotExtant() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var informationId = UUID.randomUUID();
    undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setId(informationId);
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);

    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(undevelopedDiscoveryInformationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .updateDiscoveryInformation(RESOURCE_ID, informationId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateDiscoveryInformation_informationDoesNotBelongToDiscovery() throws Exception {
    setupUserIsReservesAndResourcesAdvisor(user);

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.TRUE);
    var differentUndevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource,
        Boolean.TRUE);
    differentUndevelopedDiscovery.setId(UUID.randomUUID());

    var informationId = UUID.randomUUID();
    var undevelopedDiscoveryInformation = new UndevelopedDiscoveryInformation();
    undevelopedDiscoveryInformation.setUndevelopedDiscovery(differentUndevelopedDiscovery);
    undevelopedDiscoveryInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);

    when(resourceService.getResourceOfTypeOrThrow(RESOURCE_ID, ResourceType.DISCOVERY))
        .thenReturn(resource);
    when(undevelopedDiscoveryService.getUndevelopedDiscoveryForResourceOrThrow(resource))
        .thenReturn(undevelopedDiscovery);
    when(undevelopedDiscoveryInformationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .thenReturn(undevelopedDiscoveryInformation);

    mockMvc.perform(
            post(ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .updateDiscoveryInformation(RESOURCE_ID, informationId, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}
