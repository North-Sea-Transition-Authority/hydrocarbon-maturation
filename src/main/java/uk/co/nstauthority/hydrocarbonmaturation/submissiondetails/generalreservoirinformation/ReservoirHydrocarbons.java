package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Entity
@Table(name = "reservoir_hydrocarbons")
public class ReservoirHydrocarbons implements ReservoirSubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @Enumerated(EnumType.STRING)
  private HydrocarbonsDiscovered hydrocarbonsDiscovered;

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

  public HydrocarbonsDiscovered getHydrocarbonsDiscovered() {
    return hydrocarbonsDiscovered;
  }

  public void setHydrocarbonsDiscovered(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    this.hydrocarbonsDiscovered = hydrocarbonsDiscovered;
  }
}
