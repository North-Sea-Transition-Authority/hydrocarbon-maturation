package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

public class ProbabilityOfExceedanceForm {


  // error field is not used for input. It is used to bind the error message to.
  private String error;
  private DecimalInput min = new DecimalInput();
  private DecimalInput p99 = new DecimalInput();
  private DecimalInput p90 = new DecimalInput();
  private DecimalInput mode = new DecimalInput();
  private DecimalInput p50 = new DecimalInput();
  private DecimalInput mean = new DecimalInput();
  private DecimalInput p10 = new DecimalInput();
  private DecimalInput p1 = new DecimalInput();
  private DecimalInput max = new DecimalInput();
  private DecimalInput constant = new DecimalInput();
  private DecimalInput mostLikely = new DecimalInput();

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public DecimalInput getMin() {
    return min;
  }

  public void setMin(DecimalInput min) {
    this.min = min;
  }

  public DecimalInput getP99() {
    return p99;
  }

  public void setP99(DecimalInput p99) {
    this.p99 = p99;
  }

  public DecimalInput getP90() {
    return p90;
  }

  public void setP90(DecimalInput p90) {
    this.p90 = p90;
  }

  public DecimalInput getMode() {
    return mode;
  }

  public void setMode(DecimalInput mode) {
    this.mode = mode;
  }

  public DecimalInput getP50() {
    return p50;
  }

  public void setP50(DecimalInput p50) {
    this.p50 = p50;
  }

  public DecimalInput getMean() {
    return mean;
  }

  public void setMean(DecimalInput mean) {
    this.mean = mean;
  }

  public DecimalInput getP10() {
    return p10;
  }

  public void setP10(DecimalInput p10) {
    this.p10 = p10;
  }

  public DecimalInput getP1() {
    return p1;
  }

  public void setP1(DecimalInput p1) {
    this.p1 = p1;
  }

  public DecimalInput getMax() {
    return max;
  }

  public void setMax(DecimalInput max) {
    this.max = max;
  }

  public DecimalInput getConstant() {
    return constant;
  }

  public void setConstant(DecimalInput constant) {
    this.constant = constant;
  }

  public DecimalInput getMostLikely() {
    return mostLikely;
  }

  public void setMostLikely(DecimalInput mostLikely) {
    this.mostLikely = mostLikely;
  }
}
