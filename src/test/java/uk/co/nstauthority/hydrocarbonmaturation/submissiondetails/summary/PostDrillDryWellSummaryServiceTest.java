package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;

@ExtendWith(MockitoExtension.class)
class PostDrillDryWellSummaryServiceTest {

  @Mock
  private DryWellDetailsSummaryService dryWellDetailsSummaryService;

  @Mock
  private PermeabilitySummaryService permeabilitySummaryService;

  private PostDrillDryWellSummaryService postDrillDryWellSummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();

    postDrillDryWellSummaryService = new PostDrillDryWellSummaryService(
        dryWellDetailsSummaryService,
        permeabilitySummaryService);
  }

  @Test
  void getDryWellDetailSummaryViewFromSubmissionDetail_verifyMethodCall() {
    postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail);

    verify(dryWellDetailsSummaryService, times(1))
        .getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getPermeabilityDetailSummaryViewFromSubmissionDetail_verifyMethodCall() {
    postDrillDryWellSummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail);

    verify(permeabilitySummaryService, times(1))
        .getPermeabilitySummaryFromSubmissionDetail(submissionDetail);
  }
}