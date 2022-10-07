package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.List;
import java.util.stream.IntStream;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public class FileUploadValidationUtils {

  private FileUploadValidationUtils() {
    throw new AssertionError();
  }

  public static void rejectIfFileDescriptionsAreEmptyOrWhitespace(Errors errors, List<FileUploadDetail> fileUploadDetails,
                                                            String field) {
    IntStream.range(0, fileUploadDetails.size()).forEachOrdered(
        i -> ValidationUtils.rejectIfEmptyOrWhitespace(errors, "%s[%d].uploadedFileDescription".formatted(field, i),
            "%s[%d].uploadedFileDescription.required".formatted(field, i),
            "Enter a description of this file"));
  }
}
