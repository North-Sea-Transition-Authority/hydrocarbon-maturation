package uk.co.nstauthority.hydrocarbonmaturation.file;

import static uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadCustomValidatorType.OFFSHORE_SHAPEFILE_VALIDATION;
import static uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadCustomValidatorType.ONSHORE_SHAPEFILE_VALIDATION;

import java.util.Optional;
import java.util.Set;

public enum FileDocumentType {

  MAP_OF_OPPORTUNITY,
  PROSPECT_DEPTH_MAP,
  GEOLOGICAL_CROSS_SECTION,
  REPRESENTATIVE_SEISMIC_SECTION,
  ADDITIONAL_DOCUMENT,
  SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE(Set.of("zip"), OFFSHORE_SHAPEFILE_VALIDATION),
  SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_ONSHORE(Set.of("zip"), ONSHORE_SHAPEFILE_VALIDATION),
  RELINQUISHMENT_REPORT,
  ;

  private final Set<String> allowedExtensions;
  private final FileUploadCustomValidatorType fileUploadCustomValidatorType;

  FileDocumentType() {
    this.allowedExtensions = null;
    this.fileUploadCustomValidatorType = null;
  }

  FileDocumentType(Set<String> allowedExtensions, FileUploadCustomValidatorType fileUploadCustomValidatorType) {
    this.fileUploadCustomValidatorType = fileUploadCustomValidatorType;
    this.allowedExtensions = allowedExtensions;
  }

  /**
   * Retrieve extensions that are only available on the specific document type.
   * @return Optional set of extensions to validate against. If empty, assume default extensions.
   */
  public Optional<Set<String>> getAllowedExtensions() {
    return Optional.ofNullable(allowedExtensions);
  }

  public Optional<FileUploadCustomValidatorType> getFileUploadCustomValidatorType() {
    return Optional.ofNullable(fileUploadCustomValidatorType);
  }
}
