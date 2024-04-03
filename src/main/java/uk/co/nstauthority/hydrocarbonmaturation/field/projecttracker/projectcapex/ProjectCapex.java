package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Entity
@Table(name = "project_capex")
@Audited
class ProjectCapex {
  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  private LocalDate dateProvided;

  private BigDecimal totalCapex;

  @Enumerated(EnumType.STRING)
  private ProjectCapexSource sourceOfInformation;

  @Enumerated(EnumType.STRING)
  @Column(name = "project_capex_status")
  private ProjectCapexStatus status;

  public ProjectCapex() {
  }

  public ProjectCapex(UUID id) {
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

  public LocalDate getDateProvided() {
    return dateProvided;
  }

  public void setDateProvided(LocalDate dateProvided) {
    this.dateProvided = dateProvided;
  }

  public BigDecimal getTotalCapex() {
    return totalCapex;
  }

  public void setTotalCapex(BigDecimal totalCapex) {
    this.totalCapex = totalCapex;
  }

  public ProjectCapexSource getSourceOfInformation() {
    return sourceOfInformation;
  }

  public void setSourceOfInformation(ProjectCapexSource sourceOfInformation) {
    this.sourceOfInformation = sourceOfInformation;
  }

  public ProjectCapexStatus getStatus() {
    return status;
  }

  public void setStatus(ProjectCapexStatus status) {
    this.status = status;
  }
}
