package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.validation.DeferredFileValidation;
import uk.co.nstauthority.hydrocarbonmaturation.gis.OffshoreShapefileValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.gis.OnshoreShapefileValidationService;

@Service
class FileUploadCustomValidatorFactory {

  private final OnshoreShapefileValidationService onshoreShapefileValidationService;
  private final OffshoreShapefileValidationService offshoreShapefileValidationService;

  @Autowired
  FileUploadCustomValidatorFactory(OnshoreShapefileValidationService onshoreShapefileValidationService,
                                   OffshoreShapefileValidationService offshoreShapefileValidationService) {
    this.onshoreShapefileValidationService = onshoreShapefileValidationService;
    this.offshoreShapefileValidationService = offshoreShapefileValidationService;
  }

  Optional<DeferredFileValidation> getFileUploadCustomValidator(FileDocumentType fileDocumentType) {
    var fileUploadCustomValidator = fileDocumentType.getFileUploadCustomValidatorType();

    return fileUploadCustomValidator
        .flatMap(fileUploadCustomValidatorType -> switch (fileUploadCustomValidatorType) {
          case OFFSHORE_SHAPEFILE_VALIDATION -> Optional.ofNullable(offshoreShapefileValidationService);
          case ONSHORE_SHAPEFILE_VALIDATION -> Optional.ofNullable(onshoreShapefileValidationService);
        });
  }
}
