package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Entity
@Table(name = "oil_fluid_properties")
class OilFluidProperty implements ReservoirSubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  private Integer oilGravity;

  private BigDecimal oilViscosity;

  private Integer oilSulphur;

  private BigDecimal oilWax;

  private Integer oilOtherWaxAppearanceTemperature;

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

  public Integer getOilGravity() {
    return oilGravity;
  }

  public void setOilGravity(Integer oilGravity) {
    this.oilGravity = oilGravity;
  }

  public BigDecimal getOilViscosity() {
    return oilViscosity;
  }

  public void setOilViscosity(BigDecimal oilViscosity) {
    this.oilViscosity = oilViscosity;
  }

  public Integer getOilSulphur() {
    return oilSulphur;
  }

  public void setOilSulphur(Integer oilSulphur) {
    this.oilSulphur = oilSulphur;
  }

  public BigDecimal getOilWax() {
    return oilWax;
  }

  public void setOilWax(BigDecimal oilWax) {
    this.oilWax = oilWax;
  }

  public Integer getOilOtherWaxAppearanceTemperature() {
    return oilOtherWaxAppearanceTemperature;
  }

  public void setOilOtherWaxAppearanceTemperature(Integer oilOtherWaxAppearanceTemperature) {
    this.oilOtherWaxAppearanceTemperature = oilOtherWaxAppearanceTemperature;
  }
}
