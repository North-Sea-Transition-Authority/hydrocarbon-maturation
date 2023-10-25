package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

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
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Disabled
@ContextConfiguration(classes = AdditionalInformationController.class)
class AdditionalInformationControllerTest extends AbstractSubmissionControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private AdditionalInformationFormService additionalInformationFormService;

  @MockBean
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @MockBean
  private SupportingDocumentService supportingDocumentService;

  private AdditionalInformationForm form;
  private BindingResult bindingResult;
  private FileUploadTemplate fileUploadTemplate;

  @BeforeEach
  void setUp() {
    form = new AdditionalInformationForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    fileUploadTemplate = new FileUploadTemplate("", "", "", "", "");
  }

  @SecurityTest
  void getAdditionalInformationPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(additionalInformationFormService.getAdditionalInformationForm(submissionDetail)).thenReturn(new AdditionalInformationForm());
    when(supportingDocumentService.buildFileUploadTemplate(submissionDetail.getId(), SupportingDocumentType.ADDITIONAL_DOCUMENT))
        .thenReturn(fileUploadTemplate);
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(AdditionalInformationController.class).getAdditionalInformation(submissionDetail.getId(), form)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getAdditionalInformationPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(AdditionalInformationController.class).getAdditionalInformation(submissionDetail.getId(), form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }


  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveAdditionalInformationPage_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(additionalInformationFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(AdditionalInformationController.class)
                .saveAdditionalInformation(submissionDetail.getId(), form, bindingResult, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId()))));

    verify(additionalInformationPersistenceService, times(1))
        .saveAdditionalInformation(any(AdditionalInformationForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveAdditionalInformationPage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(additionalInformationFormService.validate(any(), any(), any(), any())).thenReturn(bindingResult);
    when(supportingDocumentService.buildFileUploadTemplate(submissionDetail.getId(), SupportingDocumentType.ADDITIONAL_DOCUMENT))
        .thenReturn(fileUploadTemplate);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(AdditionalInformationController.class)
                .saveAdditionalInformation(submissionDetail.getId(), form, bindingResult, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(additionalInformationPersistenceService, never()).saveAdditionalInformation(any(), eq(submissionDetail));
  }

  @Test
  void getAdditionalInformationModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(additionalInformationFormService.getAdditionalInformationForm(submissionDetail))
        .thenReturn(new AdditionalInformationForm());
    when(supportingDocumentService.buildFileUploadTemplate(submissionDetail.getId(), SupportingDocumentType.ADDITIONAL_DOCUMENT))
        .thenReturn(fileUploadTemplate);

    ModelMap modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(AdditionalInformationController.class).getAdditionalInformation(submissionDetail.getId(), form)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(AdditionalInformationForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("pageTitle", AdditionalInformationController.PAGE_NAME),
            entry("additionalDocumentTemplate", fileUploadTemplate))
        .containsKeys("breadcrumbsMap", "additionalDocumentUploads");
  }
}