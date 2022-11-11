package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationSummaryServiceTest {

  @Mock
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @Mock
  private FileUploadService fileUploadService;

  @Mock
  private SupportingDocumentService supportingDocumentService;

  @InjectMocks
  private AdditionalInformationSummaryService additionalInformationSummaryService;

  private SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Test
  void getAdditionalInformationSummaryFromSubmissionDetail_whenNoAdditionalInformation() {
    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail))
        .thenReturn(Optional.empty());

    var additionalInformationSummaryView =
        additionalInformationSummaryService.getAdditionalInformationSummaryFromSubmissionDetail(submissionDetail);

    assertThat(additionalInformationSummaryView)
        .extracting(
            AdditionalInformationSummaryView::providingAdditionalComments,
            AdditionalInformationSummaryView::additionalComments,
            AdditionalInformationSummaryView::providingAdditionalDocuments,
            AdditionalInformationSummaryView::additionalDocuments)
        .containsExactly(
            "",
            "",
            "",
            Collections.emptyMap()
        );
  }

  @Test
  void getAdditionalInformationSummaryFromSubmissionDetail_whenAdditionalInformation_whenNo() {
    var additionalInformation = new AdditionalInformation();
    additionalInformation.setHasAdditionalDocuments(false);
    additionalInformation.setHasAdditionalComments(false);

    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail))
        .thenReturn(Optional.of(additionalInformation));

    var additionalInformationSummaryView =
        additionalInformationSummaryService.getAdditionalInformationSummaryFromSubmissionDetail(submissionDetail);

    assertThat(additionalInformationSummaryView)
        .extracting(
            AdditionalInformationSummaryView::providingAdditionalComments,
            AdditionalInformationSummaryView::additionalComments,
            AdditionalInformationSummaryView::providingAdditionalDocuments,
            AdditionalInformationSummaryView::additionalDocuments)
        .containsExactly(
            "No",
            "",
            "No",
            Map.of()
        );
  }

  @Test
  void getAdditionalInformationSummaryFromSubmissionDetail_whenAdditionalInformation() {
    var additionalInformation = new AdditionalInformation();
    additionalInformation.setAdditionalComments("comment");
    additionalInformation.setHasAdditionalDocuments(true);
    additionalInformation.setHasAdditionalComments(true);

    FileUploadForm fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileDescription("description");
    var uploadedFile = new UploadedFile();
    uploadedFile.setFilename("uploadedfile.txt");
    uploadedFile.setDescription("description");

    when(fileUploadService.findUploadedFileOrThrow(fileUploadForm.getUploadedFileId()))
        .thenReturn(uploadedFile);
    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail))
        .thenReturn(Optional.of(additionalInformation));
    when(supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail,
        SupportingDocumentType.ADDITIONAL_DOCUMENT)).thenReturn(List.of(fileUploadForm));

    var fileMap = new HashMap<UploadedFile, String>();
    fileMap.put(uploadedFile, ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
        .download(submissionDetail.getId(), fileUploadForm.getUploadedFileId())));

    var additionalInformationSummaryView =
        additionalInformationSummaryService.getAdditionalInformationSummaryFromSubmissionDetail(submissionDetail);

    assertThat(additionalInformationSummaryView)
        .extracting(
            AdditionalInformationSummaryView::providingAdditionalComments,
            AdditionalInformationSummaryView::additionalComments,
            AdditionalInformationSummaryView::providingAdditionalDocuments,
            AdditionalInformationSummaryView::additionalDocuments)
        .containsExactly(
            "Yes",
            "comment",
            "Yes",
            fileMap
        );
  }
}