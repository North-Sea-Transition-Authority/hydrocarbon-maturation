package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

public record OilFluidPropertySummaryView(String oilGravity,
                                          String oilViscosity,
                                          String oilSulphur,
                                          String oilWax,
                                          String oilOtherWaxAppearanceTemperature) {
  public static OilFluidPropertySummaryView empty() {
    return new OilFluidPropertySummaryView("", "", "", "", "");
  }
}
