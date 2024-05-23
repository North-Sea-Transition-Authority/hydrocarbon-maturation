package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.apache.commons.lang3.BooleanUtils;
import org.jooq.tools.StringUtils;

public class SubmissionDetailDisplayUtil {
  private SubmissionDetailDisplayUtil() {
    throw new IllegalStateException("Util class");
  }

  public static String getDisplayNameWithoutLicenceApplicationReference(String resourceName,
                                                                        String centreOfInterestQuadBlock) {
    return resourceName + getCentreOfInterestBlockDisplay(centreOfInterestQuadBlock);
  }

  public static String getDisplayName(String resourceName,
                                      String centreOfInterestQuadBlock,
                                      String licenceApplicationReference) {
    return getDisplayNameWithoutLicenceApplicationReference(resourceName, centreOfInterestQuadBlock) +
        getLicenceApplicationReferenceDisplay(licenceApplicationReference);
  }

  private static String getCentreOfInterestBlockDisplay(String centreOfInterestQuadBlock) {
    if (BooleanUtils.isTrue(StringUtils.isBlank(centreOfInterestQuadBlock))) {
      return "";
    }

    return " (%s)".formatted(centreOfInterestQuadBlock);
  }

  private static String getLicenceApplicationReferenceDisplay(String licenceApplicationReference) {
    if (BooleanUtils.isTrue(StringUtils.isBlank(licenceApplicationReference))) {
      return "";
    }

    return " (Round %s)".formatted(licenceApplicationReference);
  }
}