package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

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

@Entity
@Table(name = "hydrocarbon_case_details")
class HydrocarbonCaseDetail {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private Boolean oilOnly;
  private BigDecimal oilOnlyProbability;
  private String oilOnlyComments;
  private Boolean gasOnly;
  private BigDecimal gasOnlyProbability;
  private String gasOnlyComments;
  private Boolean oilAndGas;
  private BigDecimal oilAndGasProbability;
  private String oilAndGasComments;

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

  public Boolean getOilOnly() {
    return oilOnly;
  }

  public void setOilOnly(Boolean oilOnly) {
    this.oilOnly = oilOnly;
  }

  public BigDecimal getOilOnlyProbability() {
    return oilOnlyProbability;
  }

  public void setOilOnlyProbability(BigDecimal oilOnlyProbability) {
    this.oilOnlyProbability = oilOnlyProbability;
  }

  public String getOilOnlyComments() {
    return oilOnlyComments;
  }

  public void setOilOnlyComments(String oilOnlyComments) {
    this.oilOnlyComments = oilOnlyComments;
  }

  public Boolean getGasOnly() {
    return gasOnly;
  }

  public void setGasOnly(Boolean gasOnly) {
    this.gasOnly = gasOnly;
  }

  public BigDecimal getGasOnlyProbability() {
    return gasOnlyProbability;
  }

  public void setGasOnlyProbability(BigDecimal gasOnlyProbability) {
    this.gasOnlyProbability = gasOnlyProbability;
  }

  public String getGasOnlyComments() {
    return gasOnlyComments;
  }

  public void setGasOnlyComments(String gasOnlyComments) {
    this.gasOnlyComments = gasOnlyComments;
  }

  public Boolean getOilAndGas() {
    return oilAndGas;
  }

  public void setOilAndGas(Boolean oilAndGas) {
    this.oilAndGas = oilAndGas;
  }

  public BigDecimal getOilAndGasProbability() {
    return oilAndGasProbability;
  }

  public void setOilAndGasProbability(BigDecimal oilAndGasProbability) {
    this.oilAndGasProbability = oilAndGasProbability;
  }

  public String getOilAndGasComments() {
    return oilAndGasComments;
  }

  public void setOilAndGasComments(String oilAndGasComments) {
    this.oilAndGasComments = oilAndGasComments;
  }
}
