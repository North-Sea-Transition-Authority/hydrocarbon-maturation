package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileConversionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCasePersistenceServiceTest {

  @Mock
  private SupportingDocumentService supportingDocumentService;
  @Mock
  private ShapefileConversionService shapefileConversionService;
  @Mock
  private OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;
  @InjectMocks
  private OutlineForMaximumCasePersistenceService underTest;
  private final SupportingDocumentType type = SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE;

  @Nested
  class WhenSavingOutlineForMaximumCaseUploadDescriptions {
    private OutlineForMaximumCaseUploadForm form;

    @BeforeEach
    void setup() {
      form = new OutlineForMaximumCaseUploadForm();
    }

    @Test
    void verifyCalls() {
      var fileUploadForm = new FileUploadForm();
      var fileUploadFormList = List.of(fileUploadForm);
      form.setOutlineForMaximumCase(fileUploadFormList);

      underTest.saveOutlineForMaximumCaseUploadDescriptions(form);

      verify(supportingDocumentService).updateSupportingDocumentFileDescriptions(fileUploadFormList);
    }
  }

  @Nested
  class WhenDeletingOutlineForMaximumCase {
    @Mock
    private SubmissionDetail submissionDetail;
    private final UUID UPLOADED_FILE_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
      var uploadedFile = new UploadedFile();
      uploadedFile.setId(UPLOADED_FILE_ID);
      var supportingDocument = new SupportingDocument();
      supportingDocument.setUploadedFile(uploadedFile);

      when(supportingDocumentService.getSupportingDocumentsForType(submissionDetail, type))
          .thenReturn(Optional.of(supportingDocument));

    }

    @Test
    void verifyCalls() {
      underTest.deleteOutlineForMaximumCase(submissionDetail);

      verify(supportingDocumentService).deleteFile(submissionDetail, UPLOADED_FILE_ID);
      verify(outlineForMaximumCaseRepository).deleteBySubmissionDetail(submissionDetail);
    }
  }

  @Nested
  class WhenGettingGeoJsonForOutlineForMaximumCase {
    @Mock
    private SubmissionDetail submissionDetail;
    @Mock
    private SupportingDocument supportingDocument;

    @BeforeEach
    void setup() {
      when(supportingDocumentService.getSupportingDocumentsForType(submissionDetail, type))
          .thenReturn(Optional.of(supportingDocument));
    }

    @Test
    void verifyCalls() {
      underTest.getGeoJsonForOutlineForMaximumCase(submissionDetail);

      verify(shapefileConversionService).convertShapefileToGeoJson(supportingDocument);
    }
  }

  @Nested
  class WhenSavingOutlineForMaximumCaseVerifyForm {
    @Mock
    private OutlineForMaximumCase outlineForMaximumCase;
    @Mock
    private SubmissionDetail submissionDetail;

    private OutlineForMaximumCaseVerifyForm form;

    @BeforeEach
    void setUp() {
      form = new OutlineForMaximumCaseVerifyForm();
      form.setShapeIsCorrect(false);
    }

    @Test
    void recordDoesNotExist_hasVerifiedShapefile() {
      when(outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());
      form.setShapeIsCorrect(true);

      var argumentCaptor = ArgumentCaptor.forClass(OutlineForMaximumCase.class);
      underTest.saveOutlineForMaximumCaseVerifyForm(submissionDetail, form);

      verify(outlineForMaximumCaseRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue().getHasVerifiedShapefile()).isTrue();
    }

    @Test
    void recordDoesNotExist_hasNotVerifiedShapefile() {
      when(outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());

      var argumentCaptor = ArgumentCaptor.forClass(OutlineForMaximumCase.class);
      underTest.saveOutlineForMaximumCaseVerifyForm(submissionDetail, form);

      verify(outlineForMaximumCaseRepository).save(argumentCaptor.capture());
      assertThat(argumentCaptor.getValue().getHasVerifiedShapefile()).isFalse();
    }

    @Test
    void recordExists() {
      when(outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(outlineForMaximumCase));
      underTest.saveOutlineForMaximumCaseVerifyForm(submissionDetail, form);
      verify(outlineForMaximumCaseRepository).save(outlineForMaximumCase);
    }
  }

  @Nested
  class WhenCheckingIfHasVerifiedShapefile {
    @Mock
    private SubmissionDetail submissionDetail;
    private OutlineForMaximumCase outlineForMaximumCase;

    @BeforeEach
    void setUp() {
      outlineForMaximumCase = new OutlineForMaximumCase();
      when(outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(outlineForMaximumCase));
    }
    @Test
    void hasVerifiedShapefile() {
      outlineForMaximumCase.setHasVerifiedShapefile(true);

      var result = underTest.hasVerifiedShapefile(submissionDetail);

      assertThat(result).isTrue();
    }

    @Test
    void hasNotVerifiedShapefile() {
      outlineForMaximumCase.setHasVerifiedShapefile(false);

      var result = underTest.hasVerifiedShapefile(submissionDetail);

      assertThat(result).isFalse();
    }
  }
}