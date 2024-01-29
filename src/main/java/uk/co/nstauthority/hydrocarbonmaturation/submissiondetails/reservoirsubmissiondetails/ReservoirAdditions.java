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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Entity
@Table(name = "reservoir_additions")
public class ReservoirAdditions {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "submission_detail_id")
  @ManyToOne
  private SubmissionDetail submissionDetail;

  @Enumerated(EnumType.STRING)
  private ReservoirAdditionStatus reservoirAdditionStatus;

  public UUID getId() {
    return id;
  }

  public ReservoirAdditions setId(UUID id) {
    this.id = id;
    return this;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public ReservoirAdditions setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
    return this;
  }

  public ReservoirAdditionStatus getReservoirAdditionStatus() {
    return reservoirAdditionStatus;
  }

  public ReservoirAdditions setReservoirAdditionStatus(ReservoirAdditionStatus reservoirAdditionStatus) {
    this.reservoirAdditionStatus = reservoirAdditionStatus;
    return this;
  }
}
