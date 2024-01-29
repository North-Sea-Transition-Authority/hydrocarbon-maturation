package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import jakarta.persistence.Column;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Entity
@Table(name = "drill_stem_tests")
public class DrillStemTest implements ReservoirSubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @Enumerated(EnumType.STRING)
  @Column(name = "has_drill_stem_test")
  private DrillStemTestSummaryOptions drillStemTestSummaryOption;

  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  public ReservoirSubmissionDetail getReservoirSubmissionDetail() {
    return reservoirSubmissionDetail;
  }

  @Override
  public void setReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    this.reservoirSubmissionDetail = reservoirSubmissionDetail;
  }

  public DrillStemTestSummaryOptions getDrillStemTestSummaryOption() {
    return drillStemTestSummaryOption;
  }

  public void setDrillStemTestSummaryOption(
      DrillStemTestSummaryOptions drillStemTestSummaryOptions) {
    this.drillStemTestSummaryOption = drillStemTestSummaryOptions;
  }
}

