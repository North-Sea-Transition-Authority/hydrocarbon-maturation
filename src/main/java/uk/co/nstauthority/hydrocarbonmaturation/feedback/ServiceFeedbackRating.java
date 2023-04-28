package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ServiceFeedbackRating implements Displayable {
  VERY_SATISFIED("Very satisfied"),
  SATISFIED("Satisfied"),
  NEITHER("Neither satisfied or dissatisfied"),
  DISSATISFIED("Dissatisfied"),
  VERY_DISSATISFIED("Very dissatisfied");

  private final String displayName;

  ServiceFeedbackRating(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
