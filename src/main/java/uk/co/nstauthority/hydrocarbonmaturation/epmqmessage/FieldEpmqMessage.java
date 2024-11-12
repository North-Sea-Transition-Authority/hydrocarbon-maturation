package uk.co.nstauthority.hydrocarbonmaturation.epmqmessage;

import java.time.Instant;

public class FieldEpmqMessage extends RemiEpmqMessage {

  private Integer fieldId;
  private String fieldName;
  private Integer fieldStatus;
  private String subtype;
  private Boolean infrastructureHost;
  private String onshoreOffshore;
  private Boolean medianLineFlag;
  private Integer parentFieldId;
  private String geographicalAreaMnemonic;
  private Integer fieldNumber;

  public FieldEpmqMessage() {
    super(RemiTypeMapping.FIELD_UPDATED.name(), null, null);
  }

  private FieldEpmqMessage(
      Integer fieldId,
      String fieldName,
      Integer fieldStatus,
      String subtype,
      Boolean infrastructureHost,
      String onshoreOffshore,
      Boolean medianLineFlag,
      Integer parentFieldId,
      String geographicalAreaMnemonic,
      Integer fieldNumber,
      String correlationId,
      Instant createdInstant
  ) {
    super(RemiTypeMapping.FIELD_UPDATED.name(), correlationId, createdInstant);
    this.fieldId = fieldId;
    this.fieldName = fieldName;
    this.fieldStatus = fieldStatus;
    this.subtype = subtype;
    this.infrastructureHost = infrastructureHost;
    this.onshoreOffshore = onshoreOffshore;
    this.medianLineFlag = medianLineFlag;
    this.parentFieldId = parentFieldId;
    this.geographicalAreaMnemonic = geographicalAreaMnemonic;
    this.fieldNumber = fieldNumber;
  }

  public Integer getFieldId() {
    return fieldId;
  }

  public String getFieldName() {
    return fieldName;
  }

  public Integer getFieldStatus() {
    return fieldStatus;
  }

  public String getSubtype() {
    return subtype;
  }

  public Boolean getInfrastructureHost() {
    return infrastructureHost;
  }

  public String getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public Boolean getMedianLineFlag() {
    return medianLineFlag;
  }

  public Integer getParentFieldId() {
    return parentFieldId;
  }

  public String getGeographicalAreaMnemonic() {
    return geographicalAreaMnemonic;
  }

  public Integer getFieldNumber() {
    return fieldNumber;
  }

  public static Builder builder(String correlationId, Instant createdInstant) {
    return new Builder(correlationId, createdInstant);
  }

  public static class Builder {
    private final String correlationId;
    private final Instant createdInstant;

    private Integer fieldId;
    private String fieldName;
    private Integer fieldStatus;
    private String subtype;
    private Boolean infrastructureHost;
    private String onshoreOffshore;
    private Boolean medianLineFlag;
    private Integer parentFieldId;
    private String geographicalAreaMnemonic;
    private Integer fieldNumber;

    public Builder setFieldId(Integer fieldId) {
      this.fieldId = fieldId;
      return this;
    }

    public Builder setFieldName(String fieldName) {
      this.fieldName = fieldName;
      return this;
    }

    public Builder setFieldStatus(Integer fieldStatus) {
      this.fieldStatus = fieldStatus;
      return this;
    }

    public Builder setSubtype(String subtype) {
      this.subtype = subtype;
      return this;
    }

    public Builder setInfrastructureHost(Boolean infrastructureHost) {
      this.infrastructureHost = infrastructureHost;
      return this;
    }

    public Builder setOnshoreOffshore(String onshoreOffshore) {
      this.onshoreOffshore = onshoreOffshore;
      return this;
    }

    public Builder setMedianLineFlag(Boolean medianLineFlag) {
      this.medianLineFlag = medianLineFlag;
      return this;
    }

    public Builder setParentFieldId(Integer parentFieldId) {
      this.parentFieldId = parentFieldId;
      return this;
    }

    public Builder setGeographicalAreaMnemonic(String geographicalAreaMnemonic) {
      this.geographicalAreaMnemonic = geographicalAreaMnemonic;
      return this;
    }

    public Builder setFieldNumber(Integer fieldNumber) {
      this.fieldNumber = fieldNumber;
      return this;
    }

    private Builder(
        String correlationId,
        Instant createdInstant
    ) {
      this.correlationId = correlationId;
      this.createdInstant = createdInstant;
    }

    public FieldEpmqMessage build() {
      return new FieldEpmqMessage(
        fieldId,
        fieldName,
        fieldStatus,
        subtype,
        infrastructureHost,
        onshoreOffshore,
        medianLineFlag,
        parentFieldId,
          geographicalAreaMnemonic,
        fieldNumber,
        correlationId,
        createdInstant
      );
    }
  }
}
