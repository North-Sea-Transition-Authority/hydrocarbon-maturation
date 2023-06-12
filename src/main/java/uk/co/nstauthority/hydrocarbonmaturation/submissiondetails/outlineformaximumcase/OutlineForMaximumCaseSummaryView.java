package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;

public record OutlineForMaximumCaseSummaryView(Map<UploadedFile, String> outlineForMaximumCaseShapefile,
                                               boolean hasVerifiedShapefile,
                                               String mapGeoJsonDataRoute,
                                               String mapAttributions) {
  public static OutlineForMaximumCaseSummaryView empty() {
    return new OutlineForMaximumCaseSummaryView(Map.of(), false, "", "");
  }
}