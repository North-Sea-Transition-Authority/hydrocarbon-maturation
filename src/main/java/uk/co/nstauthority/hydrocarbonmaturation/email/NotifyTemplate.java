package uk.co.nstauthority.hydrocarbonmaturation.email;

public enum NotifyTemplate {

  FEEDBACK_FAILED_TO_SEND("0a63b4ad-e733-4091-bb2a-4893adb933b0"),
  SUBMISSION_APPROVAL_NOTIFICATION("be32442a-f681-4888-aba9-b18ccca45afa"),
  SUBMISSION_CONFIRMATION_NOTIFICATION("c928e104-dedc-4ae1-ab27-d2112271f5ba"),
  SUBMISSION_RETURNED_FOR_UPDATE("afece522-d0c1-4405-a7eb-3da8108ceca3"),
  SUBMISSION_UPDATE_CONFIRMATION("a15350d7-eead-4f9a-8d75-600a362d824b"),
  SUBMISSION_WITHDRAW_CONFIRMATION("6b637ed4-a341-48dd-a1f4-4a55ff6c4f91"),
  ;

  private final String templateId;

  NotifyTemplate(String templateId) {
    this.templateId = templateId;
  }

  public String getTemplateId() {
    return templateId;
  }
}
