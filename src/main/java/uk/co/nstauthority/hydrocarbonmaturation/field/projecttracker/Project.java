package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Entity
@Table(name = "projects")
@Audited
public class Project {
  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "field_identifier")
  @Audited(targetAuditMode = NOT_AUDITED)
  private Field field;

  private String projectName;

  @Enumerated(value = EnumType.STRING)
  private ProjectType projectType;

  @Enumerated(value = EnumType.STRING)
  private ProjectStatus projectStatus;

  private Boolean isPartOfPipeline;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  @Audited(targetAuditMode = NOT_AUDITED)
  private SubmissionDetail submissionDetail;

  private Instant createdTimestamp;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public ProjectType getProjectType() {
    return projectType;
  }

  public void setProjectType(ProjectType projectType) {
    this.projectType = projectType;
  }

  public ProjectStatus getProjectStatus() {
    return projectStatus;
  }

  public void setProjectStatus(ProjectStatus projectStatus) {
    this.projectStatus = projectStatus;
  }

  public Boolean getPartOfPipeline() {
    return isPartOfPipeline;
  }

  public void setPartOfPipeline(Boolean partOfPipeline) {
    isPartOfPipeline = partOfPipeline;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public Instant getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Instant createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }
}
