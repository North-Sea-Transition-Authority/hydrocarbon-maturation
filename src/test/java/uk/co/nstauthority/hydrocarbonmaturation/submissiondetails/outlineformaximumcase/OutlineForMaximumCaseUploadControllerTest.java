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
import java.util.Collections;
import java.util.List;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = OutlineForMaximumCaseUploadController.class)
class OutlineForMaximumCaseUploadControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService;

  @MockBean
  OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @MockBean
  SubmissionSupportingDocumentService supportingDocumentService;
  private final List<UploadedFileForm> existingFiles = Collections.emptyList();
  private final FileUploadComponentAttributes fileUploadComponentAttributes = new FileUploadComponentAttributes(
      "path",
      "upload_url",
      "download_url",
      "delete_url",
      "max_allowed_size",
      "allowed_extensions",
      "dropzone_link_text",
      "dropzone_text",
      "dropzone_link_screen_reader_text",
      "multi_file_error_message",
      true,
      existingFiles,
      true,
      false);
  private OutlineForMaximumCaseUploadForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    form = new OutlineForMaximumCaseUploadForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(supportingDocumentService.fileUploadComponentAttributes(eq(existingFiles), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);

    var resourceDetail = new ResourceDetail();
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .thenReturn(resourceDetail);

  }

  @SecurityTest
  void getOutlineForMaximumCasePage_assertStatusOk() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseFormService.getOutlineForMaximumCaseUploadForm(submissionDetail)).thenReturn(new OutlineForMaximumCaseUploadForm());
    when(supportingDocumentService.fileUploadComponentAttributes(anyList(), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);

    mockMvc.perform(
        get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_postDrillDryHoleSubmission_assertStatusOk() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseFormService.getOutlineForMaximumCaseUploadForm(submissionDetail)).thenReturn(new OutlineForMaximumCaseUploadForm());

    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void getOutlineForMaximumCasePage_andSubmissionIsDiscoveryOrProspect_assertStatusOk(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);

    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseFormService.getOutlineForMaximumCaseUploadForm(submissionDetail)).thenReturn(new OutlineForMaximumCaseUploadForm());

    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonAllowedSubmissionTypes")
  void getOutlineForMaximumCasePage_andSubmissionIsNonAllowedType_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_whenUserNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    when(supportingDocumentService.fileUploadComponentAttributes(anyList(), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);
    mockMvc.perform(
            post(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class)
                .saveOutlineForMaximumCaseUpload(submissionDetailId,null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(outlineForMaximumCasePersistenceService, never()).saveOutlineForMaximumCaseUploads(eq(submissionDetail), anyList());
  }

  @Test
  void getOutlineForMaximumCasePage_userHasVerifiedShapefile_assertRedirection() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    when(outlineForMaximumCaseFormService.getOutlineForMaximumCaseUploadForm(submissionDetail)).thenReturn(new OutlineForMaximumCaseUploadForm());
    when(outlineForMaximumCasePersistenceService.hasVerifiedShapefile(submissionDetail)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId))));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOutlineForMaximumCasePage_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(outlineForMaximumCaseFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).saveOutlineForMaximumCaseUpload(submissionDetailId, form, bindingResult)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(OutlineForMaximumCaseVerifyController.class).getOutlineForMaximumCaseVerify(submissionDetailId))));

  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOutlineForMaximumCasePage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(outlineForMaximumCaseFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(supportingDocumentService.fileUploadComponentAttributes(anyList(), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);

    mockMvc.perform(
        post(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).saveOutlineForMaximumCaseUpload(submissionDetailId, form, bindingResult)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

  }

  @Test
  void getOutlineForMaximumCaseModelAndView_assertModelProperties() throws Exception {
    when(outlineForMaximumCaseFormService.getOutlineForMaximumCaseUploadForm(submissionDetail)).thenReturn(new OutlineForMaximumCaseUploadForm());
    when(supportingDocumentService.fileUploadComponentAttributes(anyList(), any(FileDocumentType.class)))
        .thenReturn(fileUploadComponentAttributes);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(OutlineForMaximumCaseUploadForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
        entry("pageTitle", OutlineForMaximumCaseUploadController.PAGE_NAME),
        entry("cancelUrl",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submissionDetailId))),
        entry("isOffshore", true)
    );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
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