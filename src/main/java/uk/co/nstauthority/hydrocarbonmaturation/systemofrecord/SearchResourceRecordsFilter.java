package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@SessionAttributes({"searchSystemOfRecordFilter"})
public class SearchResourceRecordsFilter implements Serializable {

  @Serial
  private static final long serialVersionUID = 2231235085927579692L;

  private String resourceDetailIdForResourceSubmission;
  private Integer responsibleOrganisationId;
  private List<String> resourceTypes = new ArrayList<>();
  private List<String> opportunityTypes = new ArrayList<>();
  private String licenceRound;
  private String centreOfInterestBlock;
  private String licenceId;
  private List<String> geographicalArea = new ArrayList<>();
  private boolean showOnlyPreferredViews;
  private boolean showResults;

  void update(SearchResourceRecordsForm form) {
    setShowOnlyPreferredViews(form.isShowOnlyPreferredViews());
    setResourceTypes(form.getResourceTypes());
    setOpportunityTypes(form.getOpportunityTypes());
    setLicenceId(form.getLicenceId());
    setLicenceRound(form.getLicenceRound());
    setResourceDetailIdForResourceSubmission(form.getResourceDetailIdForResourceSubmission());
    setCentreOfInterestBlock(form.getCentreOfInterestBlock());
    setResponsibleOrganisationId(form.getResponsibleOrganisationId());
    setGeographicalArea(form.getGeographicalArea());
    setShowResults(true);
  }

  void clearFilter() {
    setGeographicalArea(new ArrayList<>());
    setLicenceRound(null);
    setCentreOfInterestBlock(null);
    setLicenceId(null);
    setResourceDetailIdForResourceSubmission(null);
    setShowOnlyPreferredViews(true);
    setResponsibleOrganisationId(null);
    setResourceTypes(List.of(ResourceType.PROSPECT.name(), ResourceType.DISCOVERY.name()));
    var oppTypes = new ArrayList<>(EnumUtils.getEnumNames(OpportunityType.class));
    oppTypes.add("NOT_SET");
    setOpportunityTypes(oppTypes);
    setShowResults(false);
  }

  public String getResourceDetailIdForResourceSubmission() {
    return resourceDetailIdForResourceSubmission;
  }

  public void setResourceDetailIdForResourceSubmission(String resourceDetailIdForResourceSubmission) {
    this.resourceDetailIdForResourceSubmission = resourceDetailIdForResourceSubmission;
  }

  public Integer getResponsibleOrganisationId() {
    return responsibleOrganisationId;
  }

  public void setResponsibleOrganisationId(Integer responsibleOrganisationId) {
    this.responsibleOrganisationId = responsibleOrganisationId;
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

  public void setOpportunityTypes(List<String> opportunityTypes) {
    this.opportunityTypes = opportunityTypes;
  }

  public String getLicenceRound() {
    return licenceRound;
  }

  public void setLicenceRound(String licenceRound) {
    this.licenceRound = licenceRound;
  }

  public String getCentreOfInterestBlock() {
    return centreOfInterestBlock;
  }

  public void setCentreOfInterestBlock(String centreOfInterestBlock) {
    this.centreOfInterestBlock = centreOfInterestBlock;
  }

  public String getLicenceId() {
    return licenceId;
  }

  public void setLicenceId(String licenceId) {
    this.licenceId = licenceId;
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

  public boolean isShowResults() {
    return showResults;
  }

  public void setShowResults(boolean showResults) {
    this.showResults = showResults;
  }
}
