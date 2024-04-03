package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

class ProjectCapexForm {
  private String dateProvidedDay;
  private String dateProvidedMonth;
  private String dateProvidedYear;

  private DecimalInput totalCapex;

  private ProjectCapexSource sourceOfInformation;

  public String getDateProvidedDay() {
    return dateProvidedDay;
  }

  public void setDateProvidedDay(String dateProvidedDay) {
    this.dateProvidedDay = dateProvidedDay;
  }

  public String getDateProvidedMonth() {
    return dateProvidedMonth;
  }

  public void setDateProvidedMonth(String dateProvidedMonth) {
    this.dateProvidedMonth = dateProvidedMonth;
  }

  public String getDateProvidedYear() {
    return dateProvidedYear;
  }

  public void setDateProvidedYear(String dateProvidedYear) {
    this.dateProvidedYear = dateProvidedYear;
  }

  public DecimalInput getTotalCapex() {
    return totalCapex;
  }

  public void setTotalCapex(DecimalInput totalCapex) {
    this.totalCapex = totalCapex;
  }

  public ProjectCapexSource getSourceOfInformation() {
    return sourceOfInformation;
  }

  public void setSourceOfInformation(ProjectCapexSource sourceOfInformation) {
    this.sourceOfInformation = sourceOfInformation;
  }
}
