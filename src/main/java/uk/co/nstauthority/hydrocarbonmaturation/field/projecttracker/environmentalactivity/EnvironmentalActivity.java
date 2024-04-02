package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import jakarta.persistence.Entity;
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
@Table(name = "environmental_activity")
@Audited
class EnvironmentalActivity {
  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  private LocalDate submissionDate;

  private LocalDate consultationStartDate;

  private LocalDate consultationClosingDate;

  public EnvironmentalActivity() {
  }

  public EnvironmentalActivity(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public LocalDate getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(LocalDate submissionDate) {
    this.submissionDate = submissionDate;
  }

  public LocalDate getConsultationStartDate() {
    return consultationStartDate;
  }

  public void setConsultationStartDate(LocalDate consultationStartDate) {
    this.consultationStartDate = consultationStartDate;
  }

  public LocalDate getConsultationClosingDate() {
    return consultationClosingDate;
  }

  public void setConsultationClosingDate(LocalDate consultationClosingDate) {
    this.consultationClosingDate = consultationClosingDate;
  }
}
