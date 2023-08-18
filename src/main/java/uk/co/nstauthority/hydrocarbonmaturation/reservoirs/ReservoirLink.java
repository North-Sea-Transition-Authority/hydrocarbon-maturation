package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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

  @JoinColumn(name = "reservoir_submission_detail_id")
  @ManyToOne
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @JoinColumn(name = "linked_reservoir_id")
  @ManyToOne
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
