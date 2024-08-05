package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryService.FILE_ROW_KEY;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseSummaryServiceTest {

  @Mock
  private OutlineForMaximumCaseMapService outlineForMaximumCaseMapService;
  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;
  @Mock
  private OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  @Mock
  private OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  @InjectMocks
  private OutlineForMaximumCaseSummaryService underTest;

  @Nested
  class WhenGettingOutlineForMaximumCaseSummaryFromSubmissionDetail {
    @Mock
    private SubmissionDetail submissionDetail;
    private String ROUTE;
    private String ATTRIBUTIONS;
    private UploadedFile UPLOADED_FILE;

    @BeforeEach
    void setup() {
      UPLOADED_FILE = UploadedFileTestUtil.builder()
          .withId(UUID.randomUUID())
          .build();

      ROUTE = "route";
      when(outlineForMaximumCaseMapService.getMapGeoJsonDataRoute(submissionDetail)).thenReturn(ROUTE);

      ATTRIBUTIONS = "attributions";
      when(outlineForMaximumCaseMapService.getMapAttributions()).thenReturn(ATTRIBUTIONS);
    }

    @Test
    void hasUpload() {
      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail)).thenReturn(
          documentType);
      when(supportingDocumentService.getUploadedFiles(
          submissionDetail,
          documentType
      )).thenReturn(List.of(UPLOADED_FILE));

      when(outlineForMaximumCasePersistenceService.hasVerifiedShapefile(submissionDetail)).thenReturn(true);

      var result = underTest.getOutlineForMaximumCaseSummaryFromSubmissionDetail(submissionDetail);

      assertThat(result)
          .extracting(
              OutlineForMaximumCaseSummaryView::outlineForMaximumCaseShapefile,
              OutlineForMaximumCaseSummaryView::hasVerifiedShapefile,
              OutlineForMaximumCaseSummaryView::mapGeoJsonDataRoute,
              OutlineForMaximumCaseSummaryView::mapAttributions
          )
          .containsExactly(
              FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(
                  FILE_ROW_KEY,
                  List.of(UPLOADED_FILE),
                  submissionDetail
              ),
              true,
              ROUTE,
              ATTRIBUTIONS
          );
    }

    @Test
    void noUpload() {
      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail)).thenReturn(
          documentType);
      when(supportingDocumentService.getUploadedFiles(
          submissionDetail,
          documentType
      )).thenReturn(List.of());

      when(outlineForMaximumCasePersistenceService.hasVerifiedShapefile(submissionDetail)).thenReturn(false);

      var result = underTest.getOutlineForMaximumCaseSummaryFromSubmissionDetail(submissionDetail);

      assertThat(result)
          .extracting(
              OutlineForMaximumCaseSummaryView::outlineForMaximumCaseShapefile,
              OutlineForMaximumCaseSummaryView::hasVerifiedShapefile,
              OutlineForMaximumCaseSummaryView::mapGeoJsonDataRoute,
              OutlineForMaximumCaseSummaryView::mapAttributions
          )
          .containsExactly(
              List.of(SummaryFileViewRow.empty(FILE_ROW_KEY)),
              false,
              ROUTE,
              ATTRIBUTIONS
          );
    }
  }
}