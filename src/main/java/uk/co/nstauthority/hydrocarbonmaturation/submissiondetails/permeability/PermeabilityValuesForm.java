package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class PermeabilityValuesForm implements SubmissionForm {

  private DecimalInput permeabilityMin = new DecimalInput();
  private DecimalInput permeabilityMax = new DecimalInput();
  private DecimalInput permeabilityAverage = new DecimalInput();
  private AverageMethod averageMethodUsed;
  private PermeabilitySource permeabilitySource;
  private String otherPermeabilitySource;
  private String permeabilityComments;
  private DecimalInput kvKhAverage = new DecimalInput();
  private ProbabilityDistributionForm permeability = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm horizontalPermeability = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm verticalPermeability = new ProbabilityDistributionForm();

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

  public DecimalInput getKvKhAverage() {
    return kvKhAverage;
  }

  public void setKvKhAverage(DecimalInput kvKhAverage) {
    this.kvKhAverage = kvKhAverage;
  }

  public String getOtherPermeabilitySource() {
    return otherPermeabilitySource;
  }

  public void setOtherPermeabilitySource(String otherPermeabilitySource) {
    this.otherPermeabilitySource = otherPermeabilitySource;
  }

  public ProbabilityDistributionForm getPermeability() {
    return permeability;
  }

  public void setPermeability(
      ProbabilityDistributionForm permeability) {
    this.permeability = permeability;
  }

  public ProbabilityDistributionForm getHorizontalPermeability() {
    return horizontalPermeability;
  }

  public void setHorizontalPermeability(
      ProbabilityDistributionForm horizontalPermeability) {
    this.horizontalPermeability = horizontalPermeability;
  }

  public ProbabilityDistributionForm getVerticalPermeability() {
    return verticalPermeability;
  }

  public void setVerticalPermeability(
      ProbabilityDistributionForm verticalPermeability) {
    this.verticalPermeability = verticalPermeability;
  }
}
