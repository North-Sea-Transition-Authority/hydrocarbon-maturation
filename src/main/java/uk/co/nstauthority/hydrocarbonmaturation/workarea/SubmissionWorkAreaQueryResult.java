package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

class SubmissionWorkAreaQueryResult {

  private final UUID submissionDetailId;
  private final SubmissionType submissionType;
  private final String resourceName;
  private final String centreOfInterestQuadBlock;
  private final Integer organisationResponsibleId;
  private final List<Integer> licencesTheResourceIsOn;
  private final SubmissionDetailStatus status;
  private final boolean updateRequested;
  private final boolean isCorrection;
  private final Integer parentFieldId;
  private final FieldStatus fieldStatus;

  SubmissionWorkAreaQueryResult(UUID submissionDetailId,
                                String submissionType,
                                String status,
                                String resourceName,
                                String centreOfInterestQuadBlock,
                                Integer organisationResponsibleId,
                                List<Integer> licencesTheResourceIsOn,
                                boolean updateRequested,
                                boolean isCorrection, Integer parentFieldId, FieldStatus fieldStatus) {
    this.submissionDetailId = submissionDetailId;
    this.submissionType = SubmissionType.valueOf(submissionType);
    this.resourceName = resourceName;
    this.centreOfInterestQuadBlock = centreOfInterestQuadBlock;
    this.organisationResponsibleId = organisationResponsibleId;
    this.licencesTheResourceIsOn = licencesTheResourceIsOn;
    this.status = SubmissionDetailStatus.valueOf(status);
    this.updateRequested = updateRequested;
    this.isCorrection = isCorrection;
    this.parentFieldId = parentFieldId;
    this.fieldStatus = fieldStatus;
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

  Integer getOrganisationResponsibleId() {
    return organisationResponsibleId;
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

  public Integer getParentFieldId() {
    return parentFieldId;
  }

  public FieldStatus getFieldStatus() {
    return fieldStatus;
  }
}
