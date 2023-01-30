package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class DrillStemTestResultForm implements SubmissionForm {

  private String testNumber;
  private String formationName;
  private DecimalInput topDepth = new DecimalInput();
  private DecimalInput bottomDepth = new DecimalInput();
  private DecimalInput bhfp = new DecimalInput();
  private DecimalInput whfp = new DecimalInput();
  private DecimalInput flowDuration = new DecimalInput();
  private DecimalInput gasFlowrate = new DecimalInput();
  private DecimalInput oilFlowrate = new DecimalInput();
  private DecimalInput waterFlowrate = new DecimalInput();
  private String commentsInterpretation;

  public String getTestNumber() {
    return testNumber;
  }

  public void setTestNumber(String testNumber) {
    this.testNumber = testNumber;
  }

  public String getFormationName() {
    return formationName;
  }

  public void setFormationName(String formationName) {
    this.formationName = formationName;
  }

  public DecimalInput getTopDepth() {
    return topDepth;
  }

  public void setTopDepth(DecimalInput topDepth) {
    this.topDepth = topDepth;
  }

  public DecimalInput getBottomDepth() {
    return bottomDepth;
  }

  public void setBottomDepth(DecimalInput bottomDepth) {
    this.bottomDepth = bottomDepth;
  }

  public DecimalInput getBhfp() {
    return bhfp;
  }

  public void setBhfp(DecimalInput bhfp) {
    this.bhfp = bhfp;
  }

  public DecimalInput getWhfp() {
    return whfp;
  }

  public void setWhfp(DecimalInput whfp) {
    this.whfp = whfp;
  }

  public DecimalInput getFlowDuration() {
    return flowDuration;
  }

  public void setFlowDuration(DecimalInput flowDuration) {
    this.flowDuration = flowDuration;
  }

  public DecimalInput getGasFlowrate() {
    return gasFlowrate;
  }

  public void setGasFlowrate(DecimalInput gasFlowrate) {
    this.gasFlowrate = gasFlowrate;
  }

  public DecimalInput getOilFlowrate() {
    return oilFlowrate;
  }

  public void setOilFlowrate(DecimalInput oilFlowrate) {
    this.oilFlowrate = oilFlowrate;
  }

  public DecimalInput getWaterFlowrate() {
    return waterFlowrate;
  }

  public void setWaterFlowrate(DecimalInput waterFlowrate) {
    this.waterFlowrate = waterFlowrate;
  }

  public String getCommentsInterpretation() {
    return commentsInterpretation;
  }

  public void setCommentsInterpretation(String commentsInterpretation) {
    this.commentsInterpretation = commentsInterpretation;
  }
}
