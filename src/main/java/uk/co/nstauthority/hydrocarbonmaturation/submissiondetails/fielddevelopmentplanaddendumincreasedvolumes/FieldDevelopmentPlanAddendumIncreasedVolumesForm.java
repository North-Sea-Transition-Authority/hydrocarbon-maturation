package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumincreasedvolumes;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class FieldDevelopmentPlanAddendumIncreasedVolumesForm implements SubmissionForm {

  private Boolean changeInVolumetrics;
  private ProbabilityDistributionForm oilInitiallyInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasInitiallyInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedCondensateRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedCondensateReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm naturalGasLiquidsRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm naturalGasLiquidsReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm salesGasRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm salesGasReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentReserves = new ProbabilityDistributionForm();
  private String comment;

  public Boolean getChangeInVolumetrics() {
    return changeInVolumetrics;
  }

  public void setChangeInVolumetrics(Boolean changeInVolumetrics) {
    this.changeInVolumetrics = changeInVolumetrics;
  }

  public ProbabilityDistributionForm getOilInitiallyInPlace() {
    return oilInitiallyInPlace;
  }

  public void setOilInitiallyInPlace(
      ProbabilityDistributionForm oilInitiallyInPlace) {
    this.oilInitiallyInPlace = oilInitiallyInPlace;
  }

  public ProbabilityDistributionForm getGasInitiallyInPlace() {
    return gasInitiallyInPlace;
  }

  public void setGasInitiallyInPlace(
      ProbabilityDistributionForm gasInitiallyInPlace) {
    this.gasInitiallyInPlace = gasInitiallyInPlace;
  }

  public ProbabilityDistributionForm getOilRecoverableResources() {
    return oilRecoverableResources;
  }

  public void setOilRecoverableResources(
      ProbabilityDistributionForm oilRecoverableResources) {
    this.oilRecoverableResources = oilRecoverableResources;
  }

  public ProbabilityDistributionForm getOilReserves() {
    return oilReserves;
  }

  public void setOilReserves(
      ProbabilityDistributionForm oilReserves) {
    this.oilReserves = oilReserves;
  }

  public ProbabilityDistributionForm getAssociatedCondensateRecoverableResources() {
    return associatedCondensateRecoverableResources;
  }

  public void setAssociatedCondensateRecoverableResources(
      ProbabilityDistributionForm associatedCondensateRecoverableResources) {
    this.associatedCondensateRecoverableResources = associatedCondensateRecoverableResources;
  }

  public ProbabilityDistributionForm getAssociatedCondensateReserves() {
    return associatedCondensateReserves;
  }

  public void setAssociatedCondensateReserves(
      ProbabilityDistributionForm associatedCondensateReserves) {
    this.associatedCondensateReserves = associatedCondensateReserves;
  }

  public ProbabilityDistributionForm getNaturalGasLiquidsRecoverableResources() {
    return naturalGasLiquidsRecoverableResources;
  }

  public void setNaturalGasLiquidsRecoverableResources(
      ProbabilityDistributionForm naturalGasLiquidsRecoverableResources) {
    this.naturalGasLiquidsRecoverableResources = naturalGasLiquidsRecoverableResources;
  }

  public ProbabilityDistributionForm getNaturalGasLiquidsReserves() {
    return naturalGasLiquidsReserves;
  }

  public void setNaturalGasLiquidsReserves(
      ProbabilityDistributionForm naturalGasLiquidsReserves) {
    this.naturalGasLiquidsReserves = naturalGasLiquidsReserves;
  }

  public ProbabilityDistributionForm getSalesGasRecoverableResources() {
    return salesGasRecoverableResources;
  }

  public void setSalesGasRecoverableResources(
      ProbabilityDistributionForm salesGasRecoverableResources) {
    this.salesGasRecoverableResources = salesGasRecoverableResources;
  }

  public ProbabilityDistributionForm getSalesGasReserves() {
    return salesGasReserves;
  }

  public void setSalesGasReserves(
      ProbabilityDistributionForm salesGasReserves) {
    this.salesGasReserves = salesGasReserves;
  }

  public ProbabilityDistributionForm getOilEquivalentRecoverableResources() {
    return oilEquivalentRecoverableResources;
  }

  public void setOilEquivalentRecoverableResources(
      ProbabilityDistributionForm oilEquivalentRecoverableResources) {
    this.oilEquivalentRecoverableResources = oilEquivalentRecoverableResources;
  }

  public ProbabilityDistributionForm getOilEquivalentReserves() {
    return oilEquivalentReserves;
  }

  public void setOilEquivalentReserves(
      ProbabilityDistributionForm oilEquivalentReserves) {
    this.oilEquivalentReserves = oilEquivalentReserves;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
