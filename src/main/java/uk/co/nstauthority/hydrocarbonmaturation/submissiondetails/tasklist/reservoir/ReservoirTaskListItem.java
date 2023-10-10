package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListItem;

public record ReservoirTaskListItem(String reservoirName, String reservoirDeleteUrl, List<TaskListItem> taskListItems) {
}
