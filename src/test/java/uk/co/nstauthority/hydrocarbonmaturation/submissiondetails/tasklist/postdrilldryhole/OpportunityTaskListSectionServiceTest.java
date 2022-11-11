package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSubmissionService;

@ExtendWith(MockitoExtension.class)
class OpportunityTaskListSectionServiceTest {

  @Mock
  private OpportunityDetailsSubmissionService opportunityDetailsSubmissionService;

  @Mock
  private TrapPropertiesSubmissionService trapPropertiesSubmissionService;

  @Mock
  private SourceAndSealSubmissionService sourceAndSealSubmissionService;

  @InjectMocks
  private OpportunityTaskListSectionService opportunityTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection() {
    when(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    var taskListSectionOptional = opportunityTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Opportunity details",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetail.getId(), null))),
            new TaskListItem("Trap properties",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(submissionDetail.getId()))),
            new TaskListItem("Source and seal",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(submissionDetail.getId())))),
            "Opportunity",
            20
        );
  }
}