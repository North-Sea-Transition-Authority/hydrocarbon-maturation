package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSectionService;

@ExtendWith(MockitoExtension.class)
class ProspectSubmissionServiceTest {

  private ProspectSubmissionService prospectSubmissionService;
  private List<TaskListSectionService> taskListSectionServices;
  private SubmissionDetail submissionDetail;

  @Mock
  private TaskListSectionService taskListSectionService;

  @Mock
  private TaskListSectionService taskListSectionService2;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    taskListSectionServices = new ArrayList<>();
    taskListSectionServices.add(taskListSectionService);
    taskListSectionServices.add(taskListSectionService2);
    prospectSubmissionService = new ProspectSubmissionService(taskListSectionServices);
  }

  @Test
  void isSubmissionSubmittable_whenSectionsSubmittable_assertTrue() {
    when(taskListSectionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(taskListSectionService2.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(prospectSubmissionService.isSubmissionSubmittable(submissionDetail));
  }

  @Test
  void isSubmissionSubmittable_whenSectionNotSubmittable_assertTrue() {
    when(taskListSectionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(taskListSectionService2.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(prospectSubmissionService.isSubmissionSubmittable(submissionDetail));
  }
}