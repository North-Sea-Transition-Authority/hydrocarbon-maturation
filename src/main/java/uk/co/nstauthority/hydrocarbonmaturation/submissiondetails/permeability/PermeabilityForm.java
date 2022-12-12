package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class PermeabilityForm implements SubmissionForm {

  private Boolean hasPermeability;
  private PermeabilityValuesForm permeabilityValuesForm = new PermeabilityValuesForm();

  public Boolean getHasPermeability() {
    return hasPermeability;
  }

  public void setHasPermeability(Boolean hasPermeability) {
    this.hasPermeability = hasPermeability;
  }

  public PermeabilityValuesForm getPermeabilityValuesForm() {
    return permeabilityValuesForm;
  }

  public void setPermeabilityValuesForm(
      PermeabilityValuesForm permeabilityValuesForm) {
    this.permeabilityValuesForm = permeabilityValuesForm;
  }
}
