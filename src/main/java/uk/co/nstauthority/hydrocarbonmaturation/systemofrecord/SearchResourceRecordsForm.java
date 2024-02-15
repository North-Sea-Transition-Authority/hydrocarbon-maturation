package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.ArrayList;
import java.util.List;

public class SearchResourceRecordsForm {

  private String resourceNameFromResourceDetail;
  private String fieldNameForResourceSubmission;
  private Integer responsibleOrganisationId;
  private List<String> resourceTypes = new ArrayList<>();
  private List<String> opportunityTypes = new ArrayList<>();
  private String licenceRound;
  private String centreOfInterestBlock;
  private String licenceId;
  private List<String> geographicalArea = new ArrayList<>();
  private boolean showOnlyPreferredViews;
  private List<String> fieldStatuses = new ArrayList<>();

  public String getResourceNameFromResourceDetail() {
    return resourceNameFromResourceDetail;
  }

  public void setResourceNameFromResourceDetail(String resourceNameFromResourceDetail) {
    this.resourceNameFromResourceDetail = resourceNameFromResourceDetail;
  }

  public Integer getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(Integer responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
  }

  public String getCentreOfInterestBlock() {
    return centreOfInterestBlock;
  }

  public void setCentreOfInterestBlock(String centreOfInterestBlock) {
    this.centreOfInterestBlock = centreOfInterestBlock;
  }

  public String getLicenceRound() {
    return licenceRound;
  }

  public void setLicenceRound(String licenceRound) {
    this.licenceRound = licenceRound;
  }

  public String getLicenceId() {
    return licenceId;
  }

  public void setLicenceId(String licenceId) {
    this.licenceId = licenceId;
  }

  public List<String> getResourceTypes() {
    return resourceTypes;
  }

  public void setResourceTypes(List<String> resourceTypes) {
    this.resourceTypes = resourceTypes;
  }

  public List<String> getOpportunityTypes() {
    return opportunityTypes;
  }

  public void setOpportunityTypes(
      List<String> opportunityTypes) {
    this.opportunityTypes = opportunityTypes;
  }

  public List<String> getGeographicalArea() {
    return geographicalArea;
  }

  public void setGeographicalArea(List<String> geographicalArea) {
    this.geographicalArea = geographicalArea;
  }

  public boolean isShowOnlyPreferredViews() {
    return showOnlyPreferredViews;
  }

  public void setShowOnlyPreferredViews(boolean showOnlyPreferredViews) {
    this.showOnlyPreferredViews = showOnlyPreferredViews;
  }

  public String getFieldNameForResourceSubmission() {
    return fieldNameForResourceSubmission;
  }

  public void setFieldNameForResourceSubmission(String fieldNameForResourceSubmission) {
    this.fieldNameForResourceSubmission = fieldNameForResourceSubmission;
  }

  public List<String> getFieldStatuses() {
    return fieldStatuses;
  }

  public void setFieldStatuses(List<String> fieldStatuses) {
    this.fieldStatuses = fieldStatuses;
  }

  public static SearchResourceRecordsForm from(SearchProspectAndDiscoveryRecordsFilter filter) {
    var form = new SearchResourceRecordsForm();
    form.setShowOnlyPreferredViews(filter.isShowOnlyPreferredViews());
    form.setResourceTypes(filter.getResourceTypes());
    form.setOpportunityTypes(filter.getOpportunityTypes());
    form.setLicenceId(filter.getLicenceId());
    form.setLicenceRound(filter.getLicenceRound());
    form.setResourceNameFromResourceDetail(filter.getResourceNameFromResourceDetail());
    form.setCentreOfInterestBlock(filter.getCentreOfInterestBlock());
    form.setResponsibleOrganisationId(filter.getResponsibleOrganisationId());
    form.setGeographicalArea(filter.getGeographicalArea());
    return form;
  }

  public static SearchResourceRecordsForm from(SearchFieldRecordsFilter filter) {
    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(filter.getResourceTypes());
    form.setFieldNameForResourceSubmission(filter.getFieldNameForResourceSubmission());
    form.setFieldStatuses(filter.getFieldStatuses());
    form.setGeographicalArea(filter.getGeographicalAreas());
    return form;
  }
}
