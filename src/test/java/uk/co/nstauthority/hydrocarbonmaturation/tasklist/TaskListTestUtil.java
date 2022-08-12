package uk.co.nstauthority.hydrocarbonmaturation.tasklist;

public class TaskListTestUtil {

  private TaskListTestUtil() {
    throw new IllegalStateException("TaskListTestUtil is a static utility class and should not be instantiated");
  }

  public static TaskListItemView.TaskListItemViewBuilder getItemViewBuilder(int displayOrder,
                                                                            String displayName,
                                                                            String actionUrl) {
    return new TaskListItemView.TaskListItemViewBuilder(displayOrder, displayName, actionUrl);
  }
}
