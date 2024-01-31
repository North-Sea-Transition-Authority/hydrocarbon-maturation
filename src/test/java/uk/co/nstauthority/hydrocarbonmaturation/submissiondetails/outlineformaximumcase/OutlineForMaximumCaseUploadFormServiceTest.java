package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseUploadFormServiceTest {

  @Mock
  private OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseFormValidator;
  @Mock
  private SubmissionSupportingDocumentService submissionSupportingDocumentService;
  @Mock
  private OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  @InjectMocks
  private OutlineForMaximumCaseUploadFormService underTest;

  @Nested
  class WhenGettingOutlineForMaximumCaseUploadForm {
    @Mock
    private SubmissionDetail submissionDetail;
    private static final FileDocumentType FILE_DOCUMENT_TYPE = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;

    @Test
    void shouldReturnFormWithOutlineForMaximumCaseFileUploads() {
      var uploadedFileForm = mock(UploadedFileForm.class);
      var outlineForMaximumCaseFileUploads = List.of(uploadedFileForm);

      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail))
          .thenReturn(FILE_DOCUMENT_TYPE);
      when(submissionSupportingDocumentService.getUploadedFileForms(submissionDetail, FILE_DOCUMENT_TYPE))
          .thenReturn(outlineForMaximumCaseFileUploads);

      var result = underTest.getOutlineForMaximumCaseUploadForm(submissionDetail);

      assertThat(result.getOutlineForMaximumCase()).isEqualTo(outlineForMaximumCaseFileUploads);
    }
  }

  @Nested
  class WhenValidating {
    private final ValidationType VALIDATION_TYPE = ValidationType.FULL;
    @Mock
    private SubmissionForm form;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private SubmissionDetail submissionDetail;

    @Test
    void verifyMethodCall() {
      var argument = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

      var result = underTest.validate(form, bindingResult, VALIDATION_TYPE, submissionDetail);
      verify(outlineForMaximumCaseFormValidator).validate(eq(form), eq(bindingResult), argument.capture());

      assertThat(result).isEqualTo(bindingResult);
      assertThat(argument.getValue())
          .extracting(
              SubmissionFormValidatorHints::getSubmissionDetail,
              SubmissionFormValidatorHints::getValidationType
          )
          .containsExactly(
              submissionDetail,
              VALIDATION_TYPE
          );
    }
  }
}