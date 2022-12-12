package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.DrillStemTestChildEntity;

@Entity
@Table(name = "drill_stem_test_results")
public class DrillStemTestResult implements DrillStemTestChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "drill_stem_test_id")
  private DrillStemTest drillStemTest;

  private String formationName;

  private BigDecimal topDepth;

  private BigDecimal bottomDepth;

  private Integer bhfp;

  private Integer whfp;

  private BigDecimal flowDuration;

  private BigDecimal gasFlowrate;

  private BigDecimal oilFlowrate;

  private BigDecimal waterFlowrate;

  private String commentsInterpretation;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public DrillStemTest getDrillStemTest() {
    return drillStemTest;
  }

  public void setDrillStemTest(
      DrillStemTest drillStemTest) {
    this.drillStemTest = drillStemTest;
  }

  public String getFormationName() {
    return formationName;
  }

  public void setFormationName(String formationName) {
    this.formationName = formationName;
  }

  public BigDecimal getTopDepth() {
    return topDepth;
  }

  public void setTopDepth(BigDecimal topDepth) {
    this.topDepth = topDepth;
  }

  public BigDecimal getBottomDepth() {
    return bottomDepth;
  }

  public void setBottomDepth(BigDecimal bottomDepth) {
    this.bottomDepth = bottomDepth;
  }

  public Integer getBhfp() {
    return bhfp;
  }

  public void setBhfp(Integer bhfp) {
    this.bhfp = bhfp;
  }

  public Integer getWhfp() {
    return whfp;
  }

  public void setWhfp(Integer whfp) {
    this.whfp = whfp;
  }

  public BigDecimal getFlowDuration() {
    return flowDuration;
  }

  public void setFlowDuration(BigDecimal flowDuration) {
    this.flowDuration = flowDuration;
  }

  public BigDecimal getGasFlowrate() {
    return gasFlowrate;
  }

  public void setGasFlowrate(BigDecimal gasFlowrate) {
    this.gasFlowrate = gasFlowrate;
  }

  public BigDecimal getOilFlowrate() {
    return oilFlowrate;
  }

  public void setOilFlowrate(BigDecimal oilFlowrate) {
    this.oilFlowrate = oilFlowrate;
  }

  public BigDecimal getWaterFlowrate() {
    return waterFlowrate;
  }

  public void setWaterFlowrate(BigDecimal waterFlowrate) {
    this.waterFlowrate = waterFlowrate;
  }

  public String getCommentsInterpretation() {
    return commentsInterpretation;
  }

  public void setCommentsInterpretation(String commentsInterpretation) {
    this.commentsInterpretation = commentsInterpretation;
  }
}
