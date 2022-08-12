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
public class HydrocarbonCase {

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

  public HydrocarbonCase setId(UUID id) {
    this.id = id;
    return this;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public HydrocarbonCase setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
    return this;
  }

  public Boolean getOilOnly() {
    return oilOnly;
  }

  public HydrocarbonCase setOilOnly(Boolean oilOnly) {
    this.oilOnly = oilOnly;
    return this;
  }

  public BigDecimal getOilOnlyProbability() {
    return oilOnlyProbability;
  }

  public HydrocarbonCase setOilOnlyProbability(BigDecimal oilOnlyProbability) {
    this.oilOnlyProbability = oilOnlyProbability;
    return this;
  }

  public String getOilOnlyComments() {
    return oilOnlyComments;
  }

  public HydrocarbonCase setOilOnlyComments(String oilOnlyComments) {
    this.oilOnlyComments = oilOnlyComments;
    return this;
  }

  public Boolean getGasOnly() {
    return gasOnly;
  }

  public HydrocarbonCase setGasOnly(Boolean gasOnly) {
    this.gasOnly = gasOnly;
    return this;
  }

  public BigDecimal getGasOnlyProbability() {
    return gasOnlyProbability;
  }

  public HydrocarbonCase setGasOnlyProbability(BigDecimal gasOnlyProbability) {
    this.gasOnlyProbability = gasOnlyProbability;
    return this;
  }

  public String getGasOnlyComments() {
    return gasOnlyComments;
  }

  public HydrocarbonCase setGasOnlyComments(String gasOnlyComments) {
    this.gasOnlyComments = gasOnlyComments;
    return this;
  }

  public Boolean getOilAndGas() {
    return oilAndGas;
  }

  public HydrocarbonCase setOilAndGas(Boolean oilAndGas) {
    this.oilAndGas = oilAndGas;
    return this;
  }

  public BigDecimal getOilAndGasProbability() {
    return oilAndGasProbability;
  }

  public HydrocarbonCase setOilAndGasProbability(BigDecimal oilAndGasProbability) {
    this.oilAndGasProbability = oilAndGasProbability;
    return this;
  }

  public String getOilAndGasComments() {
    return oilAndGasComments;
  }

  public HydrocarbonCase setOilAndGasComments(String oilAndGasComments) {
    this.oilAndGasComments = oilAndGasComments;
    return this;
  }
}
