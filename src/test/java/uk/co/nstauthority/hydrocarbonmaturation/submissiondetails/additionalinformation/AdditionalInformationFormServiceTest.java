package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationFormServiceTest {

  @Mock
  private AdditionalInformationFormValidator additionalInformationFormValidator;

  @Mock
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @Mock
  private SupportingDocumentService supportingDocumentService;

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

    additionalInformationFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(additionalInformationFormValidator, times(1))
        .validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void additionalInformationEntityToForm_newEntity_thenEmptyForm() {
    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail)).thenReturn(Optional.empty());

    assertThat(additionalInformationFormService.getAdditionalInformationForm(submissionDetail))
        .extracting(
            AdditionalInformationForm::getAdditionalComments,
            AdditionalInformationForm::getAdditionalDocuments,
            AdditionalInformationForm::getProvidingAdditionalComments,
            AdditionalInformationForm::getProvidingAdditionalDocuments
        )
        .containsExactly(
            null,
            Collections.emptyList(),
            null,
            null
        );
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var additionalInformation = getAdditionalInformation(submissionDetail);
    var listOfUploads = List.of(getFileUploadDetail());
    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail))
        .thenReturn(Optional.of(additionalInformation));
    when(supportingDocumentService.getFileUploadDetailListForSubmissionAndType(submissionDetail,
        SupportingDocumentType.ADDITIONAL_DOCUMENT)).thenReturn(listOfUploads);


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

  @Test
  void getAdditionalDocumentsUploads_VerifyCalls() {
    FileUploadDetail fileUploadDetail1 = getFileUploadDetail();
    FileUploadDetail fileUploadDetail2 = getFileUploadDetail();

    var form = new AdditionalInformationForm();
    form.setAdditionalDocuments(List.of(fileUploadDetail1, fileUploadDetail2));

    additionalInformationFormService.getAdditionalDocumentUploads(form);
    var fileUploadIdList = FileUploadUtils.getUploadIdList(List.of(fileUploadDetail1, fileUploadDetail2));
    verify(supportingDocumentService, times(1)).getFileUploadFdsViewList(fileUploadIdList);
  }

  private AdditionalInformation getAdditionalInformation(SubmissionDetail submissionDetail) {
    var additionalInformation =  new AdditionalInformation();
    additionalInformation.setSubmissionDetail(submissionDetail);
    additionalInformation.setAdditionalComments("comments");
    additionalInformation.setHasAdditionalDocuments(true);
    additionalInformation.setHasAdditionalDocuments(true);
    return additionalInformation;
  }

  private FileUploadDetail getFileUploadDetail() {
    FileUploadDetail fileUploadDetail = new FileUploadDetail();
    fileUploadDetail.setUploadedFileId(UUID.randomUUID());
    return fileUploadDetail;
  }
}