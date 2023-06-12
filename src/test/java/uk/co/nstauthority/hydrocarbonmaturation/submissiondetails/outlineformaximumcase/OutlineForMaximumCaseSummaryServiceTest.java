package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseSummaryServiceTest {

  @Mock
  private OutlineForMaximumCaseMapService outlineForMaximumCaseMapService;
  @Mock
  private SupportingDocumentService supportingDocumentService;
  @Mock
  private FileUploadService fileUploadService;
  @Mock
  private OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  @InjectMocks
  private OutlineForMaximumCaseSummaryService underTest;

  @Nested
  class WhenGettingOutlineForMaximumCaseSummaryFromSubmissionDetail {
    @Mock
    private SubmissionDetail submissionDetail;
    private String ROUTE;
    private String ATTRIBUTIONS;
    private SupportingDocument SUPPORTING_DOCUMENT;
    private UploadedFile UPLOADED_FILE;

    @BeforeEach
    void setup() {
      SUPPORTING_DOCUMENT = new SupportingDocument();
      UPLOADED_FILE = new UploadedFile();
      UPLOADED_FILE.setId(UUID.randomUUID());
      SUPPORTING_DOCUMENT.setUploadedFile(UPLOADED_FILE);

      ROUTE = "route";
      when(outlineForMaximumCaseMapService.getMapGeoJsonDataRoute(submissionDetail)).thenReturn(ROUTE);

      ATTRIBUTIONS = "attributions";
      when(outlineForMaximumCaseMapService.getMapAttributions()).thenReturn(ATTRIBUTIONS);
    }

    @Test
    void hasUpload() {
      when(supportingDocumentService.getSupportingDocumentsForType(
          submissionDetail,
          SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE
      )).thenReturn(Optional.of(SUPPORTING_DOCUMENT));

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
      when(supportingDocumentService.getSupportingDocumentsForType(
          submissionDetail,
          SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE
      )).thenReturn(Optional.empty());

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