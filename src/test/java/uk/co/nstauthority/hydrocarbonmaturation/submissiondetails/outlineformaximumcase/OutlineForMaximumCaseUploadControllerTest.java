package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = OutlineForMaximumCaseUploadController.class)
class OutlineForMaximumCaseUploadControllerTest extends AbstractSubmissionControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  OutlineForMaximumCaseUploadFormService outlineForMaximumCaseFormService;

  @MockBean
  OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @MockBean
  SupportingDocumentService supportingDocumentService;

  public static final FileUploadTemplate fileUploadTemplate = new FileUploadTemplate("download_url", "upload_url", "delete_url",
      "max_allowed_size", OutlineForMaximumCaseUploadFormService.ALLOWED_EXTENSIONS);
  private OutlineForMaximumCaseUploadForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    form = new OutlineForMaximumCaseUploadForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    when(outlineForMaximumCaseFormService.getAllowedExtensions()).thenReturn(OutlineForMaximumCaseUploadFormService.ALLOWED_EXTENSIONS);
    when(supportingDocumentService.buildFileUploadTemplate(submissionDetailId, SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE, OutlineForMaximumCaseUploadFormService.ALLOWED_EXTENSIONS))
        .thenReturn(fileUploadTemplate);

  }

  @SecurityTest
  void getOutlineForMaximumCasePage_assertStatusOk() throws Exception {
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
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_andSubmissionIsField_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class).getOutlineForMaximumCaseUpload(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOutlineForMaximumCasePage_whenUserNotResponsibleOrganisation_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(OutlineForMaximumCaseUploadController.class)
                .saveOutlineForMaximumCaseUpload(submissionDetailId,null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(outlineForMaximumCasePersistenceService, never()).saveOutlineForMaximumCaseUploadDescriptions(any());
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
                .getTaskList(submissionDetailId)))
    )
    .containsKeys("breadcrumbsMap");
  }
}