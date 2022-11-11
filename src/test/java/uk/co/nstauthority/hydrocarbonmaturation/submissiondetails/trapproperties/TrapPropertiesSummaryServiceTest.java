package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesSummaryServiceTest {

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private FileUploadService fileUploadService;

  @Mock
  private SupportingDocumentService supportingDocumentService;

  @InjectMocks
  private TrapPropertiesSummaryService trapPropertiesSummaryService;

  private SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Test
  void getTrapPropertiesSummaryFromSubmissionDetail_whenNoTrapProperty() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.empty());
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(emptySummary);

    var trapPropertiesSummaryView = trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(trapPropertiesSummaryView)
        .extracting(
            TrapPropertiesSummaryView::trapDescription,
            TrapPropertiesSummaryView::trapType,
            TrapPropertiesSummaryView::otherTrapType,
            TrapPropertiesSummaryView::latitudeDegrees,
            TrapPropertiesSummaryView::latitudeMinutes,
            TrapPropertiesSummaryView::latitudeSeconds,
            TrapPropertiesSummaryView::longitudeDegrees,
            TrapPropertiesSummaryView::longitudeMinutes,
            TrapPropertiesSummaryView::longitudeSeconds,
            TrapPropertiesSummaryView::longitudeHemisphere,
            TrapPropertiesSummaryView::probabilityDistributionSummaryViews,
            TrapPropertiesSummaryView::prospectDepthMap)
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            List.of(
                emptySummary,
                emptySummary,
                emptySummary),
            Collections.emptyMap()
        );
  }

  @Test
  void getTrapPropertiesSummaryFromSubmissionDetail_whenTrapPropertyPresent() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20",
        "20", "20", "20",
        "20", "20", "unit");

    var trapProperty = new TrapProperty();
    trapProperty.setTrapDescription("desc");
    trapProperty.setTrapType(TrapType.OTHER);
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setLatitudeDegrees(BigDecimal.ONE);
    trapProperty.setLatitudeMinutes(BigDecimal.ONE);
    trapProperty.setLatitudeSeconds(BigDecimal.ONE);
    trapProperty.setLongitudeDegrees(BigDecimal.TEN);
    trapProperty.setLongitudeMinutes(BigDecimal.TEN);
    trapProperty.setLongitudeSeconds(BigDecimal.TEN);
    trapProperty.setLongitudeHemisphere(Hemisphere.EAST);
    trapProperty.setOtherTrapType("other trap type");

    FileUploadForm fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileDescription("description");

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));
    when(probabilityDistributionSummaryService
             .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(testSummary);
    when(supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail,
        SupportingDocumentType.PROSPECT_DEPTH_MAP)).thenReturn(List.of(fileUploadForm));

    var uploadedFile = new UploadedFile();
    uploadedFile.setFilename("uploadedfile.txt");
    uploadedFile.setDescription("description");

    when(fileUploadService.findUploadedFileOrThrow(fileUploadForm.getUploadedFileId()))
        .thenReturn(uploadedFile);

    var fileMap = new HashMap<UploadedFile, String>();
    fileMap.put(uploadedFile, ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
            .download(submissionDetail.getId(), fileUploadForm.getUploadedFileId())));

    var trapPropertiesSummaryView = trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(trapPropertiesSummaryView)
          .extracting(
              TrapPropertiesSummaryView::trapDescription,
              TrapPropertiesSummaryView::trapType,
              TrapPropertiesSummaryView::otherTrapType,
              TrapPropertiesSummaryView::latitudeDegrees,
              TrapPropertiesSummaryView::latitudeMinutes,
              TrapPropertiesSummaryView::latitudeSeconds,
              TrapPropertiesSummaryView::longitudeDegrees,
              TrapPropertiesSummaryView::longitudeMinutes,
              TrapPropertiesSummaryView::longitudeSeconds,
              TrapPropertiesSummaryView::longitudeHemisphere,
              TrapPropertiesSummaryView::probabilityDistributionSummaryViews,
              TrapPropertiesSummaryView::prospectDepthMap)
          .containsExactly(
              "desc",
              "Other",
              "other trap type",
              "1°",
              "1''",
              "1'",
              "10°",
              "10''",
              "10'",
              "East",
              List.of(
                  testSummary,
                  testSummary,
                  testSummary),
              fileMap
          );
  }
}