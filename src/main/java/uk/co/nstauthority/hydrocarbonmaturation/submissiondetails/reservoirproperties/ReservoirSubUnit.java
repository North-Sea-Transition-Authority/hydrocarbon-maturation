package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum ReservoirSubUnit implements Displayable {

  ALBA("Alba", 10),
  ANDREW("Andrew", 20),
  BALDER("Balder", 30),
  BALDER_SANDSTONE("Balder Sandstone", 40),
  BALDER_TUFF("Balder Tuff", 50),
  BALMORAL("Balmoral", 60),
  BEAULY("Beauly", 70),
  BLACK_SAIL("Black Sail", 80),
  BRUCE_A("Bruce A", 90),
  BRUCE_B("Bruce B", 100),
  BRUCE_C("Bruce C", 110),
  BUNTER("Bunter", 120),
  CALVERT("Calvert", 130),
  CARAN("Caran", 140),
  COLSAY("Colsay", 150),
  CROMARTY("Cromarty", 160),
  DANIAN("Danian", 170),
  FERGIE("Fergie", 180),
  FLUGGA("Flugga", 190),
  FRIGG("Frigg", 200),
  GENNEKER("Genneker", 210),
  GLAMIS("Glamis", 220),
  GRID("Grid", 230),
  HEIMDAL("Heimdal", 240),
  HERMOD("Hermod", 250),
  HILDASAY("Hildasay", 260),
  JOANNE("Joanne", 270),
  JONATHAN("Jonathan", 280),
  JOSEPHINE("Josephine", 290),
  JOSHUA("Joshua", 300),
  JUDY("Judy", 310),
  JULIUS("Julius", 320),
  LAMBA_SANDSTONE("Lamba Sandstone", 330),
  LEWIS("Lewis", 340),
  LEWIS_I("Lewis I", 350),
  LEWIS_II("Lewis II", 360),
  LEWIS_III("Lewis III", 370),
  LEWIS_IV("Lewis IV", 380),
  LOWER_DORNOCH("Lower Dornoch", 390),
  LOWER_FORTIES("Lower Forties", 400),
  LOWER_MAIN("Lower Main", 410),
  LOWER_NESS("Lower Ness", 420),
  LOWER_TAY("Lower Tay", 430),
  M1("M1", 440),
  M2("M2", 450),
  M3("M3", 460),
  MAGNUS_SANDSTONE("Magnus Sandstone", 470),
  MAUREEN("Maureen", 480),
  ODIN("Odin", 490),
  RATTRAY("Rattray", 500),
  RAUDE("Raude", 510),
  SKADAN("Skadan", 520),
  SKROO("Skroo", 530),
  SKY_SAIL("Sky Sail", 540),
  SOLAN_SANDSTONE("Solan Sandstone", 550),
  SPINNAKER("Spinnaker", 560),
  T10("T10", 570),
  T22("T22", 580),
  T25("T25", 590),
  T28("T28", 600),
  T31("T31", 610),
  T32("T32", 620),
  T34("T34", 630),
  T35("T35", 640),
  T36("T36", 650),
  T38("T38", 660),
  TAYLOR("Taylor", 670),
  TEAL("Teal", 680),
  UNIT_I("Unit I", 690),
  UNIT_II("Unit II", 700),
  UNIT_III("Unit III", 710),
  UNIT_IV("Unit IV", 720),
  UNIT_IX("Unit IX", 730),
  UNIT_V("Unit V", 740),
  UNIT_VI("Unit VI", 750),
  UNIT_VII("Unit VII", 760),
  UNIT_VIII("Unit VIII", 770),
  UNIT_X("Unit X", 780),
  UPPER_BALDER("Upper Balder", 790),
  UPPER_DORNOCH("Upper Dornoch", 800),
  UPPER_FORTIES("Upper Forties", 810),
  UPPER_MAIN("Upper Main", 820),
  UPPER_NESS("Upper Ness", 830),
  UPPER_TAY("Upper Tay", 840),
  VALE("Vale", 850);

  private final String displayName;
  private final int displayOrder;


  ReservoirSubUnit(String displayName, int displayOrder) {
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

  public static Map<String, String> getSubUnits() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirSubUnit.class);
  }
}
