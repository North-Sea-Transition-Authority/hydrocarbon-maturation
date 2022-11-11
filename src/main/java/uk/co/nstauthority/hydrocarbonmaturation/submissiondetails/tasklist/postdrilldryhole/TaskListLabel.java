package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

public enum TaskListLabel {

  NOT_COMPLETE,
  COMPLETE,
  ;

  public static TaskListLabel notStartedOrComplete(boolean complete) {
    if (complete) {
      return COMPLETE;
    } else {
      return NOT_COMPLETE;
    }
  }
}

