package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationPersistenceServiceTest {

  @Mock
  private AdditionalInformationRepository additionalInformationRepository;

  @Mock
  private SupportingDocumentService supportingDocumentService;

  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    additionalInformationPersistenceService = new AdditionalInformationPersistenceService(additionalInformationRepository,
        supportingDocumentService);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveAdditionalInformation_whenGivenAFormYesAnswers_verifyCreatedEntity() {
    var form = new AdditionalInformationForm();
    form.setAdditionalComments("comments");
    form.setProvidingAdditionalDocuments(true);
    form.setProvidingAdditionalComments(true);

    var additionalInformationArgumentCaptor = ArgumentCaptor.forClass(AdditionalInformation.class);

    additionalInformationPersistenceService.saveAdditionalInformation(form, submissionDetail);

    verify(additionalInformationRepository, times(1)).save(additionalInformationArgumentCaptor.capture());
    var savedAdditionalInformation = (AdditionalInformation) additionalInformationArgumentCaptor.getValue();

    assertThat(savedAdditionalInformation)
        .extracting(
            AdditionalInformation::getAdditionalComments,
            AdditionalInformation::getHasAdditionalComments,
            AdditionalInformation::getHasAdditionalDocuments
        )
        .containsExactly(
            form.getAdditionalComments(),
            form.getProvidingAdditionalComments(),
            form.getProvidingAdditionalDocuments()
        );
  }

  @Test
  void saveAdditionalInformation_whenGivenAFormNoAnswers_verifyCreatedEntity() {
    var form = new AdditionalInformationForm();
    form.setProvidingAdditionalDocuments(false);
    form.setProvidingAdditionalComments(false);

    var additionalInformationArgumentCaptor = ArgumentCaptor.forClass(AdditionalInformation.class);

    additionalInformationPersistenceService.saveAdditionalInformation(form, submissionDetail);

    verify(additionalInformationRepository, times(1)).save(additionalInformationArgumentCaptor.capture());
    var savedAdditionalInformation = (AdditionalInformation) additionalInformationArgumentCaptor.getValue();

    assertThat(savedAdditionalInformation)
        .extracting(
            AdditionalInformation::getAdditionalComments,
            AdditionalInformation::getHasAdditionalComments,
            AdditionalInformation::getHasAdditionalDocuments
        )
        .containsExactly(
            form.getAdditionalComments(),
            form.getProvidingAdditionalComments(),
            form.getProvidingAdditionalDocuments()
        );
  }

  @Test
  void saveAdditionalInformation_whenGivenAFormUpload_verifyFileUpload() {
    var form = new AdditionalInformationForm();
    form.setProvidingAdditionalDocuments(true);
    var fileUploadForm = new FileUploadForm();
    form.setAdditionalDocuments(List.of(fileUploadForm));

    additionalInformationPersistenceService.saveAdditionalInformation(form, submissionDetail);

    verify(supportingDocumentService, times(1))
        .updateSupportingDocumentFileDescriptions(form.getAdditionalDocuments());
  }

  @Test
  void saveAdditionalInformation_whenGivenAFormNoUpload_verifyFileDelete() {
    var form = new AdditionalInformationForm();
    form.setProvidingAdditionalDocuments(false);
    var fileUploadForm = new FileUploadForm();

    when(supportingDocumentService.getFileUploadFormListForSubmissionAndType(submissionDetail, SupportingDocumentType.ADDITIONAL_DOCUMENT))
        .thenReturn(List.of(fileUploadForm));

    additionalInformationPersistenceService.saveAdditionalInformation(form, submissionDetail);

    verify(supportingDocumentService, times(1))
        .deleteFile(submissionDetail, fileUploadForm.getUploadedFileId());
  }


  @Test
  void getAdditionalInformation_verifyMethodCall() {
    additionalInformationPersistenceService.getAdditionalInformation(submissionDetail);
    verify(additionalInformationRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}