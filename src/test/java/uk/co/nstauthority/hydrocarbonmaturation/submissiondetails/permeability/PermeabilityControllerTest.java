package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

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
import org.junit.jupiter.api.BeforeAll;
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
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = PermeabilityController.class)
class PermeabilityControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private PermeabilityFormService permeabilityFormService;

  @MockBean
  private PermeabilityPersistenceService permeabilityPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getPermeabilityPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(permeabilityFormService.getPermeabilityForm(submissionDetail)).thenReturn(new PermeabilityForm());

    mockMvc.perform(get(ReverseRouter.route(on(PermeabilityController.class)
        .getPermeabilityPage(submissionDetail.getId(), null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/permeability/permeabilityDryWell"));
  }


  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void savePermeabilityDetails_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(PermeabilityForm.class, "form");
    when(permeabilityFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PermeabilityController.class)
            .savePermeabilityDetails(submissionDetail.getId(), null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId()))));

    verify(permeabilityFormService, times(1)).validate(any(), any(), eq(validationType), eq(
        SubmissionType.POST_DRILL_DRY_HOLE));
    verify(permeabilityPersistenceService, times(1))
        .savePermeabilityDetail(any(PermeabilityForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void savePermeabilityDetails_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(PermeabilityForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(permeabilityFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PermeabilityController.class)
            .savePermeabilityDetails(submissionDetail.getId(), null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(permeabilityFormService, times(1)).validate(any(), any(), eq(validationType), any());
    verify(permeabilityPersistenceService, never())
        .savePermeabilityDetail(any(), any());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY"})
  void getPermeabilityPageModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    var form = new PermeabilityForm();

    when(permeabilityFormService.getPermeabilityForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(PermeabilityController.class)
                .getPermeabilityPage(submissionDetail.getId(), null)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("methodUsed", DisplayableEnumOptionUtil.getDisplayableOptions(AverageMethod.class)),
            entry("source", DisplayableEnumOptionUtil.getDisplayableOptions(PermeabilitySource.class))
        );

    assertEquals(PermeabilityForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class)
            .getTaskList(submissionDetail.getId())), TaskListController.PAGE_NAME)
    );
  }
}