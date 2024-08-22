package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FieldComplexityController.class)
class FieldComplexityControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private FieldComplexityService fieldComplexityService;

  @MockBean
  private FieldComplexityFormValidator fieldComplexityFormValidator;

  private FieldComplexityForm form;
  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    form = new FieldComplexityForm();
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
  }

  @SecurityTest
  void getFieldComplexity_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(fieldComplexityService.submissionDetailToForm(submissionDetail)).thenReturn(new FieldComplexityForm());
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(FieldComplexityController.class).getFieldComplexity(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fieldComplexity"));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      names = "DRAFT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getFieldComplexity_andStatusIsNotDraft_assertForbidden(SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldComplexityController.class).getFieldComplexity(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      names = "DRAFT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void saveFieldComplexity_andStatusIsNotDraft_assertForbidden(SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldComplexityController.class).getFieldComplexity(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void getFieldComplexity_andSubmissionNotFieldType_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldComplexityController.class).getFieldComplexity(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void saveFieldComplexity_andSubmissionNotFieldType_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            post(ReverseRouter.route(on(FieldComplexityController.class)
                .saveFieldComplexity(submissionDetailId, null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFieldComplexity_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldComplexityController.class)
                .getFieldComplexity(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFieldComplexity_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(FieldComplexityController.class).saveFieldComplexity(
                submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void saveFieldComplexity_validForm (ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(fieldComplexityFormValidator.isValid(
        any(FieldComplexityForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(validationType))
    ).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldComplexityController.class)
                .saveFieldComplexity(submissionDetailId, form, ReverseRouter.emptyBindingResult(), validationType)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

    verify(fieldComplexityService, times(1))
        .saveFieldComplexity(any(FieldComplexityForm.class), eq(submissionDetail));
    verifyNoMoreInteractions(fieldComplexityService);
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void saveFieldComplexity_invalidForm (ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(fieldComplexityFormValidator.isValid(
        any(FieldComplexityForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        any(ValidationType.class))
    ).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldComplexityController.class)
                .saveFieldComplexity(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("remi/submissiondetails/fieldComplexity"));

    verifyNoInteractions(fieldComplexityService);
  }

  @Test
  void getFieldComplexity_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(fieldComplexityService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(FieldComplexityController.class).getFieldComplexity(submissionDetailId)))
                .with(user(USER))))
        .andReturn()
        .getModelAndView()
        .getModelMap();

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME)
        );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("pageTitle", FieldComplexityController.FIELD_COMPLEXITY),
            entry("viscosityRangeMap",
                DisplayableEnumOptionUtil.getDisplayableOptions(ViscosityRange.class)),
            entry("verticalHeterogeneityRangeMap",
                DisplayableEnumOptionUtil.getDisplayableOptions(VerticalHeterogeneityRange.class)),
            entry("arealDensityRangeMap",
                DisplayableEnumOptionUtil.getDisplayableOptions(ArealDensityRange.class)),
            entry("structuralComplexityRangeMap",
                DisplayableEnumOptionUtil.getDisplayableOptions(StructuralComplexityRange.class))
        );
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}