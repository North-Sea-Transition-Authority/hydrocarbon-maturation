package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListItem;

/**
 * Interface to represent a task list item on the prospect form task list. Applies sensible defaults
 * specific to the prospect task list implementation.
 */
interface ProspectTaskListItem extends TaskListItem<ProspectTaskListItemType> {

  @Override
  default boolean isVisible(ProspectTaskListItemType target) {
    return true;
  }

  @Override
  default boolean showTaskListLabels(ProspectTaskListItemType target) {
    return true;
  }
}
