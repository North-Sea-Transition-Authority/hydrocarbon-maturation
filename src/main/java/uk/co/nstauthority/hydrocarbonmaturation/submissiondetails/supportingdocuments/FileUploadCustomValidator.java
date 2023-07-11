package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface FileUploadCustomValidator {
  List<String> validateFileUpload(InputStream inputStream, String originalFilename, UUID usageId);
}
