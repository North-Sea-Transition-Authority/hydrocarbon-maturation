package uk.co.nstauthority.hydrocarbonmaturation.field;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Entity
@Table(name = "fields")
public class Field {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "field_identifier_seq")
  @SequenceGenerator(name = "field_identifier_seq", sequenceName = "field_identifier_seq", allocationSize = 1)
  private Integer fieldIdentifier;

  @JoinColumn(name = "resource_id")
  @ManyToOne
  private Resource resource;

  private String legalFieldName;

  private String correctedLegalFieldName;

  private String commonNameForField;

  private Integer responsibleOrganisationId;

  private String organisationRegisteredNumber;

  private String organisationName;

  @Enumerated(EnumType.STRING)
  private OnshoreOffshore onshoreOffshore;

  private Integer fieldNumber;

  private Boolean infrastructureHost;

  @Enumerated(EnumType.STRING)
  private DeterminationStatus determinationStatus;

  @JoinColumn(name = "parent_determined_field_id")
  @ManyToOne
  private Field parentDeterminedField;

  public Integer getFieldIdentifier() {
    return fieldIdentifier;
  }

  public void setFieldIdentifier(Integer fieldIdentifier) {
    this.fieldIdentifier = fieldIdentifier;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

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

  public Integer getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(Integer responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
  }

  public String getOrganisationName() {
    return organisationName;
  }

  public void setOrganisationName(String organisationName) {
    this.organisationName = organisationName;
  }

  public String getOrganisationRegisteredNumber() {
    return organisationRegisteredNumber;
  }

  public void setOrganisationRegisteredNumber(String organisationRegisteredNumber) {
    this.organisationRegisteredNumber = organisationRegisteredNumber;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(
      OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }

  public Integer getFieldNumber() {
    return fieldNumber;
  }

  public void setFieldNumber(Integer fieldNumber) {
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

  public Field getParentDeterminedField() {
    return parentDeterminedField;
  }

  public void setParentDeterminedField(Field parentDeterminedField) {
    this.parentDeterminedField = parentDeterminedField;
  }
}