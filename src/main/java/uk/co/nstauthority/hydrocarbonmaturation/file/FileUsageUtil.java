package uk.co.nstauthority.hydrocarbonmaturation.file;

import org.apache.commons.lang3.ObjectUtils;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class FileUsageUtil {

  private FileUsageUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static boolean hasNoUsage(UploadedFile uploadedFile) {
    return ObjectUtils.allNull(uploadedFile.getUsageId(), uploadedFile.getUsageType(), uploadedFile.getDocumentType());
  }
}
