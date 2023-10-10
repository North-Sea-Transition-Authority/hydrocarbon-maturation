package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = GeneralReservoirInformationController.class)
class GeneralReservoirInformationControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator;

  @MockBean
  private GeneralReservoirInformationService generalReservoirInformationService;

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    when(submissionDetailService.getSubmissionDetail(submissionDetail.getId())).thenReturn(Optional.of(submissionDetail));

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
  }

  @SecurityTest
  void getGeneralReservoirInformationPage_assertStatusOk() throws Exception {
    when(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .thenReturn(new GeneralReservoirInformationForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);

    mockMvc.perform(get(ReverseRouter.route(on(GeneralReservoirInformationController.class)
            .getGeneralReservoirInformationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/generalreservoirinformation/generalReservoirInformation"));
  }

  @SecurityTest
  void getGeneralReservoirInformationPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(get(ReverseRouter.route(on(GeneralReservoirInformationController.class)
            .getGeneralReservoirInformationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getGeneralReservoirInformationPage_andTypeIsNotDiscoveryOrField_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    mockMvc.perform(get(ReverseRouter.route(on(GeneralReservoirInformationController.class)
            .getGeneralReservoirInformationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getGeneralReservoirInformationPage_assertModelProperties_discovery() throws Exception {
    when(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .thenReturn(new GeneralReservoirInformationForm());

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(GeneralReservoirInformationController.class)
                        .getGeneralReservoirInformationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", "Discovery reservoir"),
            entry("isFieldSubmission", false),
            entry("hydrocarbonsDiscovered", DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class))
        )
        .doesNotContainKeys("alreadyAddedAssociatedDiscoveryReservoirs", "discoveryReservoirsApiEndpoint", "resourceNames");

    assertEquals(GeneralReservoirInformationForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @Test
  void getGeneralReservoirInformationPage_assertModelProperties_field() throws Exception {
    var form = new GeneralReservoirInformationForm();
    when(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .thenReturn(form);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var preselectedAssociatedDiscoveryReservoirs = List.of(new ReservoirView(UUID.randomUUID(), "Reservoir name"));
    when(generalReservoirInformationService.getPreselectedAssociatedDiscoveryReservoirs(form.getAssociatedDiscoveryReservoirs()))
        .thenReturn(preselectedAssociatedDiscoveryReservoirs);
    var resourceNames = List.of("Resource name");
    when(generalReservoirInformationService.getDiscoveryResourceNamesForFieldResourceLinkFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceNames);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(GeneralReservoirInformationController.class)
                        .getGeneralReservoirInformationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("pageTitle", "Field reservoir"),
            entry("isFieldSubmission", true),
            entry("hydrocarbonsDiscovered", DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class)),
            entry("alreadyAddedAssociatedDiscoveryReservoirs", preselectedAssociatedDiscoveryReservoirs),
            entry("discoveryReservoirsApiEndpoint",
                SearchSelectorService.route(on(ResourceRestController.class)
                    .searchDiscoveryReservoirs(null, submissionDetail.getId().toString()))),
            entry("resourceNames", resourceNames)
        );

    assertEquals(GeneralReservoirInformationForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(USER)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null)), ReservoirTaskListController.PAGE_TITLE)
    );
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class)
  void saveGeneralReservoirInformation_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(generalReservoirInformationFormValidator.isValid(any(GeneralReservoirInformationForm.class), any(BindingResult.class),
        eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(GeneralReservoirInformationController.class)
                .saveGeneralReservoirInformation(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(generalReservoirInformationService, never()).saveGeneralReservoirInformation(any(), any());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class)
  void saveGeneralReservoirInformation_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(generalReservoirInformationFormValidator.isValid(any(GeneralReservoirInformationForm.class), any(BindingResult.class),
        eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(GeneralReservoirInformationController.class)
                .saveGeneralReservoirInformation(submissionDetail.getId(), reservoirSubmissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetail.getId(), null))));

    verify(generalReservoirInformationService, times(1))
        .saveGeneralReservoirInformation(any(GeneralReservoirInformationForm.class), eq(reservoirSubmissionDetail));
  }
}