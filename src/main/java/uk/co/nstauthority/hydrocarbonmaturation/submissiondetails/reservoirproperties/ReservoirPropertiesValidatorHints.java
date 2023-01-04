package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public class ReservoirPropertiesValidatorHints extends SubmissionFormValidatorHints {

  private HydrocarbonsDiscovered hydrocarbonsDiscovered;

  public ReservoirPropertiesValidatorHints(ValidationType validationType,
                                           SubmissionType submissionType,
                                           HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    super(validationType, submissionType);
    this.hydrocarbonsDiscovered = hydrocarbonsDiscovered;
  }

  public HydrocarbonsDiscovered getHydrocarbonsDiscovered() {
    return hydrocarbonsDiscovered;
  }

  public void setHydrocarbonsDiscovered(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    this.hydrocarbonsDiscovered = hydrocarbonsDiscovered;
  }
}
