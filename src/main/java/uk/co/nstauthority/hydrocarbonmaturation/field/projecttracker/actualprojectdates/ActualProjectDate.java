package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

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
@Table(name = "actual_project_dates")
@Audited
class ActualProjectDate {
  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  @Enumerated(EnumType.STRING)
  private ActualProjectDateType actualProjectDateType;

  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private ActualProjectDateStatus actualProjectDateStatus;

  public ActualProjectDate() {
  }

  public ActualProjectDate(UUID id) {
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

  public ActualProjectDateType getProjectDateType() {
    return actualProjectDateType;
  }

  public void setProjectDateType(ActualProjectDateType actualProjectDateType) {
    this.actualProjectDateType = actualProjectDateType;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public ActualProjectDateStatus getProjectDateStatus() {
    return actualProjectDateStatus;
  }

  public void setProjectDateStatus(ActualProjectDateStatus actualProjectDateStatus) {
    this.actualProjectDateStatus = actualProjectDateStatus;
  }
}
