package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Entity
@Table(name = "project_submission_estimates")
@Audited
class SubmissionEstimates {
  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  @Enumerated(EnumType.STRING)
  private SubmissionEstimatesType submissionEstimateType;

  private LocalDate earliestDate;

  private LocalDate mostLikelyDate;

  private LocalDate latestDate;

  private LocalDate dateWhenEstimated;

  @Enumerated(EnumType.STRING)
  private SubmissionEstimateStatus submissionEstimateStatus;

  public UUID getId() {
    return id;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public SubmissionEstimatesType getSubmissionEstimateType() {
    return submissionEstimateType;
  }

  public void setSubmissionEstimateType(SubmissionEstimatesType submissionEstimatesType) {
    this.submissionEstimateType = submissionEstimatesType;
  }

  public LocalDate getEarliestDate() {
    return earliestDate;
  }

  public void setEarliestDate(LocalDate earliestDate) {
    this.earliestDate = earliestDate;
  }

  public LocalDate getMostLikelyDate() {
    return mostLikelyDate;
  }

  public void setMostLikelyDate(LocalDate mostLikelyDate) {
    this.mostLikelyDate = mostLikelyDate;
  }

  public LocalDate getLatestDate() {
    return latestDate;
  }

  public void setLatestDate(LocalDate latestDate) {
    this.latestDate = latestDate;
  }

  public LocalDate getDateWhenEstimated() {
    return dateWhenEstimated;
  }

  public void setDateWhenEstimated(LocalDate dateWhenEstimated) {
    this.dateWhenEstimated = dateWhenEstimated;
  }

  public SubmissionEstimateStatus getSubmissionEstimateStatus() {
    return submissionEstimateStatus;
  }

  public void setSubmissionEstimateStatus(SubmissionEstimateStatus submissionEstimateStatus) {
    this.submissionEstimateStatus = submissionEstimateStatus;
  }
}
