package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

class NewFieldForm {
  private String legalFieldName;
  private String correctedLegalFieldName;
  private String commonNameForField;
  private String responsibleOrganisationId;
  private OnshoreOffshore onshoreOffshore;
  private GeographicalArea geographicalArea;
  private ResponsibleTeamType responsibleTeamType;
  private DecimalInput fieldNumber;
  private Boolean infrastructureHost;
  private DeterminationStatus determinationStatus;
  private String parentDeterminedField;

  public String getLegalFieldName() {
    return legalFieldName;
  }

  public void setLegalFieldName(String legalFieldName) {
    this.legalFieldName = legalFieldName;
  }

  public String getCorrectedLegalFieldName() {
    return correctedLegalFieldName;
  }

  public void setCorrectedLegalFieldName(String correctedLegalFieldName) {
    this.correctedLegalFieldName = correctedLegalFieldName;
  }

  public String getCommonNameForField() {
    return commonNameForField;
  }

  public void setCommonNameForField(String commonNameForField) {
    this.commonNameForField = commonNameForField;
  }

  public String getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(String responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(
      OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }

  public GeographicalArea getGeographicalArea() {
    return geographicalArea;
  }

  public void setGeographicalArea(GeographicalArea geographicalArea) {
    this.geographicalArea = geographicalArea;
  }

  public ResponsibleTeamType getResponsibleTeamType() {
    return responsibleTeamType;
  }

  public void setResponsibleTeamType(
      ResponsibleTeamType responsibleTeamType) {
    this.responsibleTeamType = responsibleTeamType;
  }

  public DecimalInput getFieldNumber() {
    return fieldNumber;
  }

  public void setFieldNumber(DecimalInput fieldNumber) {
    this.fieldNumber = fieldNumber;
  }

  public Boolean getInfrastructureHost() {
    return infrastructureHost;
  }

  public void setInfrastructureHost(Boolean infrastructureHost) {
    this.infrastructureHost = infrastructureHost;
  }

  public DeterminationStatus getDeterminationStatus() {
    return determinationStatus;
  }

  public void setDeterminationStatus(DeterminationStatus determinationStatus) {
    this.determinationStatus = determinationStatus;
  }

  public String getParentDeterminedField() {
    return parentDeterminedField;
  }

  public void setParentDeterminedField(String parentDeterminedField) {
    this.parentDeterminedField = parentDeterminedField;
  }
}