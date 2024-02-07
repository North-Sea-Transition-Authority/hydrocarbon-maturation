package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

public class FieldStatusForm {
  private FieldStatus fieldStatus;

  private String fieldStatusDateDay;
  private String fieldStatusDateMonth;
  private String fieldStatusDateYear;

  public FieldStatus getFieldStatus() {
    return fieldStatus;
  }

  public void setFieldStatus(FieldStatus fieldStatus) {
    this.fieldStatus = fieldStatus;
  }

  public String getFieldStatusDateDay() {
    return fieldStatusDateDay;
  }

  public void setFieldStatusDateDay(String fieldStatusDateDay) {
    this.fieldStatusDateDay = fieldStatusDateDay;
  }

  public String getFieldStatusDateMonth() {
    return fieldStatusDateMonth;
  }

  public void setFieldStatusDateMonth(String fieldStatusDateMonth) {
    this.fieldStatusDateMonth = fieldStatusDateMonth;
  }

  public String getFieldStatusDateYear() {
    return fieldStatusDateYear;
  }

  public void setFieldStatusDateYear(String fieldStatusDateYear) {
    this.fieldStatusDateYear = fieldStatusDateYear;
  }
}
