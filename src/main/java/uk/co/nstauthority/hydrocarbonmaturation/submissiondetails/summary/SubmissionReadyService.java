package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSectionService;

@Service
public class SubmissionReadyService {

  private final List<TaskListSectionService> taskListSectionServices;

  @Autowired
  public SubmissionReadyService(List<TaskListSectionService> taskListSectionServices) {
    this.taskListSectionServices = taskListSectionServices;
  }

  public boolean isSubmissionSubmittable(SubmissionDetail submissionDetail) {
    return taskListSectionServices.stream().allMatch(section -> section.isSectionSubmittable(submissionDetail));
  }
}
