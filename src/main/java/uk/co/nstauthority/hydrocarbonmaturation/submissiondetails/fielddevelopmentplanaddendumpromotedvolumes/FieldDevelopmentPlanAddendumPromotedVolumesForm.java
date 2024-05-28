package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumpromotedvolumes;

import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class FieldDevelopmentPlanAddendumPromotedVolumesForm implements SubmissionForm {

  private Boolean hasPromotedVolumes;
  private ProbabilityDistributionForm oilReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm associatedCondensateReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm naturalGasLiquidsReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm salesGasReserves = new ProbabilityDistributionForm();
  private ProbabilityDistributionForm oilEquivalentReserves = new ProbabilityDistributionForm();
  private String comment;

  public Boolean getHasPromotedVolumes() {
    return hasPromotedVolumes;
  }

  public void setHasPromotedVolumes(Boolean hasPromotedVolumes) {
    this.hasPromotedVolumes = hasPromotedVolumes;
  }

  public ProbabilityDistributionForm getOilReserves() {
    return oilReserves;
  }

  public void setOilReserves(
      ProbabilityDistributionForm oilReserves) {
    this.oilReserves = oilReserves;
  }

  public ProbabilityDistributionForm getAssociatedCondensateReserves() {
    return associatedCondensateReserves;
  }

  public void setAssociatedCondensateReserves(
      ProbabilityDistributionForm associatedCondensateReserves) {
    this.associatedCondensateReserves = associatedCondensateReserves;
  }

  public ProbabilityDistributionForm getNaturalGasLiquidsReserves() {
    return naturalGasLiquidsReserves;
  }

  public void setNaturalGasLiquidsReserves(
      ProbabilityDistributionForm naturalGasLiquidsReserves) {
    this.naturalGasLiquidsReserves = naturalGasLiquidsReserves;
  }

  public ProbabilityDistributionForm getSalesGasReserves() {
    return salesGasReserves;
  }

  public void setSalesGasReserves(
      ProbabilityDistributionForm salesGasReserves) {
    this.salesGasReserves = salesGasReserves;
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
