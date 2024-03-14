package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory;

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
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.ProjectStage;

@Entity
@Table(name = "project_stage_history")
public class ProjectStageHistory {
  @Id
  @UuidGenerator
  private UUID id;

  @JoinColumn(name = "project_id")
  @ManyToOne
  private Project project;

  @Enumerated(EnumType.STRING)
  private ProjectStage stage;

  private LocalDate stageDate;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public ProjectStage getStage() {
    return stage;
  }

  public void setStage(ProjectStage stage) {
    this.stage = stage;
  }

  public LocalDate getStageDate() {
    return stageDate;
  }

  public void setStageDate(LocalDate stageDate) {
    this.stageDate = stageDate;
  }
}
