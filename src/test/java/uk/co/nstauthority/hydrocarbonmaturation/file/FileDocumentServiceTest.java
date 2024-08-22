package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.unit.DataSize;
import org.springframework.web.server.ResponseStatusException;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.FileUsage;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ExtendWith(MockitoExtension.class)
class FileDocumentServiceTest {


  @Mock
  private FileService fileService;
  @InjectMocks
  private FileDocumentService underTest;

  @Captor
  private ArgumentCaptor<Function<FileUsage.Builder, FileUsage>> fileUsageFunctionCaptor;
  private final FileDocumentType DOCUMENT_TYPE = FileDocumentType.RELINQUISHMENT_REPORT;

  private static final UUID FILE_ID = UUID.randomUUID();
  private static final String FILE_DESCRIPTION = "file description";
  private static final String USAGE_TYPE = "Resource";
  private final String USAGE_ID = "1";

  @Test
  void saveDocuments() {
    var documentForms = new ArrayList<UploadedFileForm>();

    var uploadedFile = new UploadedFile();
    uploadedFile.setId(FILE_ID);
    when(fileService.findAll(Collections.singletonList(FILE_ID))).thenReturn(Collections.singletonList(uploadedFile));

    var form = new UploadedFileForm();
    form.setFileId(FILE_ID);
    form.setFileDescription(FILE_DESCRIPTION);
    documentForms.add(form);

    underTest.saveDocuments(documentForms, DOCUMENT_TYPE, USAGE_ID, USAGE_TYPE);

    verify(fileService).updateUsageAndDescription(
        eq(uploadedFile),
        fileUsageFunctionCaptor.capture(),
        eq(FILE_DESCRIPTION)
    );
    var fileUsage = fileUsageFunctionCaptor.getValue().apply(FileUsage.newBuilder());
    assertThat(fileUsage)
        .extracting(
            FileUsage::usageId,
            FileUsage::usageType,
            FileUsage::documentType
        )
        .containsExactly(
            USAGE_ID,
            USAGE_TYPE,
            DOCUMENT_TYPE.name()
        );
  }

  @Test
  void saveDocuments_withFilesLinkedToAnotherApplication() {
    var documentForms = new ArrayList<UploadedFileForm>();

    var uploadedFile = new UploadedFile();
    uploadedFile.setId(FILE_ID);
    uploadedFile.setUsageId(UUID.randomUUID().toString());
    when(fileService.findAll(Collections.singletonList(FILE_ID))).thenReturn(Collections.singletonList(uploadedFile));

    var form = new UploadedFileForm();
    form.setFileId(FILE_ID);
    form.setFileDescription(FILE_DESCRIPTION);
    documentForms.add(form);

    assertThatThrownBy(
        () -> underTest.saveDocuments(documentForms, DOCUMENT_TYPE, USAGE_ID, USAGE_TYPE))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("File %s does not exist for Resource %s".formatted(FILE_ID, USAGE_ID));
  }

  @Test
  void throwIfFileDoesNotBelongToUsageType() {
    assertDoesNotThrow(() ->
        underTest.throwIfFileDoesNotBelongToUsageType(new UploadedFile(), DOCUMENT_TYPE.name(), USAGE_ID, USAGE_TYPE)
    );
  }
  @Test
  void getFileUploadComponentAttributes() {
    var existingFiles = Collections.<UploadedFileForm>emptyList();

    var attributesBuilder = FileUploadComponentAttributes.newBuilder()
        .withMaximumSize(DataSize.ofMegabytes(50))
        .withAllowedExtensions(Collections.singleton("pdf"));
    when(fileService.getFileUploadAttributes()).thenReturn(attributesBuilder);

    var result = underTest.getFileUploadComponentAttributes(existingFiles, DOCUMENT_TYPE);

    assertThat(result)
        .extracting(
            FileUploadComponentAttributes::uploadUrl,
            FileUploadComponentAttributes::downloadUrl,
            FileUploadComponentAttributes::deleteUrl,
            FileUploadComponentAttributes::existingFiles
        )
        .containsExactly(
            ReverseRouter.route(on(UnlinkedFileController.class).upload(null, DOCUMENT_TYPE.name(), null)),
            ReverseRouter.route(on(UnlinkedFileController.class).download(null, null)),
            ReverseRouter.route(on(UnlinkedFileController.class).delete(null, null)),
            existingFiles
        );
  }

  @Test
  void getFileUploadComponentAttributesBuilder() {
    var existingFiles = Collections.<UploadedFileForm>emptyList();

    var attributesBuilder = FileUploadComponentAttributes.newBuilder()
        .withMaximumSize(DataSize.ofMegabytes(50))
        .withAllowedExtensions(Collections.singleton("pdf"));
    when(fileService.getFileUploadAttributes()).thenReturn(attributesBuilder);

    var result = underTest.getFileUploadComponentAttributesBuilder(existingFiles, DOCUMENT_TYPE).build();

    assertThat(result)
        .extracting(
            FileUploadComponentAttributes::uploadUrl,
            FileUploadComponentAttributes::downloadUrl,
            FileUploadComponentAttributes::deleteUrl
        )
        .containsOnlyNulls();

    assertThat(result)
        .extracting(
            FileUploadComponentAttributes::existingFiles
        ).isEqualTo(
            existingFiles
        );
  }

  @Test
  void getFileNotFoundException() {

    var result = underTest.getFileNotFoundException(FILE_ID, USAGE_TYPE, USAGE_ID);

    assertThat(result)
        .extracting(
            ResponseStatusException::getStatusCode,
            ResponseStatusException::getReason
        )
        .containsExactly(
            HttpStatus.NOT_FOUND,
            "File %s does not exist for %s %s".formatted(FILE_ID, USAGE_TYPE, USAGE_ID)
        );
  }
}