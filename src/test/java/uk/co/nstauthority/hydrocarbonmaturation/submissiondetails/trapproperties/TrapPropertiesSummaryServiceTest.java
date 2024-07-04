package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesSummaryServiceTest {

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  @InjectMocks
  private TrapPropertiesSummaryService trapPropertiesSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Test
  void getTrapPropertiesSummaryFromSubmissionDetail_whenNoTrapProperty() {
    var tableView = ProbabilityDistributionTableView.empty();

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.empty());
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        ))
        .thenReturn(tableView);

    var trapPropertiesSummaryView = trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

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
            TrapPropertiesSummaryView::tableView,
            TrapPropertiesSummaryView::prospectDepthMap,
            TrapPropertiesSummaryView::datum,
            TrapPropertiesSummaryView::quadAndBlockCentreOfInterest)
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
            tableView,
            Collections.emptyMap(),
            "",
            ""
        );
  }

  @Test
  void getTrapPropertiesSummaryFromSubmissionDetail_whenProspect_whenTrapPropertyPresent() {
    var tableView = ProbabilityDistributionTableView.empty();

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
    trapProperty.setDatum(ReservoirDatum.ED50);
    trapProperty.setCentreOfInterestQuadrantBlock("10/15");

    var uploadedFile = new UploadedFile();
    uploadedFile.setName("uploadedfile.txt");
    uploadedFile.setDescription("description");
    var fileUploadForm = new UploadedFileForm();
    fileUploadForm.setUploadedFileDescription("description");

    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail))
        .thenReturn(Optional.of(trapProperty));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT,
                ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT,
                ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT
            ),
            submissionDetail,
            null
        ))
        .thenReturn(tableView);
    when(supportingDocumentService.getUploadedFiles(
        submissionDetail,
        FileDocumentType.PROSPECT_DEPTH_MAP)).thenReturn(List.of(uploadedFile));

    var fileMap = new HashMap<UploadedFile, String>();
    fileMap.put(uploadedFile, ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
        .download(submissionDetail.getId(), fileUploadForm.getUploadedFileId())));

    var trapPropertiesSummaryView = trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

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
            TrapPropertiesSummaryView::tableView,
            TrapPropertiesSummaryView::prospectDepthMap,
            TrapPropertiesSummaryView::datum,
            TrapPropertiesSummaryView::quadAndBlockCentreOfInterest)
        .containsExactly(
            "desc",
            "Other",
            "other trap type",
            "1°",
            "1'",
            "1\"",
            "10°",
            "10'",
            "10\"",
            "East",
            tableView,
            fileMap,
            ReservoirDatum.ED50.getDisplayName(),
            "10/15"
        );
  }

  @Test
  void getTrapPropertiesSummaryFromSubmissionDetail_whenDiscovery_whenTrapPropertyPresent() {
    var tableView = ProbabilityDistributionTableView.empty();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();


    var trapProperty = new TrapProperty();
    trapProperty.setTrapDescription("desc");
    trapProperty.setTrapType(TrapType.OTHER);
    trapProperty.setSubmissionDetail(discoverySubmissionDetail);
    trapProperty.setLatitudeDegrees(BigDecimal.ONE);
    trapProperty.setLatitudeMinutes(BigDecimal.ONE);
    trapProperty.setLatitudeSeconds(BigDecimal.ONE);
    trapProperty.setLongitudeDegrees(BigDecimal.TEN);
    trapProperty.setLongitudeMinutes(BigDecimal.TEN);
    trapProperty.setLongitudeSeconds(BigDecimal.TEN);
    trapProperty.setLongitudeHemisphere(Hemisphere.EAST);
    trapProperty.setOtherTrapType("other trap type");
    trapProperty.setDatum(ReservoirDatum.ED50);
    trapProperty.setCentreOfInterestQuadrantBlock("10/15");

    var uploadedFile = new UploadedFile();
    uploadedFile.setName("uploadedfile.txt");
    uploadedFile.setDescription("description");
    var fileUploadForm = new UploadedFileForm();
    fileUploadForm.setUploadedFileDescription("description");

    when(trapPropertiesPersistenceService.getTrapProperty(discoverySubmissionDetail))
        .thenReturn(Optional.of(trapProperty));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_DISCOVERY,
                ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_DISCOVERY,
                ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_DISCOVERY
            ),
            discoverySubmissionDetail,
            null
        ))
        .thenReturn(tableView);
    when(supportingDocumentService.getUploadedFiles(
        discoverySubmissionDetail,
        FileDocumentType.PROSPECT_DEPTH_MAP)).thenReturn(List.of(uploadedFile));

    var fileMap = new HashMap<UploadedFile, String>();
    fileMap.put(uploadedFile, ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
        .download(discoverySubmissionDetail.getId(), fileUploadForm.getUploadedFileId())));

    var trapPropertiesSummaryView = trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(
        discoverySubmissionDetail);

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
            TrapPropertiesSummaryView::tableView,
            TrapPropertiesSummaryView::prospectDepthMap,
            TrapPropertiesSummaryView::datum,
            TrapPropertiesSummaryView::quadAndBlockCentreOfInterest)
        .containsExactly(
            "desc",
            "Other",
            "other trap type",
            "1°",
            "1'",
            "1\"",
            "10°",
            "10'",
            "10\"",
            "East",
            tableView,
            fileMap,
            ReservoirDatum.ED50.getDisplayName(),
            "10/15"
        );
  }
}