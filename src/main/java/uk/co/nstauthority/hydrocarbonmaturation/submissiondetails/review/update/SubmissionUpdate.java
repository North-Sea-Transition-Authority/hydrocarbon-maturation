package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Entity
@Table(name = "submission_update")
public class SubmissionUpdate {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_id")
  private Submission submission;

  private Instant requestedTimestamp;

  private Integer requestedByWuaId;

  private String reason;

  private Boolean resolved;

  private Instant resolvedTimestamp;

  private Integer resolvedByWuaId;

  public UUID getId() {
    return id;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public Instant getRequestedTimestamp() {
    return requestedTimestamp;
  }

  public void setRequestedTimestamp(Instant requestedTimestamp) {
    this.requestedTimestamp = requestedTimestamp;
  }

  public Integer getRequestedByWuaId() {
    return requestedByWuaId;
  }

  public void setRequestedByWuaId(Integer requestedByWuaId) {
    this.requestedByWuaId = requestedByWuaId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Boolean getResolved() {
    return resolved;
  }

  public void setResolved(Boolean resolved) {
    this.resolved = resolved;
  }

  public Instant getResolvedTimestamp() {
    return resolvedTimestamp;
  }

  public void setResolvedTimestamp(Instant resolvedTimestamp) {
    this.resolvedTimestamp = resolvedTimestamp;
  }

  public Integer getResolvedByWuaId() {
    return resolvedByWuaId;
  }

  public void setResolvedByWuaId(Integer resolvedByWuaId) {
    this.resolvedByWuaId = resolvedByWuaId;
  }
}
