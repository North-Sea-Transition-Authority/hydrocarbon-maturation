package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;

@ExtendWith(MockitoExtension.class)
class ResourceSupportingDocumentServiceTest {

  @Mock
  private FileService fileService;

  @Mock
  private FileDocumentService fileDocumentService;

  @InjectMocks
  private ResourceSupportingDocumentService resourceSupportingDocumentService;

  private static final UUID FILE_ID = UUID.randomUUID();
  private static final String FILE_DESCRIPTION = "file description";
  private static final FileDocumentType DOCUMENT_TYPE = FileDocumentType.RELINQUISHMENT_REPORT;
  private final String USAGE_TYPE = Resource.class.getSimpleName();
  private static final Resource RESOURCE = new Resource();
  private final List<UploadedFileForm> EXISTING_FILES = Collections.emptyList();

  @BeforeEach
  void setUp() {
    RESOURCE.setId(UUID.randomUUID());
  }

  @Test
  void saveDocuments_VerifyMethodCall() {

    resourceSupportingDocumentService.saveDocuments(RESOURCE, EXISTING_FILES, DOCUMENT_TYPE);

    verify(fileDocumentService).saveDocuments(EXISTING_FILES, DOCUMENT_TYPE, RESOURCE.getId().toString(), USAGE_TYPE);
  }


  @Test
  void getUploadedFiles() {
    var uploadedFiles = Collections.<UploadedFile>emptyList();
    when(resourceSupportingDocumentService.getUploadedFiles(RESOURCE, DOCUMENT_TYPE))
        .thenReturn(uploadedFiles);

    assertThat(resourceSupportingDocumentService.getUploadedFiles(RESOURCE, DOCUMENT_TYPE))
        .isEqualTo(uploadedFiles);
  }

  @Test
  void getFileNotFoundException_VerifyMethodCall() {
    resourceSupportingDocumentService.getFileNotFoundException(FILE_ID, RESOURCE);

    verify(fileDocumentService).getFileNotFoundException(FILE_ID, USAGE_TYPE, RESOURCE.getId().toString());
  }

  @Test
  void throwIfFileDoesNotBelongToResource_VerifyMethodCall() {
    var uploadedFile = new UploadedFile();
    resourceSupportingDocumentService.throwIfFileDoesNotBelongToResource(uploadedFile, RESOURCE);

    verify(fileDocumentService).throwIfFileDoesNotBelongToUsageType(uploadedFile, null, RESOURCE.getId().toString(), USAGE_TYPE);
  }

  @Test
  void fileUploadComponentAttributes_VerifyMethodCall() {

    resourceSupportingDocumentService.fileUploadComponentAttributes(EXISTING_FILES, DOCUMENT_TYPE);

    verify(fileDocumentService).fileUploadComponentAttributes(EXISTING_FILES, DOCUMENT_TYPE);
  }
}