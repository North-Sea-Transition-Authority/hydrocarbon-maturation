package uk.co.nstauthority.hydrocarbonmaturation.notify;

public enum NotifyTemplate {

  EMAIL_DELIVERY_FAILED("EMAIL_DELIVERY_FAILED_V1"),
  SUBMISSION_APPROVAL_NOTIFICATION("SUBMISSION_APPROVAL_NOTIFICATION")
  ;

  private final String templateName;

  NotifyTemplate(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return this.templateName;
  }
}
