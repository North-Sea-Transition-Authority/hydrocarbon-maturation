package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListItem;

/**
 * Interface to represent a task list item on the post-drill dry hole form task list. Applies sensible defaults
 * specific to the prospect task list implementation.
 */
public interface PostDrillDryHoleTaskListItem extends TaskListItem<PostDrillDryHoleTaskListItemType> {

  @Override
  default boolean isVisible(PostDrillDryHoleTaskListItemType target) {
    return true;
  }

  @Override
  default boolean showTaskListLabels(PostDrillDryHoleTaskListItemType target) {
    return true;
  }
}
