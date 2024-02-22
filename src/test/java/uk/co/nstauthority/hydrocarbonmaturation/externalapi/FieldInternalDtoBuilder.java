package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

final class FieldInternalDtoBuilder {
  private Integer fieldIdentifier;
  private String fieldName;
  private OnshoreOffshore onshoreOffshore;
  private DeterminationStatus determinationStatus;
  private Integer organisationUnitId;
  private GeographicalArea geographicArea;
  private FieldStatus fieldStatus;

  private FieldInternalDtoBuilder() {
  }

  public static FieldInternalDtoBuilder builder() {
    return new FieldInternalDtoBuilder();
  }

  public FieldInternalDtoBuilder withFieldIdentifier(Integer fieldIdentifier) {
    this.fieldIdentifier = fieldIdentifier;
    return this;
  }

  public FieldInternalDtoBuilder withFieldName(String fieldName) {
    this.fieldName = fieldName;
    return this;
  }

  public FieldInternalDtoBuilder withOnshoreOffshore(OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
    return this;
  }

  public FieldInternalDtoBuilder withDeterminationStatus(DeterminationStatus determinationStatus) {
    this.determinationStatus = determinationStatus;
    return this;
  }

  public FieldInternalDtoBuilder withOrganisationUnitId(Integer organisationUnitId) {
    this.organisationUnitId = organisationUnitId;
    return this;
  }

  public FieldInternalDtoBuilder withGeographicArea(GeographicalArea geographicArea) {
    this.geographicArea = geographicArea;
    return this;
  }

  public FieldInternalDtoBuilder withFieldStatus(FieldStatus fieldStatus) {
    this.fieldStatus = fieldStatus;
    return this;
  }

  public FieldInternalDto build() {
    return new FieldInternalDto(
        fieldIdentifier,
        fieldName,
        onshoreOffshore,
        determinationStatus,
        organisationUnitId,
        geographicArea,
        fieldStatus
    );
  }
}
