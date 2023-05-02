package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum AgeSeries implements Displayable {

  PLEISTOCENE("Pleistocene", 10),
  PLIOCENE("Pliocene", 20),
  UPPER_MIOCENE("Upper Miocene", 30),
  MIDDLE_MIOCENE("Middle Miocene", 40),
  LOWER_MIOCENE("Lower Miocene", 50),
  UPPER_OLIGOCENE("Upper Oligocene", 60),
  LOWER_OLIGOCENE("Lower Oligocene", 70),
  UPPER_EOCENE("Upper Eocene", 80),
  MIDDLE_EOCENE("Middle Eocene", 90),
  LOWER_EOCENE("Lower Eocene", 100),
  UPPER_PALEOCENE("Upper Paleocene", 110),
  LOWER_PALEOCENE("Lower Paleocene", 120),
  UPPER_CRETACEOUS("Upper Cretaceous", 130),
  LOWER_CRETACEOUS("Lower Cretaceous", 140),
  UPPER_JURASSIC("Upper Jurassic", 150),
  MIDDLE_JURASSIC("Middle Jurassic", 160),
  LOWER_JURASSIC("Lower Jurassic", 170),
  UPPER_TRIASSIC("Upper Triassic", 180),
  MIDDLE_TRIASSIC("Middle Triassic", 190),
  LOWER_TRIASSIC("Lower Triassic", 200),
  UPPER_PERMIAN("Upper Permian", 210),
  LR_AND_MIDDLE_PERMIAN("Lr and Middle Permian", 220),
  UPPER_CARBONIFEROUS("Upper Carboniferous", 230),
  LOWER_CARBONIFEROUS("Lower Carboniferous", 240),
  UPPER_DEVONIAN("Upper Devonian", 250),
  MIDDLE_DEVONIAN("Middle Devonian", 260),
  LOWER_DEVONIAN("Lower Devonian", 270);

  private final String displayName;
  private final int displayOrder;

  AgeSeries(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  public static Map<String, String> getAgeSeriesOptions() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(AgeSeries.class);
  }
}
