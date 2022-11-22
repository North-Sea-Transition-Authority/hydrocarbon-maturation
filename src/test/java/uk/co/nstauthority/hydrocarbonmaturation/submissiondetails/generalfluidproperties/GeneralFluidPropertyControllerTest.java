package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = GeneralFluidPropertyController.class)
class GeneralFluidPropertyControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private GeneralFluidPropertyFormService generalFluidPropertyFormService;

  @MockBean
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @MockBean
  private SubmissionDetailService submissionDetailService;

  private SubmissionDetail submissionDetail;

  private BindingResult bindingResult;

  private GeneralFluidPropertyForm generalFluidPropertyForm;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    generalFluidPropertyForm = new GeneralFluidPropertyForm();
    bindingResult = new BeanPropertyBindingResult(generalFluidPropertyForm, "form");
  }

  @Test
  void getGeneralFluidProperties_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(generalFluidPropertyFormService.getGeneralFluidPropertyForm(submissionDetail))
        .thenReturn(new GeneralFluidPropertyForm());

    mockMvc.perform(
            get(ReverseRouter.route(on(GeneralFluidPropertyController.class).getGeneralFluidProperties(submissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/generalFluidProperties"));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveGeneralFluidProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(generalFluidPropertyFormService.validate(any(), any(), any()))
        .thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(GeneralFluidPropertyController.class)
                .saveGeneralFluidProperties(submissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId()))));

    verify(generalFluidPropertyPersistenceService, times(1))
        .saveGeneralFluidProperty(any(GeneralFluidPropertyForm.class), eq(submissionDetail));
    verify(generalFluidPropertyFormService, times(1))
        .validate(any(), any(), eq(validationType));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void saveGeneralFluidProperties_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(generalFluidPropertyFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(GeneralFluidPropertyController.class)
                .saveGeneralFluidProperties(submissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(generalFluidPropertyFormService, times(1))
        .validate(any(), any(), eq(validationType));
    verify(generalFluidPropertyPersistenceService, never())
        .saveGeneralFluidProperty(any(), any());
  }

  @Test
  void getGeneralFluidPropertiesModelAndView_assertModelProperties() throws Exception {
    var form = new GeneralFluidPropertyForm();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(generalFluidPropertyFormService.getGeneralFluidPropertyForm(submissionDetail))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                get(ReverseRouter.route(on(GeneralFluidPropertyController.class)
                    .getGeneralFluidProperties(submissionDetail.getId())))
                    .with(user(USER))).andReturn().getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(GeneralFluidPropertyForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form);
  }
}