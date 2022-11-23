package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReservoirTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSectionService;

@ExtendWith(MockitoExtension.class)
class PostDrillDryWellSummaryServiceTest {

  @Mock
  private DryWellDetailsSummaryService dryWellDetailsSummaryService;

  @Mock
  private PermeabilitySummaryService permeabilitySummaryService;

  @Mock
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Mock
  private PermeabilitySubmissionService permeabilitySubmissionService;

  private List<TaskListSectionService> postWellDrillTaskListSections;

  private PostDrillDryWellSummaryService postDrillDryWellSummaryService;

  private ReservoirTaskListSectionService reservoirTaskListSectionService;

  private ReviewAndSubmitTaskListSectionService reviewAndSubmitTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    postWellDrillTaskListSections = new ArrayList<>();
    reservoirTaskListSectionService = new ReservoirTaskListSectionService(
        reservoirPropertiesSubmissionService, permeabilitySubmissionService);
    reviewAndSubmitTaskListSectionService = new ReviewAndSubmitTaskListSectionService();
    postWellDrillTaskListSections.add(reservoirTaskListSectionService);
    postWellDrillTaskListSections.add(reviewAndSubmitTaskListSectionService);

    postDrillDryWellSummaryService = new PostDrillDryWellSummaryService(
        dryWellDetailsSummaryService,
        permeabilitySummaryService,
        postWellDrillTaskListSections
    );
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