package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsSummaryServiceTest {

  @Mock
  private OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  @Mock
  private FileService fileService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  @InjectMocks
  private OpportunityDetailsSummaryService opportunityDetailsSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Test
  void getOpportunityDetailsSummaryFromSubmissionDetail_whenNoOpportunityDetails() {
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var opportunityDetailsSummaryView =
        opportunityDetailsSummaryService.getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);

    assertThat(opportunityDetailsSummaryView)
        .extracting(
            OpportunityDetailsSummaryView::opportunityType,
            OpportunityDetailsSummaryView::otherOpportunity,
            OpportunityDetailsSummaryView::otherOpportunityType,
            OpportunityDetailsSummaryView::opportunityComments,
            OpportunityDetailsSummaryView::keyEvaluationWork,
            OpportunityDetailsSummaryView::keyWorkRequired,
            OpportunityDetailsSummaryView::mapOfOpportunity,
            OpportunityDetailsSummaryView::representativeSeismicSection,
            OpportunityDetailsSummaryView::geologicalCrossSection)
        .containsExactly(
            "",
            false,
            "",
            "",
            "",
            "",
            Collections.emptyMap(),
            Collections.emptyMap(),
            Collections.emptyMap()
        );
  }

  @Test
  void getOpportunityDetailsSummaryFromSubmissionDetail_whenOpportunityDetailsPresent() {
    var opportunityDetails = new OpportunityDetail();
    opportunityDetails.setSubmissionDetail(submissionDetail);
    opportunityDetails.setOpportunityComments("comments");
    opportunityDetails.setOpportunityType(OpportunityType.OTHER);
    opportunityDetails.setOtherTypeOfOpportunity("other stuff");
    opportunityDetails.setKeyEvaluationWorkCarriedOut("evaluation");
    opportunityDetails.setKeyWorkRequiredForFurtherMaturation("work required");
    UploadedFileForm fileUploadDetail = new UploadedFileForm();
    fileUploadDetail.setUploadedFileDescription("description");

    var uploadedFile = new UploadedFile();
    uploadedFile.setName("uploadedfile.txt");
    uploadedFile.setDescription("description");

    var fileMap = new HashMap<UploadedFile, String>();
    fileMap.put(uploadedFile, ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
        .download(submissionDetail.getId(), fileUploadDetail.getUploadedFileId())));

    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail))
        .thenReturn(Optional.of(opportunityDetails));
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.MAP_OF_OPPORTUNITY)).thenReturn(List.of(uploadedFile));
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.GEOLOGICAL_CROSS_SECTION)).thenReturn(List.of(uploadedFile));
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION)).thenReturn(List.of(uploadedFile));

    var opportunityDetailsSummaryView =
        opportunityDetailsSummaryService.getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);

    assertThat(opportunityDetailsSummaryView)
        .extracting(
            OpportunityDetailsSummaryView::opportunityType,
            OpportunityDetailsSummaryView::otherOpportunity,
            OpportunityDetailsSummaryView::otherOpportunityType,
            OpportunityDetailsSummaryView::opportunityComments,
            OpportunityDetailsSummaryView::keyEvaluationWork,
            OpportunityDetailsSummaryView::keyWorkRequired,
            OpportunityDetailsSummaryView::mapOfOpportunity,
            OpportunityDetailsSummaryView::representativeSeismicSection,
            OpportunityDetailsSummaryView::geologicalCrossSection)
        .containsExactly(
            OpportunityType.OTHER.getDisplayName(),
            true,
            "other stuff",
            "comments",
            "evaluation",
            "work required",
            fileMap,
            fileMap,
            fileMap
        );
  }

  @Test
  void getOpportunityDetailsSummaryFromSubmissionDetail_whenEmptyOpportunityDetailsPresent() {
    var opportunityDetails = new OpportunityDetail();
    var fileMap = new HashMap<UploadedFile, String>();
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail))
        .thenReturn(Optional.of(opportunityDetails));
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.MAP_OF_OPPORTUNITY)).thenReturn(List.of());
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.GEOLOGICAL_CROSS_SECTION)).thenReturn(List.of());
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION))
        .thenReturn(List.of());
    var opportunityDetailsSummaryView =
        opportunityDetailsSummaryService.getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);

    assertThat(opportunityDetailsSummaryView)
        .extracting(
            OpportunityDetailsSummaryView::opportunityType,
            OpportunityDetailsSummaryView::otherOpportunity,
            OpportunityDetailsSummaryView::otherOpportunityType,
            OpportunityDetailsSummaryView::opportunityComments,
            OpportunityDetailsSummaryView::keyEvaluationWork,
            OpportunityDetailsSummaryView::keyWorkRequired,
            OpportunityDetailsSummaryView::mapOfOpportunity,
            OpportunityDetailsSummaryView::representativeSeismicSection,
            OpportunityDetailsSummaryView::geologicalCrossSection)
        .containsExactly(
            "",
            false,
            "",
            "",
            "",
            "",
            fileMap,
            fileMap,
            fileMap
        );
  }
}