package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;

@ExtendWith(MockitoExtension.class)
class ReservoirTaskListSectionServiceTest {

  @Mock
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @InjectMocks
  private ReservoirTaskListSectionService reservoirTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection() {
    var taskListSectionOptional = reservoirTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Reservoir properties",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(submissionDetail.getId())))),
            "Reservoir",
            40
        );
  }
}