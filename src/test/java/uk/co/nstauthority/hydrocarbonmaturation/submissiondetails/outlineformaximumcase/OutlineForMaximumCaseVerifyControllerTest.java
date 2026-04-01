package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.OpportunityTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = OutlineForMaximumCaseVerifyController.class)
class OutlineForMaximumCaseVerifyControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockitoBean
  OutlineForMaximumCaseVerifyFormService outlineForMaximumCaseVerifyFormService;

  @MockitoBean
  OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @MockitoBean
  OutlineForMaximumCaseMapService outlineForMaximumCaseMapService;

  @Autowired
  private ApplicationContext applicationContext;
  private OutlineForMaximumCaseVerifyForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;
  private String mapAttributions;
  private String mapGeoJsonDataRoute;

  @BeforeEach
  void setUp() {
    form = new OutlineForMaximumCaseVerifyForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mapAttributions = "Contains information provided by the %s"
        .formatted(applicationContext.getBean(CustomerConfigurationProperties.class).mnemonic());
    mapGeoJsonDataRoute = ReverseRouter.route(on(OutlineForMaximumCaseRestController.class)
        .getGeoJsonForOutlineForMaximumCase(submissionDetailId, null));
    when(outlineForMaximumCaseMapService.getMapAttributions()).thenReturn(mapAttributions);
    when(outlineForMaximumCaseMapService.getMapGeoJsonDataRoute(submissionDetail)).thenReturn(mapGeoJsonDataRoute);
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_assertStatusOk() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseVerifyFormService.getOutlineForMaximumCaseVerifyForm(submissionDetail)).thenReturn(form);

    mockMvc.perform(
        get(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void getOutlineForMaximumCasePage_discoveryOrProspectSubmission_assertStatusOk(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseVerifyFormService.getOutlineForMaximumCaseVerifyForm(submissionDetail)).thenReturn(form);

    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonAllowedSubmissionTypes")
  void getOutlineForMaximumCasePage_andStatusIsNotDraft_assertForbidden(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_andSubmissionIsField_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_whenUserNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class)
                .saveOutlineForMaximumCaseUpload(submissionDetailId,null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(outlineForMaximumCasePersistenceService, never()).saveOutlineForMaximumCaseUploads(eq(submissionDetail), anyList());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOutlineForMaximumCasePage_whenValidFormAndShapeIsCorrect_assertRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseVerifyFormService.getShapeIsCorrect(any(OutlineForMaximumCaseVerifyForm.class))).thenReturn(true);
    when(outlineForMaximumCaseVerifyFormService.validate(any(), any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).saveOutlineForMaximumCaseUpload(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOutlineForMaximumCasePage_whenValidFormAndShapeIsNotCorrect_assertRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseVerifyFormService.getShapeIsCorrect(any(OutlineForMaximumCaseVerifyForm.class))).thenReturn(false);
    when(outlineForMaximumCaseVerifyFormService.validate(any(), any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).saveOutlineForMaximumCaseUpload(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, null))));

  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOutlineForMaximumCasePage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(outlineForMaximumCaseVerifyFormService.validate(any(), any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).saveOutlineForMaximumCaseUpload(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

  }

  @Test
  void getOutlineForMaximumCaseModelAndView_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseVerifyFormService.getOutlineForMaximumCaseVerifyForm(submissionDetail)).thenReturn(form);
    
    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(OutlineForMaximumCaseVerifyForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
        entry("pageTitle", OutlineForMaximumCaseVerifyController.PAGE_NAME),
        entry("cancelUrl",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submissionDetailId, null))),
        entry("geoJsonDataUrl",
            mapGeoJsonDataRoute),
        entry("dataMapAttributions", mapAttributions)
    );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class)
                .getOutlineForMaximumCaseUpload(submissionDetail.getId(), null)),
            OpportunityTaskListSectionService.OUTLINE_FOR_MAXIMUM_CASE)
    );
  }

  private static Stream<Arguments> allDiscoveryAndProspectSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> SubmissionDetailUtil.isDiscoverySubmission(submissionType)
            || SubmissionDetailUtil.isProspectSubmission(submissionType))
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonAllowedSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isDiscoverySubmission(submissionType)
            && !SubmissionDetailUtil.isProspectSubmission(submissionType)
            && !SubmissionType.POST_DRILL_DRY_HOLE.equals(submissionType))
        .map(Arguments::of);
  }
}