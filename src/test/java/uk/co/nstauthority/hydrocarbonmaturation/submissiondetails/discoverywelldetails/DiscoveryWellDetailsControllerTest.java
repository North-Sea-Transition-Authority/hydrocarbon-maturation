package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

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

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.DryWellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = DiscoveryWellDetailsController.class)
class DiscoveryWellDetailsControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private DiscoveryWellDetailsFormService discoveryWellDetailsFormService;

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.setResourceId(resource);
  }

  @Test
  void getDiscoveryWellDetails_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail)).thenReturn(new DiscoveryWellDetailsForm());
    mockMvc.perform(
            get(ReverseRouter.route(on(DiscoveryWellDetailsController.class).getDiscoveryWellDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/wellDrillDetails/discoveryWellDrillDetails"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveDiscoveryWellDetails_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(DiscoveryWellDetailsForm.class, "form");
    when(discoveryWellDetailsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                .saveDiscoveryWellDetails(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(discoveryWellDetailsFormService, times(1)).validate(any(), any(), eq(validationType));
    verify(discoveryWellDetailsPersistenceService, times(1))
        .saveDiscoveryWellDetails(any(DiscoveryWellDetailsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveGeneralDetailsPage_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(DiscoveryWellDetailsForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(discoveryWellDetailsFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                .saveDiscoveryWellDetails(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf())
                .with(user(USER)))
        .andExpect(status().isOk());

    verify(discoveryWellDetailsFormService, times(1)).validate(any(), any(), eq(validationType));
    verify(discoveryWellDetailsPersistenceService, never()).saveDiscoveryWellDetails(any(), any());
  }

  @Test
  void getDiscoveryWellDetailsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var form = new DiscoveryWellDetailsForm();
    var preselectedWell = Map.of("well1", "well1");

    when(discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail)).thenReturn(form);
    when(discoveryWellDetailsFormService.getPreSelectedDryWell(form.getWellDetailsForm().getDryWell())).thenReturn(preselectedWell);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(DiscoveryWellDetailsController.class)
                        .getDiscoveryWellDetails(submissionDetailId, null)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("datumTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirDatum.class)),
            entry("discoveryStatus", DisplayableEnumOptionUtil.getDisplayableOptions(DiscoveryStatus.class)),
            entry("longitudeHemisphere", Hemisphere.getLongitudeHemisphere()),
            entry("hydrocarbonsDiscovered", DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class)),
            entry("dryWellEndpoint",
                SearchSelectorService.route(on(DryWellRestController.class).searchDryWells(null))),
            entry("dryWell", preselectedWell)
        );

    assertEquals(DiscoveryWellDetailsForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)),
            TaskListController.PAGE_NAME)
    );
  }
}