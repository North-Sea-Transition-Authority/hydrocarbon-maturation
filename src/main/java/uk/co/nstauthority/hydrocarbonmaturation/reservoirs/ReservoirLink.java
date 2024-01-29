package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Entity
@Table(name = "reservoir_links")
public class ReservoirLink implements ReservoirSubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @ManyToOne
  @JoinColumn(name = "linked_reservoir_id")
  private Reservoir linkedReservoir;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ReservoirSubmissionDetail getReservoirSubmissionDetail() {
    return reservoirSubmissionDetail;
  }

  public void setReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    this.reservoirSubmissionDetail = reservoirSubmissionDetail;
  }

  public Reservoir getLinkedReservoir() {
    return linkedReservoir;
  }

  public void setLinkedReservoir(Reservoir discoveryReservoir) {
    this.linkedReservoir = discoveryReservoir;
  }
}
