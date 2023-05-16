package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import java.util.Optional;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;

public enum SupportingDocumentType {
  MAP_OF_OPPORTUNITY,
  PROSPECT_DEPTH_MAP,
  GEOLOGICAL_CROSS_SECTION,
  REPRESENTATIVE_SEISMIC_SECTION,
  ADDITIONAL_DOCUMENT,
  SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE(VirtualFolder.SHAPEFILE_GEOJSON, FileUploadCustomValidatorType.SHAPEFILE_VALIDATION),
  ;

  private final VirtualFolder virtualFolder;
  private final FileUploadCustomValidatorType fileUploadCustomValidatorType;

  SupportingDocumentType(VirtualFolder virtualFolder, FileUploadCustomValidatorType fileUploadCustomValidatorType) {
    this.virtualFolder = virtualFolder;
    this.fileUploadCustomValidatorType = fileUploadCustomValidatorType;
  }

  SupportingDocumentType() {
    this(VirtualFolder.SUPPORTING_DOCUMENTS, null);
  }

  public VirtualFolder getVirtualFolder() {
    return virtualFolder;
  }

  public Optional<FileUploadCustomValidatorType> getFileUploadCustomValidator() {
    return Optional.ofNullable(fileUploadCustomValidatorType);
  }
}

