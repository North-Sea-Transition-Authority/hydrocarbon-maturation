package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

public record GasFluidPropertySummaryView(String gasGravity,
                                          String gasH2s,
                                          String gasCo2,
                                          String gasN2,
                                          String condensatePresent,
                                          Boolean showCondensate,
                                          String condensateGravity,
                                          String condensateViscosity,
                                          String condensateSulphur,
                                          String condensateWax,
                                          String condensateOtherInclWax,
                                          String adviseOtherGasesConcentration) {

  public static GasFluidPropertySummaryView empty() {
    return new GasFluidPropertySummaryView("", "", "", "", "",
        true, "", "", "", "",
        "", "");
  }
}
