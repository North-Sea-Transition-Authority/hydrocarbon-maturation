package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationFormServiceTest {

  @Mock
  private AdditionalInformationFormValidator additionalInformationFormValidator;

  @Mock
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  @InjectMocks
  private AdditionalInformationFormService additionalInformationFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new AdditionalInformationForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    additionalInformationFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail, null);

    verify(additionalInformationFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());
    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void additionalInformationEntityToForm_newEntity_thenEmptyForm() {
    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail)).thenReturn(Optional.empty());
    var listOfUploads = List.of(getUploadedFileForm());
    when(supportingDocumentService.getUploadedFileForms(submissionDetail,
        FileDocumentType.ADDITIONAL_DOCUMENT)).thenReturn(listOfUploads);

    assertThat(additionalInformationFormService.getAdditionalInformationForm(submissionDetail))
        .extracting(
            AdditionalInformationForm::getAdditionalComments,
            AdditionalInformationForm::getAdditionalDocuments,
            AdditionalInformationForm::getProvidingAdditionalComments,
            AdditionalInformationForm::getProvidingAdditionalDocuments
        )
        .containsExactly(
            null,
            listOfUploads,
            null,
            null
        );
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var additionalInformation = getAdditionalInformation(submissionDetail);
    var listOfUploads = List.of(getUploadedFileForm());
    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail))
        .thenReturn(Optional.of(additionalInformation));
    when(supportingDocumentService.getUploadedFileForms(submissionDetail,
        FileDocumentType.ADDITIONAL_DOCUMENT)).thenReturn(listOfUploads);


    assertThat(additionalInformationFormService.getAdditionalInformationForm(submissionDetail))
        .extracting(
            AdditionalInformationForm::getAdditionalComments,
            AdditionalInformationForm::getAdditionalDocuments,
            AdditionalInformationForm::getProvidingAdditionalComments,
            AdditionalInformationForm::getProvidingAdditionalDocuments
        )
        .containsExactly(
            additionalInformation.getAdditionalComments(),
            listOfUploads,
            additionalInformation.getHasAdditionalComments(),
            additionalInformation.getHasAdditionalDocuments()
        );
  }

  private AdditionalInformation getAdditionalInformation(SubmissionDetail submissionDetail) {
    var additionalInformation =  new AdditionalInformation();
    additionalInformation.setSubmissionDetail(submissionDetail);
    additionalInformation.setAdditionalComments("comments");
    additionalInformation.setHasAdditionalDocuments(true);
    additionalInformation.setHasAdditionalDocuments(true);
    return additionalInformation;
  }

  private UploadedFileForm getUploadedFileForm() {
    var fileUploadForm = new UploadedFileForm();
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    return fileUploadForm;
  }
}