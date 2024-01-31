package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.gis.OffshoreShapefileValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.gis.OnshoreShapefileValidationService;

@ExtendWith(MockitoExtension.class)
class FileUploadCustomValidatorFactoryTest {
  @Mock
  private OffshoreShapefileValidationService offshoreShapefileValidationService;
  @Mock
  private OnshoreShapefileValidationService onshoreShapefileValidationService;
  @InjectMocks
  private FileUploadCustomValidatorFactory underTest;

  @Nested
  class WhenGettingFileUploadCustomValidator {
    private final FileDocumentType WITH_CUSTOM_VALIDATOR_ONSHORE = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_ONSHORE;
    private final FileDocumentType WITH_CUSTOM_VALIDATOR_OFFSHORE = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
    private final FileDocumentType WITHOUT_CUSTOM_VALIDATOR = FileDocumentType.MAP_OF_OPPORTUNITY;

    @Test
    void withCustomValidator_Offshore() {
      var result = underTest.getFileUploadCustomValidator(WITH_CUSTOM_VALIDATOR_OFFSHORE);

      assertThat(result)
          .isPresent()
          .get()
          .isEqualTo(offshoreShapefileValidationService);
    }

    @Test
    void withCustomValidator_Onshore() {
      var result = underTest.getFileUploadCustomValidator(WITH_CUSTOM_VALIDATOR_ONSHORE);

      assertThat(result)
          .isPresent()
          .get()
          .isEqualTo(onshoreShapefileValidationService);
    }

    @Test
    void withoutCustomValidator() {
      var result = underTest.getFileUploadCustomValidator(WITHOUT_CUSTOM_VALIDATOR);

      assertThat(result)
          .isEmpty();
    }
  }
}