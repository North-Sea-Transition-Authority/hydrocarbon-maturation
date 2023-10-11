package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum BarrierBaffle implements Displayable {
  FAULTS_FRACTURES("Faults/Fractures",
      "Sealing or partially sealing",
      10),
  TIGHT_INTERBEDS("Tight interbeds",
      "Laterally continuous tight lithologies, for example, mudstone, tight sandstone/carbonate, coal, evaporites",
      20),
  VOLCANICS("Volcanics",
      "Lavas, sills, dykes, tight volcaniclastics",
      30),
  CEMENTATION("Cementation",
      "Patchy or laterally continuous",
      40),
  TAR_MAT("Tar mat",
      "Laterally continuous",
      50),
  LATERAL_FACIES_CHANGE("Lateral facies change",
      "For example, channel/lobe margin, sand pinchout, reef/shoal edge, injectite margin",
      60);

  private final String displayName;
  private final String description;
  private final int displayOrder;

  BarrierBaffle(String displayName, String info, int displayOrder) {
    this.displayName = displayName;
    this.description = info;
    this.displayOrder = displayOrder;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }
}
