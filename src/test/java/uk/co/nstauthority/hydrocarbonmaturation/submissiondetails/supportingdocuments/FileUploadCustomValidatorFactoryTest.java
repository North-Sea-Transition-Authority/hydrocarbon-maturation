package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileValidationService;

@ExtendWith(MockitoExtension.class)
class FileUploadCustomValidatorFactoryTest {
  @Mock
  private ShapefileValidationService shapefileValidationService;
  @InjectMocks
  private FileUploadCustomValidatorFactory underTest;

  @Nested
  class WhenGettingFileUploadCustomValidator {
    private final SupportingDocumentType WITH_CUSTOM_VALIDATOR = SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE;
    private final SupportingDocumentType WITHOUT_CUSTOM_VALIDATOR = SupportingDocumentType.MAP_OF_OPPORTUNITY;

    @BeforeEach
    void setup() {
    }

    @Test
    void withCustomValidator() {
      var result = underTest.getFileUploadCustomValidator(WITH_CUSTOM_VALIDATOR);

      assertThat(result)
          .isPresent()
          .get()
          .isEqualTo(shapefileValidationService);
    }

    @Test
    void withoutCustomValidator() {
      var result = underTest.getFileUploadCustomValidator(WITHOUT_CUSTOM_VALIDATOR);

      assertThat(result)
          .isEmpty();
    }
  }
}