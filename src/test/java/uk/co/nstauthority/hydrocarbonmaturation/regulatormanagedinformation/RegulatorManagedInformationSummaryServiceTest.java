package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService.FILE_ROW_KEY;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationSummaryServiceTest {

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private ResourceSupportingDocumentService resourceSupportingDocumentService;
  @Mock
  private PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  @Mock
  private ResourceLinkService resourceLinkService;
  @Mock
  private FieldPersistenceService fieldPersistenceService;
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
    regulatorManagedInfo.setLinkedToShelvedCopField(false);

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
            RegulatorManagedInformationSummaryView::relinquishmentReport,
            RegulatorManagedInformationSummaryView::linkedToFieldName,
            RegulatorManagedInformationSummaryView::linkedToFieldUrl)
        .containsExactly(
            geographicalArea.getDisplayName(),
            false,
            ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND.getDisplayName(),
            List.of(),
            "",
            ""
        );
    verifyNoInteractions(resourceSupportingDocumentService);
  }

  @Test
  void getRegulatorManagedInformationSummaryFromResource_whenGeographicalAreaAndNoFileUploaded_whenEntity() {
    var regulatorManagedInfo = new RegulatorManagedInformation();
    regulatorManagedInfo.setGeographicalArea(GeographicalArea.NORTHERN_NORTH_SEA);
    regulatorManagedInfo.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    regulatorManagedInfo.setResource(resource);
    regulatorManagedInfo.setLinkedToShelvedCopField(true);

    var fieldResource = new Resource();
    fieldResource.setId(UUID.randomUUID());

    var field = new Field();
    field.setCorrectedLegalFieldName("Name");
    field.setResource(fieldResource);

    when(submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource))
        .thenReturn(false);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInfo));
    when(postFieldDiscoveryLinkService.getFieldLinkedToDiscoveryIfExists(resource)).thenReturn(Optional.of(field));

    var regulatorManagedInformationSummaryView = regulatorManagedInformationSummaryService
        .getRegulatorManagedInformationSummaryFromResource(resource);

    assertThat(regulatorManagedInformationSummaryView)
        .extracting(
            RegulatorManagedInformationSummaryView::geographicalArea,
            RegulatorManagedInformationSummaryView::isBeingRelinquished,
            RegulatorManagedInformationSummaryView::responsibleTeamType,
            RegulatorManagedInformationSummaryView::relinquishmentReport,
            RegulatorManagedInformationSummaryView::linkedToFieldName,
            RegulatorManagedInformationSummaryView::linkedToFieldUrl)
        .containsExactly(
            GeographicalArea.NORTHERN_NORTH_SEA.getDisplayName(),
            false,
            ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND.getDisplayName(),
            List.of(),
            "Name",
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(fieldResource.getId(), null))
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
    regulatorManagedInfo.setLinkedToShelvedCopField(false);

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
            RegulatorManagedInformationSummaryView::responsibleTeamType,
            RegulatorManagedInformationSummaryView::linkedToFieldName,
            RegulatorManagedInformationSummaryView::linkedToFieldUrl)
        .containsExactly(
            GeographicalArea.ONSHORE.getDisplayName(),
            isBeingRelinquished,
            ResponsibleTeamType.EXPLORATION.getDisplayName(),
            "",
            ""
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
            RegulatorManagedInformationSummaryView::relinquishmentReport,
            RegulatorManagedInformationSummaryView::linkedToFieldName,
            RegulatorManagedInformationSummaryView::linkedToFieldUrl)
        .containsExactly(
            "",
            isBeingRelinquished,
            "",
            List.of(SummaryFileViewRow.empty(FILE_ROW_KEY)),
            "",
            ""
        );
  }

  @Test
  void getAssociatedFieldMap() {
    var discovery = new Resource();

    var fieldResource1 = new Resource();
    fieldResource1.setId(UUID.randomUUID());
    var fieldResource2 = new Resource();
    fieldResource2.setId(UUID.randomUUID());

    var field1 = new Field();
    field1.setResource(fieldResource1);
    field1.setCorrectedLegalFieldName("Field 1");
    var field2 = new Field();
    field2.setResource(fieldResource2);
    field2.setCorrectedLegalFieldName("Field 2");

    var associatedFields = List.of(fieldResource1, fieldResource2);

    when(resourceLinkService.getAssociatedFieldResources(discovery)).thenReturn(associatedFields);
    when(fieldPersistenceService.getFieldByResource(fieldResource1)).thenReturn(Optional.of(field1));
    when(fieldPersistenceService.getFieldByResource(fieldResource2)).thenReturn(Optional.of(field2));

    assertThat(regulatorManagedInformationSummaryService.getFieldsAssociatedWithDiscoveryMap(discovery)).containsExactly(
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(fieldResource1.getId(),null)),
            field1.getCorrectedLegalFieldName()
        ),
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(fieldResource2.getId(), null)),
            field2.getCorrectedLegalFieldName()
        )
    );
  }
}