package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

public record GeneralFluidPropertySummaryView(String datumDepth,
                                              String temperatureAtDatumDepth,
                                              String pressureAtDatumDepth,
                                              String saturationPressureAtDatumDepth,
                                              String waterSalinity) {
  public static GeneralFluidPropertySummaryView empty() {
    return new GeneralFluidPropertySummaryView("", "", "", "", "");
  }
}
