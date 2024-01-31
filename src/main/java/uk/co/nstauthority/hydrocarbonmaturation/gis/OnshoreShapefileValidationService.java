package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.validation.DeferredFileValidation;
import uk.co.fivium.fileuploadlibrary.validation.ValidationResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
public class OnshoreShapefileValidationService implements DeferredFileValidation {

  private final ShapefileValidationService shapefileValidationService;

  @Autowired
  OnshoreShapefileValidationService(ShapefileValidationService shapefileValidationService) {
    this.shapefileValidationService = shapefileValidationService;
  }

  @Override
  public ValidationResult validate(InputStream inputStream) {
    return shapefileValidationService.validate(inputStream, OnshoreOffshore.ONSHORE);
  }
}
