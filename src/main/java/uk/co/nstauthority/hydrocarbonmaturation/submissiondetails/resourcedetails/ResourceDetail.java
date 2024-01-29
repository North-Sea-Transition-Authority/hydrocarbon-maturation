package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Entity
@Table(name = "resource_details")
public class ResourceDetail implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private String resourceName;

  @Enumerated(EnumType.STRING)
  private OnshoreOffshore onshoreOffshore;

  private Integer organisationId;

  private String organisationName;

  private String organisationRegisteredNumber;

  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  @Override
  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(
      OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }

  public Integer getOrganisationId() {
    return organisationId;
  }

  public void setOrganisationId(Integer organisationId) {
    this.organisationId = organisationId;
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
}
