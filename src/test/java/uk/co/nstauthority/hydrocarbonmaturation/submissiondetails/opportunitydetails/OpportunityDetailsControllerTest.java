package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Collections;
import java.util.List;
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
import uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = OpportunityDetailsController.class)
class OpportunityDetailsControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  OpportunityDetailsFormService opportunityDetailsFormService;

  @MockBean
  OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  @MockBean
  SubmissionSupportingDocumentService supportingDocumentService;

  private OpportunityDetailsForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;
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

  @BeforeEach
  void setUp() {
    form = new OpportunityDetailsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getOpportunityDetailsPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail)).thenReturn(new OpportunityDetailsForm());
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetailId, form)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getOpportunityDetailsPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOpportunityDetailsPage_andSubmissionIsField_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    mockMvc.perform(
            get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getOpportunityDetailsPage_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(OpportunityDetailsController.class)
                .getOpportunityDetails(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveOpportunityDetailsPage_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(
                submissionDetailId, null, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOpportunityDetailsPage_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(opportunityDetailsFormService.validate(any(), any(), any(), eq(submissionDetail), eq(USER))).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(submissionDetailId, form, bindingResult, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

    verify(opportunityDetailsPersistenceService, times(1)).saveOpportunityDetails(any(OpportunityDetailsForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveOpportunityDetailsPage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(opportunityDetailsFormService.validate(any(), any(), any(), eq(submissionDetail), eq(USER))).thenReturn(bindingResult);
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);
    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(submissionDetailId, form, bindingResult, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(opportunityDetailsPersistenceService, never()).saveOpportunityDetails(any(), eq(submissionDetail));
  }

  @Test
  void getOpportunityDetailsModelAndViewForProsopect_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail)).thenReturn(new OpportunityDetailsForm());
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetailId, form)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(OpportunityDetailsForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
        entry("opportunityTypes", OpportunityType.getSelectableOpportunityTypes()),
        entry("pageTitle", OpportunityDetailsController.PAGE_NAME),
        entry("mapName", "Map of opportunity")
    )
    .containsKeys("breadcrumbsMap", "mapOfOpportunityTemplate");
  }

  @Test
  void getOpportunityDetailsModelAndViewForDiscovery_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail)).thenReturn(new OpportunityDetailsForm());
    when(supportingDocumentService.submissionFileUploadComponentAttributes(anyList(), any(FileDocumentType.class), eq(submissionDetail)))
        .thenReturn(fileUploadComponentAttributes);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetailId, form)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(OpportunityDetailsForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
            entry("opportunityTypes", OpportunityType.getSelectableOpportunityTypes()),
            entry("pageTitle", OpportunityDetailsController.PAGE_NAME),
            entry("mapName", "Map of discovery")
        )
        .containsKeys("breadcrumbsMap", "mapOfOpportunityTemplate");
  }
}