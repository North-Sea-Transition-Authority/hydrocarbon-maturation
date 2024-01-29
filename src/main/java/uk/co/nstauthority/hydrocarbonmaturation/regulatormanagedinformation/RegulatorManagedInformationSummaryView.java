package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.Map;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

public record RegulatorManagedInformationSummaryView(String responsibleTeamType,
                                              String geographicalArea,
                                              boolean isBeingRelinquished,
                                              Map<UploadedFile, String> relinquishmentReport) {
  public static RegulatorManagedInformationSummaryView empty(boolean isBeingRelinquished) {
    return new RegulatorManagedInformationSummaryView("", "", isBeingRelinquished, Map.of());
  }
}
