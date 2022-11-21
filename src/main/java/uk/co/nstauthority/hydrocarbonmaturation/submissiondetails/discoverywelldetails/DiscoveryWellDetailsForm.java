package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsForm;

class DiscoveryWellDetailsForm implements SubmissionForm {

  private WellDetailsForm wellDetailsForm = new WellDetailsForm();
  private HydrocarbonsDiscovered hydrocarbonsDiscovered;
  private DiscoveryStatus discoveryStatus;
  private String chanceOfCommercialityComments;
  private DecimalInput dryHoleCost = new DecimalInput();
  private DecimalInput successRelatedCosts = new DecimalInput();

  public HydrocarbonsDiscovered getHydrocarbonsDiscovered() {
    return hydrocarbonsDiscovered;
  }

  public void setHydrocarbonsDiscovered(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    this.hydrocarbonsDiscovered = hydrocarbonsDiscovered;
  }

  public DiscoveryStatus getDiscoveryStatus() {
    return discoveryStatus;
  }

  public void setDiscoveryStatus(
      DiscoveryStatus discoveryStatus) {
    this.discoveryStatus = discoveryStatus;
  }

  public String getChanceOfCommercialityComments() {
    return chanceOfCommercialityComments;
  }

  public void setChanceOfCommercialityComments(String chanceOfCommercialityComments) {
    this.chanceOfCommercialityComments = chanceOfCommercialityComments;
  }

  public DecimalInput getDryHoleCost() {
    return dryHoleCost;
  }

  public void setDryHoleCost(DecimalInput dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }

  public DecimalInput getSuccessRelatedCosts() {
    return successRelatedCosts;
  }

  public void setSuccessRelatedCosts(DecimalInput successRelatedCosts) {
    this.successRelatedCosts = successRelatedCosts;
  }

  public WellDetailsForm getWellDetailsForm() {
    return wellDetailsForm;
  }

  public void setWellDetailsForm(
      WellDetailsForm wellDetailsForm) {
    this.wellDetailsForm = wellDetailsForm;
  }
}
