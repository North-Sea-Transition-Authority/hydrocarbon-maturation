package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileValidationService;

@Service
class FileUploadCustomValidatorFactory {

  private final ShapefileValidationService shapefileValidationService;

  @Autowired
  FileUploadCustomValidatorFactory(ShapefileValidationService shapefileValidationService) {
    this.shapefileValidationService = shapefileValidationService;
  }

  public Optional<FileUploadCustomValidator> getFileUploadCustomValidator(SupportingDocumentType supportingDocumentType) {
    var fileUploadCustomValidator = supportingDocumentType.getFileUploadCustomValidator();

    if (fileUploadCustomValidator.isEmpty()) {
      return Optional.empty();
    }

    if (fileUploadCustomValidator.get() == FileUploadCustomValidatorType.SHAPEFILE_VALIDATION) {
      return Optional.ofNullable(shapefileValidationService);
    }

    return Optional.empty();
  }
}
