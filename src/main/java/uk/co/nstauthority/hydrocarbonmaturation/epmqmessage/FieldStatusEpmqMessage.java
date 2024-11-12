package uk.co.nstauthority.hydrocarbonmaturation.epmqmessage;

import java.time.Instant;

public class FieldStatusEpmqMessage extends RemiEpmqMessage {

  private Integer fieldId;
  private Integer fieldStatus;

  public FieldStatusEpmqMessage() {
    super(RemiTypeMapping.FIELD_STATUS_UPDATED.name(), null, null);
  }

  public FieldStatusEpmqMessage(
      String correlationId,
      Instant createdInstant,
      Integer fieldId,
      Integer fieldStatus
  ) {
    super(RemiTypeMapping.FIELD_STATUS_UPDATED.name(), correlationId, createdInstant);
    this.fieldId = fieldId;
    this.fieldStatus = fieldStatus;
  }

  public Integer getFieldId() {
    return fieldId;
  }

  public Integer getFieldStatus() {
    return fieldStatus;
  }
}
