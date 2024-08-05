package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService.FILE_ROW_KEY;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationSummaryServiceTest {

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private ResourceSupportingDocumentService resourceSupportingDocumentService;
  @InjectMocks
  private RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;

  private final Resource resource = new Resource();

  @ParameterizedTest
  @EnumSource(GeographicalArea.class)
  void getRegulatorManagedInformationSummaryFromResource_whenGeographicalAreaAndNoFileUploaded_whenEntity(
      GeographicalArea geographicalArea) {
    var regulatorManagedInfo = new RegulatorManagedInformation();
    regulatorManagedInfo.setGeographicalArea(geographicalArea);
    regulatorManagedInfo.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    regulatorManagedInfo.setResource(resource);

    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource))
        .thenReturn(false);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInfo));

    var regulatorManagedInformationSummaryView = regulatorManagedInformationSummaryService
        .getRegulatorManagedInformationSummaryFromResource(resource);

    assertThat(regulatorManagedInformationSummaryView)
        .extracting(
            RegulatorManagedInformationSummaryView::geographicalArea,
            RegulatorManagedInformationSummaryView::isBeingRelinquished,
            RegulatorManagedInformationSummaryView::responsibleTeamType,
            RegulatorManagedInformationSummaryView::relinquishmentReport)
        .containsExactly(
            geographicalArea.getDisplayName(),
            false,
            ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND.getDisplayName(),
            List.of()
        );
    verifyNoInteractions(resourceSupportingDocumentService);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getRegulatorManagedInformationSummaryFromResource_whenIsBeingRelinquished_whenEntity(
      boolean isBeingRelinquished) {
    var regulatorManagedInfo = new RegulatorManagedInformation();
    regulatorManagedInfo.setGeographicalArea(GeographicalArea.ONSHORE);
    regulatorManagedInfo.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    regulatorManagedInfo.setResource(resource);

    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource))
        .thenReturn(isBeingRelinquished);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInfo));

    var expectedFileViews = new ArrayList<SummaryFileViewRow>();

    if (isBeingRelinquished) {
      var uploadedFile = UploadedFileTestUtil.builder()
          .withId(UUID.randomUUID())
          .withName("uploadedfile.txt")
          .withDescription("description")
          .build();

      var fileUploadForm = new UploadedFileForm();
      fileUploadForm.setFileId(uploadedFile.getId());
      fileUploadForm.setUploadedFileDescription("description");

      expectedFileViews.add(SummaryFileViewRow.from(
          FILE_ROW_KEY,
          uploadedFile,
          ReverseRouter.route(on(ResourceSupportingDocumentsController.class)
              .download(resource.getId(), fileUploadForm.getUploadedFileId()))
      ));

      when(resourceSupportingDocumentService.getUploadedFiles(regulatorManagedInfo.getResource(),
          FileDocumentType.RELINQUISHMENT_REPORT))
          .thenReturn(List.of(uploadedFile));
    }
    var regulatorManagedInformationSummaryView = regulatorManagedInformationSummaryService
        .getRegulatorManagedInformationSummaryFromResource(resource);

    assertThat(regulatorManagedInformationSummaryView)
        .extracting(
            RegulatorManagedInformationSummaryView::geographicalArea,
            RegulatorManagedInformationSummaryView::isBeingRelinquished,
            RegulatorManagedInformationSummaryView::responsibleTeamType)
        .containsExactly(
            GeographicalArea.ONSHORE.getDisplayName(),
            isBeingRelinquished,
            ResponsibleTeamType.EXPLORATION.getDisplayName()
        );

    if (isBeingRelinquished) {
      assertThat(regulatorManagedInformationSummaryView.relinquishmentReport())
          .isEqualTo(expectedFileViews);
    } else {
      assertThat(regulatorManagedInformationSummaryView.relinquishmentReport())
          .isEqualTo(List.of());
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getRegulatorManagedInformationSummaryFromResource_whenIsBeingRelinquished_whenNoEntityPresent(
      boolean isBeingRelinquished) {

    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource))
        .thenReturn(isBeingRelinquished);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());

    var regulatorManagedInformationSummaryView = regulatorManagedInformationSummaryService
        .getRegulatorManagedInformationSummaryFromResource(resource);

    assertThat(regulatorManagedInformationSummaryView)
        .extracting(
            RegulatorManagedInformationSummaryView::geographicalArea,
            RegulatorManagedInformationSummaryView::isBeingRelinquished,
            RegulatorManagedInformationSummaryView::responsibleTeamType,
            RegulatorManagedInformationSummaryView::relinquishmentReport)
        .containsExactly(
            "",
            isBeingRelinquished,
            "",
            List.of(SummaryFileViewRow.empty(FILE_ROW_KEY))
        );
  }
}