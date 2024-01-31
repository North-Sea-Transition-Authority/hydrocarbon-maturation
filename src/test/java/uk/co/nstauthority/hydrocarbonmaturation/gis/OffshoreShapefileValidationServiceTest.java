package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.validation.ValidationResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@ExtendWith(MockitoExtension.class)
class OffshoreShapefileValidationServiceTest {


  @Mock
  private ShapefileValidationService shapefileValidationService;
  @InjectMocks
  private OffshoreShapefileValidationService underTest;

  @Nested
  class WhenValidating {
    @Mock
    private InputStream inputStream;

    @Test
    void verifyMethodCall() {
      var validationResult = Mockito.mock(ValidationResult.class);
      when(shapefileValidationService.validate(inputStream, OnshoreOffshore.OFFSHORE)).thenReturn(validationResult);

      var result = underTest.validate(inputStream);

      assertThat(result).isEqualTo(validationResult);
    }
  }
}