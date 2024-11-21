package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

public class FieldForm {
  private String legalFieldName;
  private String correctedLegalFieldName;
  private String commonNameForField;
  private String prePearsOrganisationId;
  private OnshoreOffshore onshoreOffshore;
  private GeographicalArea geographicalArea;
  private ResponsibleTeamType responsibleTeamType;
  private Boolean infrastructureHost;
  private DeterminationStatus determinationStatus;
  private String parentDeterminedField;
  private List<String> associatedDiscoveries;
  private String associatedDiscoverySelect;
  private Boolean isOnLicence;
  private List<String> licences = new ArrayList<>();
  private String licenceSelect;
  private Boolean fieldLocatedInUkcs;
  private Boolean fieldLocatedInEngland;
  private Boolean hasConsultingAuthorities;
  private FieldConsultingAuthority consultingAuthority;

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

  public String getPrePearsOrganisationId() {
    return prePearsOrganisationId;
  }

  public void setPrePearsOrganisationId(String prePearsOrganisationId) {
    this.prePearsOrganisationId = prePearsOrganisationId;
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

  public List<String> getAssociatedDiscoveries() {
    return associatedDiscoveries;
  }

  public void setAssociatedDiscoveries(List<String> associatedDiscoveries) {
    this.associatedDiscoveries = associatedDiscoveries;
  }

  public String getAssociatedDiscoverySelect() {
    return associatedDiscoverySelect;
  }

  public void setAssociatedDiscoverySelect(String associatedDiscoverySelect) {
    this.associatedDiscoverySelect = associatedDiscoverySelect;
  }

  public Boolean getIsOnLicence() {
    return isOnLicence;
  }

  public void setIsOnLicence(Boolean onLicence) {
    isOnLicence = onLicence;
  }

  public List<String> getLicences() {
    return licences;
  }

  public void setLicences(List<String> licences) {
    this.licences = licences;
  }

  public String getLicenceSelect() {
    return licenceSelect;
  }

  public void setLicenceSelect(String licenceSelect) {
    this.licenceSelect = licenceSelect;
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
      FieldConsultingAuthority consultingAuthority) {
    this.consultingAuthority = consultingAuthority;
  }
}