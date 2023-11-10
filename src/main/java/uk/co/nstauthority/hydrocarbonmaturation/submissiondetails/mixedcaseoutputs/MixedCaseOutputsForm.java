package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class MixedCaseOutputsForm implements SubmissionForm {

  private ProbabilityDistributionForm gasInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm gasRecoverableReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedCondensateRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedCondensateRecoverableReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentRecoverableReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm naturalGasLiquidsResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm naturalGasLiquidsReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm salesGasResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm salesGasReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilInPlace = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilRecoverableReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasRecoverableResources = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedGasRecoverableReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm riskedOilEquivalentRecoverableMean = new ProbabilityDistributionForm();
  private Boolean hasAssociatedGasRecoverableResources;
  private Boolean hasAssociatedCondensateRecoverableResources;

  public ProbabilityDistributionForm getGasInPlace() {
    return gasInPlace;
  }

  public void setGasInPlace(ProbabilityDistributionForm gasInPlace) {
    this.gasInPlace = gasInPlace;
  }

  public ProbabilityDistributionForm getGasRecoverableResources() {
    return gasRecoverableResources;
  }

  public void setGasRecoverableResources(ProbabilityDistributionForm gasRecoverableResources) {
    this.gasRecoverableResources = gasRecoverableResources;
  }

  public ProbabilityDistributionForm getGasRecoverableReserves() {
    return gasRecoverableReserves;
  }

  public void setGasRecoverableReserves(ProbabilityDistributionForm gasRecoverableReserves) {
    this.gasRecoverableReserves = gasRecoverableReserves;
  }

  public ProbabilityDistributionForm getAssociatedCondensateRecoverableResources() {
    return associatedCondensateRecoverableResources;
  }

  public void setAssociatedCondensateRecoverableResources(ProbabilityDistributionForm associatedCondensateRecoverableResources) {
    this.associatedCondensateRecoverableResources = associatedCondensateRecoverableResources;
  }

  public ProbabilityDistributionForm getAssociatedCondensateRecoverableReserves() {
    return associatedCondensateRecoverableReserves;
  }

  public void setAssociatedCondensateRecoverableReserves(ProbabilityDistributionForm associatedCondensateRecoverableReserves) {
    this.associatedCondensateRecoverableReserves = associatedCondensateRecoverableReserves;
  }

  public ProbabilityDistributionForm getOilEquivalentRecoverableResources() {
    return oilEquivalentRecoverableResources;
  }

  public void setOilEquivalentRecoverableResources(ProbabilityDistributionForm oilEquivalentRecoverableResources) {
    this.oilEquivalentRecoverableResources = oilEquivalentRecoverableResources;
  }

  public ProbabilityDistributionForm getOilEquivalentRecoverableReserves() {
    return oilEquivalentRecoverableReserves;
  }

  public void setOilEquivalentRecoverableReserves(ProbabilityDistributionForm oilEquivalentRecoverableReserves) {
    this.oilEquivalentRecoverableReserves = oilEquivalentRecoverableReserves;
  }

  public ProbabilityDistributionForm getNaturalGasLiquidsResources() {
    return naturalGasLiquidsResources;
  }

  public void setNaturalGasLiquidsResources(ProbabilityDistributionForm naturalGasLiquidsResources) {
    this.naturalGasLiquidsResources = naturalGasLiquidsResources;
  }

  public ProbabilityDistributionForm getNaturalGasLiquidsReserves() {
    return naturalGasLiquidsReserves;
  }

  public void setNaturalGasLiquidsReserves(ProbabilityDistributionForm naturalGasLiquidsReserves) {
    this.naturalGasLiquidsReserves = naturalGasLiquidsReserves;
  }

  public ProbabilityDistributionForm getSalesGasResources() {
    return salesGasResources;
  }

  public void setSalesGasResources(ProbabilityDistributionForm salesGasResources) {
    this.salesGasResources = salesGasResources;
  }

  public ProbabilityDistributionForm getSalesGasReserves() {
    return salesGasReserves;
  }

  public void setSalesGasReserves(ProbabilityDistributionForm salesGasReserves) {
    this.salesGasReserves = salesGasReserves;
  }

  public ProbabilityDistributionForm getOilInPlace() {
    return oilInPlace;
  }

  public void setOilInPlace(ProbabilityDistributionForm oilInPlace) {
    this.oilInPlace = oilInPlace;
  }

  public ProbabilityDistributionForm getOilRecoverableResources() {
    return oilRecoverableResources;
  }

  public void setOilRecoverableResources(ProbabilityDistributionForm oilRecoverableResources) {
    this.oilRecoverableResources = oilRecoverableResources;
  }

  public ProbabilityDistributionForm getOilRecoverableReserves() {
    return oilRecoverableReserves;
  }

  public void setOilRecoverableReserves(ProbabilityDistributionForm oilRecoverableReserves) {
    this.oilRecoverableReserves = oilRecoverableReserves;
  }

  public ProbabilityDistributionForm getAssociatedGasRecoverableResources() {
    return associatedGasRecoverableResources;
  }

  public void setAssociatedGasRecoverableResources(ProbabilityDistributionForm associatedGasRecoverableResources) {
    this.associatedGasRecoverableResources = associatedGasRecoverableResources;
  }

  public ProbabilityDistributionForm getAssociatedGasRecoverableReserves() {
    return associatedGasRecoverableReserves;
  }

  public void setAssociatedGasRecoverableReserves(ProbabilityDistributionForm associatedGasRecoverableReserves) {
    this.associatedGasRecoverableReserves = associatedGasRecoverableReserves;
  }

  public ProbabilityDistributionForm getRiskedOilEquivalentRecoverableMean() {
    return riskedOilEquivalentRecoverableMean;
  }

  public void setRiskedOilEquivalentRecoverableMean(ProbabilityDistributionForm riskedOilEquivalentRecoverableMean) {
    this.riskedOilEquivalentRecoverableMean = riskedOilEquivalentRecoverableMean;
  }

  public Boolean getHasAssociatedGasRecoverableResources() {
    return hasAssociatedGasRecoverableResources;
  }

  public void setHasAssociatedGasRecoverableResources(Boolean hasAssociatedGasRecoverableResources) {
    this.hasAssociatedGasRecoverableResources = hasAssociatedGasRecoverableResources;
  }

  public Boolean getHasAssociatedCondensateRecoverableResources() {
    return hasAssociatedCondensateRecoverableResources;
  }

  public void setHasAssociatedCondensateRecoverableResources(Boolean hasAssociatedCondensateRecoverableResources) {
    this.hasAssociatedCondensateRecoverableResources = hasAssociatedCondensateRecoverableResources;
  }
}
