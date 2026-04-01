package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FieldTrapPropertiesController.class)
class FieldTrapPropertiesControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private FieldTrapPropertiesFormService fieldTrapPropertiesFormService;

  @MockitoBean
  private FieldTrapPropertiesFormValidator fieldTrapPropertiesFormValidator;

  @MockitoBean
  private QuadrantAndBlockService quadrantAndBlockService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  private FieldTrapPropertiesForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    form = new FieldTrapPropertiesForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetail(submissionDetailId)).thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
  }

  @SecurityTest
  void getFieldTrapProperties_whenUserIsResponsibleOrganisation_assertOk() throws Exception {
    when(fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail)).thenReturn(new FieldTrapPropertiesForm());
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(FieldTrapPropertiesController.class).getFieldTrapProperties(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fieldTrapProperties"));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      names = {"DRAFT"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void getFieldTrapProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldTrapPropertiesController.class).getFieldTrapProperties(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void getFieldTrapProperties_andTypeIsNotField_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldTrapPropertiesController.class).getFieldTrapProperties(
                submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFieldTrapProperties_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(FieldTrapPropertiesController.class)
                .getFieldTrapProperties(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFieldTrapProperties_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(FieldTrapPropertiesController.class).saveFieldTrapProperties(
                submissionDetailId,  null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getFieldTrapProperties_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    when(fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail)).thenReturn(form);

    var quadrantAndBlocks = mock(Map.class);
    when(quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail))
        .thenReturn(quadrantAndBlocks);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(FieldTrapPropertiesController.class)
                .getFieldTrapProperties(submissionDetailId)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    @SuppressWarnings(value = "unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));
    assertThat(breadcrumbsMap.entrySet())
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
            entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME)
        );

    assertThat(modelMap)
        .contains(
            entry("form", form),
            entry("pageTitle", FieldTrapPropertiesController.TRAP_PROPERTIES),
            entry("trapTypes", TrapType.getTrapTypes()),
            entry("quadrantAndBlocks", quadrantAndBlocks),

            entry("depthToCrestError", ProbabilityDistributionTypeError.emptyError()),
            entry("depthToSpillPointError", ProbabilityDistributionTypeError.emptyError()),
            entry("productiveAreaError", ProbabilityDistributionTypeError.emptyError()),

            entry("depthToCrestTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD)),
            entry("depthToSpillPointTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD)),
            entry("productiveAreaTemplate",
                probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD))
        );
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFieldTrapProperties_whenValidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(fieldTrapPropertiesFormValidator.isValid(
        any(FieldTrapPropertiesForm.class),
        any(Errors.class),
        eq(submissionDetail),
        eq(validationType))
    ).thenReturn(false);

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldTrapPropertiesController.class)
                .saveFieldTrapProperties(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fieldTrapProperties"));

    verify(fieldTrapPropertiesFormService, never())
        .saveFieldTrapProperties(any(FieldTrapPropertiesForm.class), any(SubmissionDetail.class));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFieldTrapProperties_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(fieldTrapPropertiesFormValidator.isValid(
        any(OilCaseOutputsForm.class),
        any(Errors.class),
        eq(submissionDetail),
        eq(validationType))
    ).thenReturn(false);

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(FieldTrapPropertiesController.class)
                .saveFieldTrapProperties(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(fieldTrapPropertiesFormService, never())
        .saveFieldTrapProperties(any(FieldTrapPropertiesForm.class), any(SubmissionDetail.class));
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}