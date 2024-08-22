package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class TaskListService {

  private final List<TaskListSectionService> taskListSectionServices;

  @Autowired
  public TaskListService(List<TaskListSectionService> taskListSectionServices) {
    this.taskListSectionServices = taskListSectionServices;
  }

  public List<TaskListSection> getAllSections(SubmissionDetail submissionDetail, User user) {
    return taskListSectionServices.stream()
        .map(tlss -> tlss.getSection(submissionDetail, user))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(TaskListSection::displayOrder))
        .toList();
  }
}
