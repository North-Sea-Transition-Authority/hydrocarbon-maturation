package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
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
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileConversionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJson;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJsonService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCasePersistenceServiceTest {

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;
  @Mock
  private ShapefileConversionService shapefileConversionService;
  @Mock
  private OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;
  @Mock
  private OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  @Mock
  private SubmissionGeoJsonService submissionGeoJsonService;
  @InjectMocks
  private OutlineForMaximumCasePersistenceService underTest;
  private static final FileDocumentType TYPE = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;

  @Nested
  class WhenDeletingOutlineForMaximumCase {
    @Mock
    private SubmissionDetail submissionDetail;
    private final UUID UPLOADED_FILE_ID = UUID.randomUUID();
    private final UploadedFile uploadedFile = new UploadedFile();

    @BeforeEach
    void setup() {
      uploadedFile.setId(UPLOADED_FILE_ID);

      when(supportingDocumentService.getUploadedFiles(submissionDetail, TYPE))
          .thenReturn(List.of(uploadedFile));

      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail)).thenReturn(TYPE);
    }

    @Test
    void verifyCalls() {
      underTest.deleteOutlineForMaximumCase(submissionDetail);

      verify(submissionGeoJsonService).deleteGeojson(submissionDetail);
      verify(supportingDocumentService).deleteFile(UPLOADED_FILE_ID);
      verify(outlineForMaximumCaseRepository).deleteBySubmissionDetail(submissionDetail);
    }
  }

  @Nested
  class WhenGettingGeoJsonForOutlineForMaximumCase {
    @Mock
    private SubmissionDetail submissionDetail;

    @Test
    void verifyCalls() {
      var shapefileGeojson = new SubmissionGeoJson();
      shapefileGeojson.setGeojson("{test}");
      when(submissionGeoJsonService.getShapefileGeoJsonForSubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(shapefileGeojson));
      var geoJson = underTest.getGeoJsonForOutlineForMaximumCase(submissionDetail);

      assertEquals(geoJson, shapefileGeojson.getGeojson());
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

  @Nested
  class WhenGettingUploadedShapefileZip {
    @Mock
    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setUp() {
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(any(SubmissionDetail.class))).thenReturn(
          TYPE);
    }

    @Test
    void noShapefiles() {
      when(supportingDocumentService.getUploadedFiles(submissionDetail, TYPE)).thenReturn(List.of());

      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() -> underTest.getUploadedShapefileZip(submissionDetail))
          .withMessage("No shapefile found for submission detail %s and type %s".formatted(submissionDetail.getId(), TYPE.name()));
    }


    @Test
    void moreThanOneShapefile() {
      when(supportingDocumentService.getUploadedFiles(submissionDetail, TYPE))
          .thenReturn(List.of(mock(UploadedFile.class), mock(UploadedFile.class)));

      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() -> underTest.getUploadedShapefileZip(submissionDetail))
          .withMessage("Multiple shapefiles found for submission detail %s and type %s".formatted(submissionDetail.getId(), TYPE.name()));
    }

    @Test
    void oneShapefile() {
      var uploadedFile = mock(UploadedFile.class);
      when(supportingDocumentService.getUploadedFiles(submissionDetail, TYPE))
          .thenReturn(List.of(uploadedFile));

      var result = underTest.getUploadedShapefileZip(submissionDetail);

      assertThat(result).isEqualTo(uploadedFile);
    }
  }

  @Nested
  class WhenSavingOutlineForMaximumCaseUploads {
    @Mock
    private SubmissionDetail submissionDetail;
    @Mock
    private Collection<UploadedFileForm> uploadedFileForms;
    @Mock
    private User user;
    @Mock
    private UploadedFile uploadedFile;

    @Test
    void saveDocumentsAndStoreGeoJson() {
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail)).thenReturn(TYPE);
      when(supportingDocumentService.getUploadedFiles(submissionDetail, TYPE)).thenReturn(List.of(uploadedFile));
      var geoJson = "GEO_JSON";
      when(shapefileConversionService.convertShapefileToGeoJson(uploadedFile)).thenReturn(geoJson);

      underTest.saveOutlineForMaximumCaseUploads(submissionDetail, uploadedFileForms);

      verify(supportingDocumentService).saveDocuments(submissionDetail, uploadedFileForms, TYPE);
      verify(submissionGeoJsonService).storeGeoJson(geoJson, submissionDetail);
    }
  }
}