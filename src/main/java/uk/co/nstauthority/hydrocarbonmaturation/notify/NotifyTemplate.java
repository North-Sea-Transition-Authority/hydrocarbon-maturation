package uk.co.nstauthority.hydrocarbonmaturation.notify;

public enum NotifyTemplate {

  EMAIL_DELIVERY_FAILED("EMAIL_DELIVERY_FAILED_V1"),
  SUBMISSION_APPROVAL_NOTIFICATION("SUBMISSION_APPROVAL_NOTIFICATION_V1"),
  SUBMISSION_CONFIRMATION_NOTIFICATION("SUBMISSION_CONFIRMATION_NOTIFICATION_V1"),
  SUBMISSION_WITHDRAW_CONFIRMATION("SUBMISSION_WITHDRAW_CONFIRMATION_V1"),
  SUBMISSION_RETURNED_FOR_UPDATE("SUBMISSION_RETURNED_FOR_UPDATE_V1")
  ;

  private final String templateName;

  NotifyTemplate(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return this.templateName;
  }
}
