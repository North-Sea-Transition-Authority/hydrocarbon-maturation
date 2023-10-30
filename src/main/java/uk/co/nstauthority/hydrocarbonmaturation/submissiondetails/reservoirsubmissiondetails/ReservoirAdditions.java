package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
