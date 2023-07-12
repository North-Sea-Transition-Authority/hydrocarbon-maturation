package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class FieldSelectForm implements SubmissionForm {

  private String fieldSelectedOption;

  public String getFieldSelectedOption() {
    return fieldSelectedOption;
  }

  public void setFieldSelectedOption(String fieldSelectedOption) {
    this.fieldSelectedOption = fieldSelectedOption;
  }
}