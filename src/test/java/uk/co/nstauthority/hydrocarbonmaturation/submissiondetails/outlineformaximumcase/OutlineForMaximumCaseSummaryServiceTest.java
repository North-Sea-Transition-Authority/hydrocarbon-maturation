package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Map;
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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;

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
      UPLOADED_FILE = new UploadedFile();
      UPLOADED_FILE.setId(UUID.randomUUID());

      ROUTE = "route";
      when(outlineForMaximumCaseMapService.getMapGeoJsonDataRoute(submissionDetail)).thenReturn(ROUTE);

      ATTRIBUTIONS = "attributions";
      when(outlineForMaximumCaseMapService.getMapAttributions()).thenReturn(ATTRIBUTIONS);
    }

    @Test
    void hasUpload() {
      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail)).thenReturn(documentType);
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
              Map.of(UPLOADED_FILE, ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                  .download(submissionDetail.getId(), UPLOADED_FILE.getId()))),
              true,
              ROUTE,
              ATTRIBUTIONS
      );
    }

    @Test
    void noUpload() {
      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail)).thenReturn(documentType);
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
              Map.of(),
              false,
              ROUTE,
              ATTRIBUTIONS
      );
    }
  }
}