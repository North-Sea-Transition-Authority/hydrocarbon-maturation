package uk.co.nstauthority.hydrocarbonmaturation.file;

public enum UploadErrorType {

  EXTENSION_NOT_ALLOWED("File extension is not allowed"),
  MAX_FILE_SIZE_EXCEEDED("File is larger than the maximum allowed upload size"),
  VIRUS_FOUND_IN_FILE("Virus found in file"),
  OTHER_ERRORS_FOUND("Other errors found"); // TODO: FDS needs to be enhanced to except this error type and custom messages

  private final String errorMessage;

  UploadErrorType(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}

