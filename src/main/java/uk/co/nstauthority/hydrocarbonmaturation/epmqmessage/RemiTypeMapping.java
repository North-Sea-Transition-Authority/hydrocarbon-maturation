package uk.co.nstauthority.hydrocarbonmaturation.epmqmessage;

import uk.co.fivium.energyportalmessagequeue.message.EpmqMessage;

public enum RemiTypeMapping {
FIELD_UPDATED(FieldEpmqMessage.class),
FIELD_STATUS_UPDATED(FieldStatusEpmqMessage.class);

  private final Class<? extends EpmqMessage> messageClass;

  RemiTypeMapping(
      Class<? extends EpmqMessage> messageClass
  ) {
    this.messageClass = messageClass;
  }

  public Class<? extends EpmqMessage> getMessageClass() {
    return messageClass;
  }
}
