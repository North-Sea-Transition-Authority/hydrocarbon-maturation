package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

class SubmissionWorkAreaQueryResultTestUtil {

  private SubmissionWorkAreaQueryResultTestUtil() {
    throw new IllegalUtilClassInstantiationException(SubmissionWorkAreaQueryResultTestUtil.class);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private UUID submissionDetailId = UUID.randomUUID();
    private String submissionType = "CREATE_PROSPECT";
    private String status = "DRAFT";
    private String resourceName = "resource name";
    private String centreOfInterestQuadBlock = "25/5";
    private Integer organisationResponsibleId = 10;
    private List<Integer> licencesTheResourceIsOn = List.of(1,2);
    private Boolean updateRequested = false;
    private Boolean isCorrection = false;

    private Builder() {
    }

    public Builder withSubmissionDetailId(UUID submissionDetailId) {
      this.submissionDetailId = submissionDetailId;
      return this;
    }

    public Builder withSubmissionType(String submissionType) {
      this.submissionType = submissionType;
      return this;
    }

    public Builder withStatus(String status) {
      this.status = status;
      return this;
    }

    public Builder withResourceName(String resourceName) {
      this.resourceName = resourceName;
      return this;
    }

    public Builder withCentreOfInterestQuadBlock(String centreOfInterestQuadBlock) {
      this.centreOfInterestQuadBlock = centreOfInterestQuadBlock;
      return this;
    }

    public Builder withOrganisationResponsibleId(Integer organisationResponsibleId) {
      this.organisationResponsibleId = organisationResponsibleId;
      return this;
    }

    public Builder withLicencesTheResourceIsOn(List<Integer> licencesTheResourceIsOn) {
      this.licencesTheResourceIsOn = licencesTheResourceIsOn;
      return this;
    }

    public Builder withUpdateRequested(boolean updateRequested) {
      this.updateRequested = updateRequested;
      return this;
    }

    public Builder withCorrection(boolean isCorrection) {
      this.isCorrection = isCorrection;
      return this;
    }

    public SubmissionWorkAreaQueryResult build() {

      return new SubmissionWorkAreaQueryResult(submissionDetailId, submissionType, status,
          resourceName, centreOfInterestQuadBlock, organisationResponsibleId,
          licencesTheResourceIsOn, updateRequested,
          isCorrection);
    }
  }

}
