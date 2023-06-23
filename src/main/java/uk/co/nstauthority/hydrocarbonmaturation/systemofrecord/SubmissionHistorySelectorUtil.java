package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

public class SubmissionHistorySelectorUtil {

  private SubmissionHistorySelectorUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Map<String, String> getSubmissionSelectorItems(List<SubmissionQuerySearchRecord> submissionRecords) {
    var searchSelectorItems = new LinkedHashMap<String, String>();
    for (int i = 1; i <= submissionRecords.size(); i++) {
      searchSelectorItems.put(submissionRecords.get(i - 1).submissionDetailId().toString(),
          getRecordDisplay(submissionRecords.get(i - 1), (submissionRecords.size() + 1) - i));
    }
    return searchSelectorItems;
  }

  private static String getRecordDisplay(SubmissionQuerySearchRecord submissionQuerySearchRecord, Integer number) {
    return "Submission %s: %s (%s)"
        .formatted(number.toString(),
            submissionQuerySearchRecord.submissionType().getTypeName(),
            getApprovedDateDisplay(submissionQuerySearchRecord));
  }

  private static String getApprovedDateDisplay(SubmissionQuerySearchRecord submissionQuerySearchRecord) {
    if (submissionQuerySearchRecord.status() == SubmissionDetailStatus.APPROVED) {
      return "Approved: %s".formatted(DateUtil.formatLongDate(LocalDate.from(
          submissionQuerySearchRecord.approvedTimestamp().toLocalDateTime())));
    } else {
      return "Pending approval";
    }
  }
}
