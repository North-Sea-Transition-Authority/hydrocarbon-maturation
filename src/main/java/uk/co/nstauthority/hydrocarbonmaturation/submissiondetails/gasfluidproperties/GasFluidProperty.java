package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Column;
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
@Table(name = "gas_fluid_properties")
class GasFluidProperty implements ReservoirSubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_submission_detail_id")
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  private BigDecimal gasSpecificGravity;

  @Column(name = "gas_h2s")
  private BigDecimal gasH2s;

  private BigDecimal gasCo2;

  @Column(name = "gas_n2")
  private BigDecimal gasN2;

  private Boolean condensatePresent;

  private BigDecimal condensateGravity;

  private BigDecimal condensateViscosity;

  private BigDecimal condensateSulphur;

  private BigDecimal condensateWax;

  private BigDecimal condensateOtherInclWax;

  private String adviseOtherGasesConcentration;

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

  public BigDecimal getGasSpecificGravity() {
    return gasSpecificGravity;
  }

  public void setGasSpecificGravity(BigDecimal gasSpecificGravity) {
    this.gasSpecificGravity = gasSpecificGravity;
  }

  public BigDecimal getGasH2s() {
    return gasH2s;
  }

  public void setGasH2s(BigDecimal gasH2s) {
    this.gasH2s = gasH2s;
  }

  public BigDecimal getGasCo2() {
    return gasCo2;
  }

  public void setGasCo2(BigDecimal gasCo2) {
    this.gasCo2 = gasCo2;
  }

  public BigDecimal getGasN2() {
    return gasN2;
  }

  public void setGasN2(BigDecimal gasN2) {
    this.gasN2 = gasN2;
  }

  public Boolean getCondensatePresent() {
    return condensatePresent;
  }

  public void setCondensatePresent(Boolean condensatePresent) {
    this.condensatePresent = condensatePresent;
  }

  public BigDecimal getCondensateGravity() {
    return condensateGravity;
  }

  public void setCondensateGravity(BigDecimal condensateGravity) {
    this.condensateGravity = condensateGravity;
  }

  public BigDecimal getCondensateViscosity() {
    return condensateViscosity;
  }

  public void setCondensateViscosity(BigDecimal condensateViscosity) {
    this.condensateViscosity = condensateViscosity;
  }

  public BigDecimal getCondensateSulphur() {
    return condensateSulphur;
  }

  public void setCondensateSulphur(BigDecimal condensateSulphur) {
    this.condensateSulphur = condensateSulphur;
  }

  public BigDecimal getCondensateWax() {
    return condensateWax;
  }

  public void setCondensateWax(BigDecimal condensateWax) {
    this.condensateWax = condensateWax;
  }

  public BigDecimal getCondensateOtherInclWax() {
    return condensateOtherInclWax;
  }

  public void setCondensateOtherInclWax(BigDecimal condensateOtherInclWax) {
    this.condensateOtherInclWax = condensateOtherInclWax;
  }

  public String getAdviseOtherGasesConcentration() {
    return adviseOtherGasesConcentration;
  }

  public void setAdviseOtherGasesConcentration(String adviseOtherGasesConcentration) {
    this.adviseOtherGasesConcentration = adviseOtherGasesConcentration;
  }
}
