package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionWorkAreaQueryResult {

  private final ResourceType resourceType;
  private final UUID submissionDetailId;
  private final SubmissionType submissionType;
  private final String resourceName;
  private final String centreOfInterestQuadBlock;
  private final Integer prospectOrDiscoveryOrganisationResponsibleId;
  private final List<Integer> licencesTheResourceIsOn;
  private final SubmissionDetailStatus status;
  private final boolean updateRequested;
  private final boolean isCorrection;
  private final Integer fieldIdentifier;
  private final Integer parentFieldId;
  private final FieldStatus fieldStatus;
  private final String licenceApplicationReference;

  SubmissionWorkAreaQueryResult(ResourceType resourceType,
                                UUID submissionDetailId,
                                String submissionType,
                                String status,
                                String resourceName,
                                String centreOfInterestQuadBlock,
                                Integer prospectOrDiscoveryOrganisationResponsibleId,
                                List<Integer> licencesTheResourceIsOn,
                                boolean updateRequested,
                                boolean isCorrection,
                                Integer fieldIdentifier,
                                Integer parentFieldId,
                                FieldStatus fieldStatus,
                                String licenceApplicationReference) {
    this.resourceType = resourceType;
    this.submissionDetailId = submissionDetailId;
    this.submissionType = SubmissionType.valueOf(submissionType);
    this.resourceName = resourceName;
    this.centreOfInterestQuadBlock = centreOfInterestQuadBlock;
    this.prospectOrDiscoveryOrganisationResponsibleId = prospectOrDiscoveryOrganisationResponsibleId;
    this.licencesTheResourceIsOn = licencesTheResourceIsOn;
    this.status = SubmissionDetailStatus.valueOf(status);
    this.updateRequested = updateRequested;
    this.isCorrection = isCorrection;
    this.fieldIdentifier = fieldIdentifier;
    this.parentFieldId = parentFieldId;
    this.fieldStatus = fieldStatus;
    this.licenceApplicationReference = licenceApplicationReference;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  UUID getSubmissionDetailId() {
    return submissionDetailId;
  }

  SubmissionType getSubmissionType() {
    return submissionType;
  }

  String getResourceName() {
    return resourceName;
  }

  String getCentreOfInterestQuadBlock() {
    return centreOfInterestQuadBlock;
  }

  Integer getProspectOrDiscoveryOrganisationResponsibleId() {
    return prospectOrDiscoveryOrganisationResponsibleId;
  }

  SubmissionDetailStatus getStatus() {
    return status;
  }

  List<Integer> getLicencesTheResourceIsOn() {
    return licencesTheResourceIsOn;
  }

  public boolean isUpdateRequested() {
    return updateRequested;
  }

  public boolean isCorrection() {
    return isCorrection;
  }

  public Integer getFieldIdentifier() {
    return fieldIdentifier;
  }

  public Integer getParentFieldId() {
    return parentFieldId;
  }

  public FieldStatus getFieldStatus() {
    return fieldStatus;
  }

  public String getLicenceApplicationReference() {
    return licenceApplicationReference;
  }
}
