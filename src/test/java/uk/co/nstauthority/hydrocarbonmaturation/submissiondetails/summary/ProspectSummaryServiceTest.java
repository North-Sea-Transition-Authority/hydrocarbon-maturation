package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;

@ExtendWith(MockitoExtension.class)
class ProspectSummaryServiceTest {

  @Mock
  private GeneralDetailSummaryService generalDetailSummaryService;

  @Test
  void getGeneralDetailSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);

    verify(generalDetailSummaryService, times(1)).getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }
}