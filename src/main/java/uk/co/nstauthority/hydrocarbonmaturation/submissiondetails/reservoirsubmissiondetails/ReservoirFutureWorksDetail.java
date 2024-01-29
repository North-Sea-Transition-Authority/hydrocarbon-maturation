package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "reservoir_future_works_detail")
public class ReservoirFutureWorksDetail {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  private Boolean isReservoirBeingDevelopedUnderFuturePhase;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ReservoirSubmissionDetail getReservoirSubmissionDetail() {
    return reservoirSubmissionDetail;
  }

  public void setReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    this.reservoirSubmissionDetail = reservoirSubmissionDetail;
  }

  public Boolean isReservoirBeingDevelopedUnderFuturePhase() {
    return isReservoirBeingDevelopedUnderFuturePhase;
  }

  public void setReservoirBeingDevelopedUnderFuturePhase(Boolean reservoirBeingDevelopedUnderFuturePhase) {
    isReservoirBeingDevelopedUnderFuturePhase = reservoirBeingDevelopedUnderFuturePhase;
  }
}
