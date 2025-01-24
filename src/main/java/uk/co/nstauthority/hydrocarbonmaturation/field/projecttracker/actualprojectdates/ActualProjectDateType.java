package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

enum ActualProjectDateType {
  CSR_FIRST_DRAFT("Actual first draft CSR received date", "actual first draft CSR received date"),
  FDP_FIRST_DRAFT("Actual FDP first draft received date", "actual FDP first draft received date"),
  COP_FIRST_SUBMISSION("Actual first CoP submission received date", "actual first CoP submission received date"),
  COP_NO_OBJECTION_LETTER_SENT("CoP no objection letter sent date", "CoP no objection letter sent date"),
  COP_CONFIRMATION_RECEIVED("Operator confirmation of CoP received date", "operator confirmation of CoP received date")
  ;

  private final String displayName;

  private final String lowercaseFormatDisplayName;

  ActualProjectDateType(String displayName,
                        String lowercaseFormatDisplayName
  ) {
    this.displayName = displayName;
    this.lowercaseFormatDisplayName = lowercaseFormatDisplayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getLowercaseFormatDisplayName() {
    return lowercaseFormatDisplayName;
  }
}
