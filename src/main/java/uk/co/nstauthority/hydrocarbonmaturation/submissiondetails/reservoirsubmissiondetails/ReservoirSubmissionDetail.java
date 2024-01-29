package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "reservoir_submission_details")
public class ReservoirSubmissionDetail implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir")
  private Reservoir reservoir;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @Enumerated(EnumType.STRING)
  private ReservoirSubmissionDetailStatus status;

  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  public Reservoir getReservoir() {
    return reservoir;
  }

  public void setReservoir(Reservoir reservoir) {
    this.reservoir = reservoir;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  @Override
  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public ReservoirSubmissionDetailStatus getStatus() {
    return status;
  }

  public void setStatus(
      ReservoirSubmissionDetailStatus status) {
    this.status = status;
  }
}
