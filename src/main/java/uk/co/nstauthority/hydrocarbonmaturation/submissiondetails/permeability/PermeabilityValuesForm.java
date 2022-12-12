package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class PermeabilityValuesForm implements SubmissionForm {

  private DecimalInput permeabilityMin = new DecimalInput();
  private DecimalInput permeabilityMax = new DecimalInput();
  private DecimalInput permeabilityAverage = new DecimalInput();
  private AverageMethod averageMethodUsed;
  private PermeabilitySource permeabilitySource;
  private String otherPermeabilitySource;
  private String permeabilityComments;
  private DecimalInput kvKhMin = new DecimalInput();
  private DecimalInput kvKhMax = new DecimalInput();
  private DecimalInput kvKhAverage = new DecimalInput();
  private AverageMethod kvKhAverageMethodUsed;
  private PermeabilitySource kvKhSource;
  private String otherKvKhSource;
  private String kvKhComments;

  public DecimalInput getPermeabilityMin() {
    return permeabilityMin;
  }

  public void setPermeabilityMin(DecimalInput permeabilityMin) {
    this.permeabilityMin = permeabilityMin;
  }

  public DecimalInput getPermeabilityMax() {
    return permeabilityMax;
  }

  public void setPermeabilityMax(DecimalInput permeabilityMax) {
    this.permeabilityMax = permeabilityMax;
  }

  public DecimalInput getPermeabilityAverage() {
    return permeabilityAverage;
  }

  public void setPermeabilityAverage(DecimalInput permeabilityAverage) {
    this.permeabilityAverage = permeabilityAverage;
  }

  public AverageMethod getAverageMethodUsed() {
    return averageMethodUsed;
  }

  public void setAverageMethodUsed(
      AverageMethod averageMethodUsed) {
    this.averageMethodUsed = averageMethodUsed;
  }

  public PermeabilitySource getPermeabilitySource() {
    return permeabilitySource;
  }

  public void setPermeabilitySource(
      PermeabilitySource permeabilitySource) {
    this.permeabilitySource = permeabilitySource;
  }

  public String getPermeabilityComments() {
    return permeabilityComments;
  }

  public void setPermeabilityComments(String permeabilityComments) {
    this.permeabilityComments = permeabilityComments;
  }

  public DecimalInput getKvKhMin() {
    return kvKhMin;
  }

  public void setKvKhMin(DecimalInput kvKhMin) {
    this.kvKhMin = kvKhMin;
  }

  public DecimalInput getKvKhMax() {
    return kvKhMax;
  }

  public void setKvKhMax(DecimalInput kvKhMax) {
    this.kvKhMax = kvKhMax;
  }

  public DecimalInput getKvKhAverage() {
    return kvKhAverage;
  }

  public void setKvKhAverage(DecimalInput kvKhAverage) {
    this.kvKhAverage = kvKhAverage;
  }

  public AverageMethod getKvKhAverageMethodUsed() {
    return kvKhAverageMethodUsed;
  }

  public void setKvKhAverageMethodUsed(
      AverageMethod kvKhAverageMethodUsed) {
    this.kvKhAverageMethodUsed = kvKhAverageMethodUsed;
  }

  public PermeabilitySource getKvKhSource() {
    return kvKhSource;
  }

  public void setKvKhSource(
      PermeabilitySource kvKhSource) {
    this.kvKhSource = kvKhSource;
  }

  public String getKvKhComments() {
    return kvKhComments;
  }

  public void setKvKhComments(String kvKhComments) {
    this.kvKhComments = kvKhComments;
  }

  public String getOtherPermeabilitySource() {
    return otherPermeabilitySource;
  }

  public void setOtherPermeabilitySource(String otherPermeabilitySource) {
    this.otherPermeabilitySource = otherPermeabilitySource;
  }

  public String getOtherKvKhSource() {
    return otherKvKhSource;
  }

  public void setOtherKvKhSource(String otherKvKhSource) {
    this.otherKvKhSource = otherKvKhSource;
  }
}
