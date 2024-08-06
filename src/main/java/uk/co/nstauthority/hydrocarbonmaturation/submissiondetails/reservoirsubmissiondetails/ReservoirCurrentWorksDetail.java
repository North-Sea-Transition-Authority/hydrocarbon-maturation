package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSubmissionDetailChildEntity;

@Entity
@Table(name = "reservoir_current_works_detail")
public class ReservoirCurrentWorksDetail implements ReservoirSubmissionDetailChildEntity {

  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  private Boolean isReservoirBeingDevelopedUnderThisPhase;

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

  public Boolean getIsReservoirBeingDevelopedUnderThisPhase() {
    return isReservoirBeingDevelopedUnderThisPhase;
  }

  public void setIsReservoirBeingDevelopedUnderThisPhase(Boolean reservoirBeingDevelopedUnderFuturePhase) {
    isReservoirBeingDevelopedUnderThisPhase = reservoirBeingDevelopedUnderFuturePhase;
  }
}
