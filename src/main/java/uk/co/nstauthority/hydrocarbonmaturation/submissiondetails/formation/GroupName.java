package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum GroupName implements Displayable {
  BACTON("Bacton", 5),
  BRENT("Brent", 10),
  BRUCE("Bruce", 20),
  CHALK("Chalk", 30),
  CLAIR("Clair", 40),
  COAL_MEASURES("Coal Measures", 50),
  CONYBEARE("Conybeare", 55),
  CROMER_KNOLL("Cromer Knoll", 60),
  DUNLIN("Dunlin", 70),
  DUNROBIN_BAY("Dunrobin Bay", 80),
  FARNE("Farne", 90),
  FLADEN("Fladen", 100),
  HAISBOROUGH("Haisborough", 105),
  HERON("Heron", 110),
  HORDALAND("Hordaland", 120),
  HUMBER("Humber", 130),
  LIAS("Lias", 140),
  LOWER_OLD_RED("Lower Old Red", 150),
  MERCIA_MUDSTONE("Mercia Mudstone", 160),
  MIDDLE_OLD_RED("Middle Old Red", 170),
  MILLSTONE_GRIT("Millstone Grit", 180),
  MONTROSE("Montrose", 190),
  MORAY("Moray", 200),
  NORDALAND("Nordaland", 210),
  PAPA("Papa", 220),
  PENARTH("Penarth", 225),
  ROGALAND("Rogaland", 230),
  ROTLIEGEND("Rotliegend", 240),
  SHERWOOD_SANDSTONE("Sherwood Sandstone", 250),
  SHETLAND("Shetland", 260),
  SKERRY("Skerry", 270),
  STRONSAY("Stronsay", 280),
  UPPER_OLD_RED("Upper Old Red", 290),
  WESTRAY("Westray", 300),
  ZECHSTEIN("Zechstein", 310);

  private final String displayName;
  private final int displayOrder;

  GroupName(String displayName, int displayOrder) {
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

  public static Map<String, String> getGroupNames() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(GroupName.class);
  }
}
