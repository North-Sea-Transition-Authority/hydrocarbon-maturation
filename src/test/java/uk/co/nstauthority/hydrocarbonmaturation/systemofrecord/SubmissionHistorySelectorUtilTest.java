package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class SubmissionHistorySelectorUtilTest {

  @Test
  void getSubmissionSelectorItems() {
    var selectedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    var approvedSubmissionDetail = SubmissionDetailTestUtils
        .createNewProspectSubmissionDetail();
    approvedSubmissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    approvedSubmissionDetail.setAcceptedTimestamp(
        LocalDateTime.of(2012, 11, 11, 10, 10)
            .toInstant(ZoneOffset.UTC));
    selectedSubmissionDetail.setAcceptedTimestamp(Instant.now());
    var queryResult = List.of(
        SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(selectedSubmissionDetail),
        SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(approvedSubmissionDetail)
    );

    var expectedMap = Map.of(
        selectedSubmissionDetail.getId().toString(),
        "Prospect update (Pending acceptance)",
        approvedSubmissionDetail.getId().toString(),
        "New prospect (Accepted: 11 November 2012)"
    );
    assertEquals(expectedMap, SubmissionHistorySelectorUtil.getSubmissionSelectorItems(queryResult));
  }
}