package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.ReservoirTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.ReviewAndSubmitTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.TaskListSectionService;

@ExtendWith(MockitoExtension.class)
class PostDrillDryWellSummaryServiceTest {

  @Mock
  private DryWellDetailsSummaryService dryWellDetailsSummaryService;

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
  void getDryWellSubNavigation_whenTwoSections_TwoResults() {
    var subNavItems = postDrillDryWellSummaryService.getPostDrillDryWellSubNavigation(submissionDetail);

    assertThat(subNavItems).hasSize(2);
    assertThat(subNavItems)
        .containsExactly(
            new SubNavigationPostDrillSectionItem("Reservoir properties", "#reservoirproperties"),
            new SubNavigationPostDrillSectionItem("Permeability", "#permeability")
        );
  }

  @Test
  void getDryWellSubNavigation_whenOneWrongSection_noResults() {
    postWellDrillTaskListSections = List.of(reviewAndSubmitTaskListSectionService);

    postDrillDryWellSummaryService = new PostDrillDryWellSummaryService(
        dryWellDetailsSummaryService,
        postWellDrillTaskListSections
    );
    var subNavItems = postDrillDryWellSummaryService.getPostDrillDryWellSubNavigation(submissionDetail);

    assertThat(subNavItems).isEmpty();
  }
}