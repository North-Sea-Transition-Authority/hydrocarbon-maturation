package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSummaryService.FILE_ROW_KEY;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationSummaryServiceTest {

  @Mock
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  @InjectMocks
  private AdditionalInformationSummaryService additionalInformationSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

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
            List.of(SummaryFileViewRow.empty(FILE_ROW_KEY))
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
            List.of()
        );
  }

  @Test
  void getAdditionalInformationSummaryFromSubmissionDetail_whenAdditionalInformation() {
    var additionalInformation = new AdditionalInformation();
    additionalInformation.setAdditionalComments("comment");
    additionalInformation.setHasAdditionalDocuments(true);
    additionalInformation.setHasAdditionalComments(true);

    var fileUploadForm = new UploadedFileForm();
    fileUploadForm.setUploadedFileDescription("description");
    var uploadedFile = new UploadedFile();
    uploadedFile.setName("uploadedfile.txt");
    uploadedFile.setDescription("description");

    when(additionalInformationPersistenceService.getAdditionalInformation(submissionDetail))
        .thenReturn(Optional.of(additionalInformation));
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.ADDITIONAL_DOCUMENT)).thenReturn(List.of(uploadedFile));

    var fileViews = FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(
        FILE_ROW_KEY,
        List.of(uploadedFile),
        submissionDetail
    );

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
            fileViews
        );
  }
}