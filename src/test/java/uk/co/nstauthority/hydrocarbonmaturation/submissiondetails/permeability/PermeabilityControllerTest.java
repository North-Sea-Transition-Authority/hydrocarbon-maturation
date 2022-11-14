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

import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = PermeabilityController.class)
class PermeabilityControllerTest extends AbstractControllerTest {

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private PermeabilityDryWellFormService permeabilityDryWellFormService;

  @MockBean
  private PermeabilityPersistenceService permeabilityPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
  }

  @Test
  void getPermeabilityPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(permeabilityDryWellFormService.getPermeabilityForm(submissionDetail)).thenReturn(new PermeabilityDryWellForm());

    mockMvc.perform(get(ReverseRouter.route(on(PermeabilityController.class)
        .getPermeabilityPage(submissionDetail.getId(), null))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/permeabilityDryWell"));
  }


  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void savePermeabilityDetails_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(PermeabilityDryWellForm.class, "form");
    when(permeabilityDryWellFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PermeabilityController.class)
            .savePermeabilityDetails(submissionDetail.getId(), null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(PostDrillDryHoleTaskListController.class)
            .getTaskList(submissionDetail.getId()))));

    verify(permeabilityDryWellFormService, times(1)).validate(any(), any(), eq(validationType));
    verify(permeabilityPersistenceService, times(1))
        .savePermeabilityDetail(any(PermeabilityDryWellForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void savePermeabilityDetails_whenInValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);

    var bindingResult = new BeanPropertyBindingResult(PermeabilityDryWellForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(permeabilityDryWellFormService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(PermeabilityController.class)
            .savePermeabilityDetails(submissionDetail.getId(), null, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(permeabilityDryWellFormService, times(1)).validate(any(), any(), eq(validationType));
    verify(permeabilityPersistenceService, never())
        .savePermeabilityDetail(any(), any());
  }

  @Test
  void getPermeabilityPageModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    var form = new PermeabilityDryWellForm();

    when(permeabilityDryWellFormService.getPermeabilityForm(submissionDetail)).thenReturn(form);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(PermeabilityController.class)
                .getPermeabilityPage(submissionDetail.getId(), null))))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("methodUsed", DisplayableEnumOptionUtil.getDisplayableOptions(AverageMethod.class)),
            entry("source", DisplayableEnumOptionUtil.getDisplayableOptions(PermeabilitySource.class))
        );

    assertEquals(PermeabilityDryWellForm.class, modelMap.get("form").getClass());

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(PostDrillDryHoleTaskListController.class)
            .getTaskList(submissionDetail.getId())), PostDrillDryHoleTaskListController.PAGE_NAME)
    );
  }
}