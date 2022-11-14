package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class PermeabilityDryWellForm implements SubmissionForm {

  private Boolean hasPermeability;
  private PermeabilityForm permeabilityForm = new PermeabilityForm();

  public Boolean getHasPermeability() {
    return hasPermeability;
  }

  public void setHasPermeability(Boolean hasPermeability) {
    this.hasPermeability = hasPermeability;
  }

  public PermeabilityForm getPermeabilityForm() {
    return permeabilityForm;
  }

  public void setPermeabilityForm(
      PermeabilityForm permeabilityForm) {
    this.permeabilityForm = permeabilityForm;
  }
}
