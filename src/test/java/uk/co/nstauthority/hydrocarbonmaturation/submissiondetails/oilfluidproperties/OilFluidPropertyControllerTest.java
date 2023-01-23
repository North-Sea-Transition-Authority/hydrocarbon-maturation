package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = OilFluidPropertyController.class)
class OilFluidPropertyControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private OilFluidPropertyFormService oilFluidPropertyFormService;

  @MockBean
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  private BindingResult bindingResult;

  private OilFluidPropertyForm oilFluidPropertyForm;

  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    oilFluidPropertyForm = new OilFluidPropertyForm();
    bindingResult = new BeanPropertyBindingResult(oilFluidPropertyForm, "form");
  }

  @SecurityTest
  void getOilFluidProperties_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(submissionDetail))
        .thenReturn(new OilFluidPropertyForm());

    mockMvc.perform(
            get(ReverseRouter.route(on(OilFluidPropertyController.class).getOilFluidProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/oilFluidProperties"));
  }

  @SecurityTest
  void getOilFluidProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(OilFluidPropertyController.class).getOilFluidProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveOilFluidProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(oilFluidPropertyFormService.validate(any(), any(), any(), eq(submissionDetail)))
        .thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(OilFluidPropertyController.class)
                .saveOilFluidProperties(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetailId))));

    verify(oilFluidPropertyPersistenceService, times(1))
        .saveOilFluidProperty(any(OilFluidPropertyForm.class), eq(submissionDetail));
    verify(oilFluidPropertyFormService, times(1))
        .validate(any(), any(), eq(validationType), eq(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveOilFluidProperties_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(oilFluidPropertyFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(OilFluidPropertyController.class)
                .saveOilFluidProperties(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(oilFluidPropertyFormService, times(1))
        .validate(any(), any(), eq(validationType), eq(submissionDetail));
    verify(oilFluidPropertyPersistenceService, never())
        .saveOilFluidProperty(any(), any());
  }

  @Test
  void getOilFluidPropertiesModelAndView_assertModelProperties() throws Exception {
    var form = new OilFluidPropertyForm();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(submissionDetail))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(OilFluidPropertyController.class)
                    .getOilFluidProperties(submissionDetailId)))
                    .with(user(USER))).andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(OilFluidPropertyForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form);
  }
}