package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.validation.DeferredFileValidation;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileValidationService;

@Service
class FileUploadCustomValidatorFactory {

  private final ShapefileValidationService shapefileValidationService;

  @Autowired
  FileUploadCustomValidatorFactory(ShapefileValidationService shapefileValidationService) {
    this.shapefileValidationService = shapefileValidationService;
  }

  Optional<DeferredFileValidation> getFileUploadCustomValidator(FileDocumentType fileDocumentType) {
    var fileUploadCustomValidator = fileDocumentType.getFileUploadCustomValidatorType();

    if (fileUploadCustomValidator.isEmpty()) {
      return Optional.empty();
    }

    if (fileUploadCustomValidator.get() == FileUploadCustomValidatorType.SHAPEFILE_VALIDATION) {
      return Optional.ofNullable(shapefileValidationService);
    }

    return Optional.empty();
  }
}
