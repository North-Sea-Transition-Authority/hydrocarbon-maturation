package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "considering_ccus_detail")
public class ConsideringCcusDetail implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean isConnectedToProject;

  private String connectedProjects;

  private Boolean isAnyShallowerHorizons;

  private String ageAndFormationOfHorizons;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public Boolean getIsConnectedToProject() {
    return isConnectedToProject;
  }

  public void setIsConnectedToProject(Boolean connectedToProject) {
    isConnectedToProject = connectedToProject;
  }

  public String getConnectedProjects() {
    return connectedProjects;
  }

  public void setConnectedProjects(String connectedProjects) {
    this.connectedProjects = connectedProjects;
  }

  public Boolean getIsAnyShallowerHorizons() {
    return isAnyShallowerHorizons;
  }

  public void setIsAnyShallowerHorizons(Boolean anyShallowerHorizons) {
    isAnyShallowerHorizons = anyShallowerHorizons;
  }

  public String getAgeAndFormationOfHorizons() {
    return ageAndFormationOfHorizons;
  }

  public void setAgeAndFormationOfHorizons(String ageAndFormationOfHorizons) {
    this.ageAndFormationOfHorizons = ageAndFormationOfHorizons;
  }
}
