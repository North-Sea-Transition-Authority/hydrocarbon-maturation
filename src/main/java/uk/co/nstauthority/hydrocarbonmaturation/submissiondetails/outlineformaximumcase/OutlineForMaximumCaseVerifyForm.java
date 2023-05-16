package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class OutlineForMaximumCaseVerifyForm implements SubmissionForm {
  private Boolean shapeIsCorrect;

  public Boolean getShapeIsCorrect() {
    return shapeIsCorrect;
  }

  public void setShapeIsCorrect(Boolean shapeIsCorrect) {
    this.shapeIsCorrect = shapeIsCorrect;
  }
}
