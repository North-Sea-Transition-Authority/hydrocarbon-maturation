package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSubmissionService;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationTaskListSectionServiceTest {

  @Mock
  private AdditionalInformationSubmissionService additionalInformationSubmissionService;

  @InjectMocks
  private AdditionalInformationTaskListSectionService additionalInformationTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection() {
    var taskListSectionOptional = additionalInformationTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::displayName,
            TaskListSection::displayOrder,
            TaskListSection::items)
        .containsExactly(
            "Additional information",
            70,
            List.of(
                new TaskListItem("Additional information",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(AdditionalInformationController.class)
                        .getAdditionalInformation(submissionDetail.getId(), null)))
        ));
  }
}