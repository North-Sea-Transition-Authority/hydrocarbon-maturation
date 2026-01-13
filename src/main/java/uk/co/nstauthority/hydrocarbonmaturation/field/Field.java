package uk.co.nstauthority.hydrocarbonmaturation.field;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Entity
@Table(name = "fields")
@Audited
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

  @Column(name = "responsible_party_prior_to_field_operator_id")
  private Integer prePearsOrganisationId;

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

  @Column(name = "is_on_licence_prior_to_field_operator")
  private Boolean isOnPrePearsLicence;

  private Boolean fieldLocatedInUkcs;

  private Boolean fieldLocatedInEngland;

  private Boolean hasConsultingAuthorities;

  @Enumerated(EnumType.STRING)
  @Column(name = "consulting_authorities")
  private FieldConsultingAuthority consultingAuthority;

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

  public Integer getPrePearsOrganisationId() {
    return prePearsOrganisationId;
  }

  public void setPrePearsOrganisationId(Integer responsibleOrganisationId) {
    this.prePearsOrganisationId = responsibleOrganisationId;
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

  public Boolean getIsOnPrePearsLicence() {
    return isOnPrePearsLicence;
  }

  public void setIsOnPrePearsLicence(Boolean onLicence) {
    isOnPrePearsLicence = onLicence;
  }


  public Boolean getFieldLocatedInUkcs() {
    return fieldLocatedInUkcs;
  }

  public void setFieldLocatedInUkcs(Boolean fieldLocatedInUkcs) {
    this.fieldLocatedInUkcs = fieldLocatedInUkcs;
  }

  public Boolean getFieldLocatedInEngland() {
    return fieldLocatedInEngland;
  }

  public void setFieldLocatedInEngland(Boolean fieldLocatedInEngland) {
    this.fieldLocatedInEngland = fieldLocatedInEngland;
  }

  public Boolean getHasConsultingAuthorities() {
    return hasConsultingAuthorities;
  }

  public void setHasConsultingAuthorities(Boolean hasConsultingAuthorities) {
    this.hasConsultingAuthorities = hasConsultingAuthorities;
  }

  public FieldConsultingAuthority getConsultingAuthority() {
    return consultingAuthority;
  }

  public void setConsultingAuthority(
      FieldConsultingAuthority fieldConsultingAuthority) {
    this.consultingAuthority = fieldConsultingAuthority;
  }
}