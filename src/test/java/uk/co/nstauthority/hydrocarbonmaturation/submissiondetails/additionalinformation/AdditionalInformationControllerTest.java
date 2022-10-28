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

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@WithMockUser
@ContextConfiguration(classes = AdditionalInformationController.class)
class AdditionalInformationControllerTest extends AbstractControllerTest {

  @MockBean
  private AdditionalInformationFormService additionalInformationFormService;

  @MockBean
  private SubmissionDetailService submissionDetailService;

  @MockBean
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @MockBean
  private SupportingDocumentService supportingDocumentService;

  private AdditionalInformationForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;
  private FileUploadTemplate fileUploadTemplate;

  @BeforeEach
  void setUp() {
    form = new AdditionalInformationForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    fileUploadTemplate = new FileUploadTemplate("", "", "", "", "");
  }

  @Test
  void getAdditionalInformationPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(additionalInformationFormService.getAdditionalInformationForm(submissionDetail)).thenReturn(new AdditionalInformationForm());
    when(supportingDocumentService.buildFileUploadTemplate(submissionDetail.getId(), SupportingDocumentType.ADDITIONAL_DOCUMENT))
        .thenReturn(fileUploadTemplate);

    mockMvc.perform(
            get(ReverseRouter.route(on(AdditionalInformationController.class).getAdditionalInformation(submissionDetail.getId(), form))))
        .andExpect(status().isOk());
  }


  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveAdditionalInformationPage_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(additionalInformationFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(AdditionalInformationController.class)
                .saveAdditionalInformation(submissionDetail.getId(), form, bindingResult, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetail.getId()))));

    verify(additionalInformationPersistenceService, times(1))
        .saveAdditionalInformation(any(AdditionalInformationForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveAdditionalInformationPage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(additionalInformationFormService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(supportingDocumentService.buildFileUploadTemplate(submissionDetail.getId(), SupportingDocumentType.ADDITIONAL_DOCUMENT))
        .thenReturn(fileUploadTemplate);

    mockMvc.perform(
            post(ReverseRouter.route(on(AdditionalInformationController.class)
                .saveAdditionalInformation(submissionDetail.getId(), form, bindingResult, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
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

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(AdditionalInformationController.class).getAdditionalInformation(submissionDetail.getId(), form))))
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