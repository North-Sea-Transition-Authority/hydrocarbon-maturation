package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class TaskListService {

  private final List<TaskListSectionService> taskListSectionServices;

  @Autowired
  TaskListService(List<TaskListSectionService> taskListSectionServices) {
    this.taskListSectionServices = taskListSectionServices;
  }

  public List<TaskListSection> getAllSections(SubmissionDetail submissionDetail) {
    return taskListSectionServices.stream()
        .map(tlss -> tlss.getSection(submissionDetail))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(TaskListSection::displayOrder))
        .toList();
  }
}
