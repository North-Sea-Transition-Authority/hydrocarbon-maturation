package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "planned_unsanctioned_wells")
public class PlannedUnsanctionedWells implements SubmissionDetailChildEntity {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private BigDecimal numberOfPlannedUnsanctionedWells;

  private BigDecimal totalCapex;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public BigDecimal getNumberOfPlannedUnsanctionedWells() {
    return numberOfPlannedUnsanctionedWells;
  }

  public void setNumberOfPlannedUnsanctionedWells(BigDecimal numberOfPlannedUnsanctionedWells) {
    this.numberOfPlannedUnsanctionedWells = numberOfPlannedUnsanctionedWells;
  }

  public BigDecimal getTotalCapex() {
    return totalCapex;
  }

  public void setTotalCapex(BigDecimal totalCapex) {
    this.totalCapex = totalCapex;
  }
}
