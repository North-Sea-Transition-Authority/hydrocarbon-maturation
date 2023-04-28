package uk.co.nstauthority.hydrocarbonmaturation.feedback;

public class FeedbackForm {

  private ServiceFeedbackRating serviceRating;

  private String feedback;

  public ServiceFeedbackRating getServiceRating() {
    return serviceRating;
  }

  public void setServiceRating(ServiceFeedbackRating serviceRating) {
    this.serviceRating = serviceRating;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }
}