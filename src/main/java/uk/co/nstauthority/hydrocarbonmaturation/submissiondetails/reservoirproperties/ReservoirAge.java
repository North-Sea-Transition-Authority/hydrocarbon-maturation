package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum ReservoirAge implements Displayable {

  AALENIAN("Aalenian", 10),
  ALBIAN("Albian", 20),
  APTIAN("Aptian", 30),
  BAJOCIAN("Bajocian", 40),
  BARREMIAN("Barremian", 50),
  BATHONIAN("Bathonian", 60),
  BERRIASIAN("Berriasian", 70),
  CALLOVIAN("Callovian", 80),
  CAMBRIAN("Cambrian", 90),
  CARBONIFEROUS("Carboniferous", 100),
  CHALK_GROUP_UPPER_CRETACEOUS_DANIAN("Chalk Group (Upper Cretaceous-Danian)", 110),
  CORALLIAN("Corallian", 120),
  CRETACEOUS("Cretaceous", 130),
  LOWER_CRETACEOUS("Lower Cretaceous", 140),
  LOWER_CRETACEOUS_DEVONIAN("Lower Cretaceous-Devonian", 150),
  UPPER_CRETACEOUS("Upper Cretaceous", 160),
  DANIAN("Danian", 170),
  DEVONIAN("Devonian", 180),
  LOWER_DEVONIAN("Lower Devonian", 190),
  UPPER_DEVONIAN("Upper Devonian", 200),
  DINANTIAN_LOWER_CARB("Dinantian (Lower Carb.)", 210),
  EOCENE("Eocene", 220),
  LOWER_EOCENE("Lower Eocene", 230),
  MIDDLE_EOCENE("Middle Eocene", 240),
  UPPER_EOCENE("Upper Eocene", 250),
  HAUTERIVIAN("Hauterivian", 260),
  HETTANGIAN("Hettangian", 270),
  JURASSIC("Jurassic", 280),
  JURASSIC_TRIASSIC("Jurassic-Triassic", 290),
  LOWER_JURASSIC("Lower Jurassic", 300),
  MIDDLE_JURASSIC("Middle Jurassic", 310),
  UPPER_JURASSIC("Upper Jurassic", 320),
  KIMMERIDGIAN_OFFSHORE("Kimmeridgian (offshore)", 330),
  KIMMERIDGIAN_ONSHORE("Kimmeridgian (onshore)", 340),
  LLANDOVERY("Llandovery", 350),
  MESOZOIC("Mesozoic", 360),
  MIOCENE("Miocene", 370),
  LOWER_MIOCENE("Lower Miocene", 380),
  MIDDLE_MIOCENE("Middle Miocene", 390),
  UPPER_MIOCENE("Upper Miocene", 400),
  NAMURIAN("Namurian", 410),
  NAMURIAN_A("Namurian A", 420),
  NAMURIAN_B("Namurian B", 430),
  NAMURIAN_C("Namurian C", 440),
  OLIGOCENE("Oligocene", 450),
  LOWER_OLIGOCENE("Lower Oligocene", 460),
  MIDDLE_OLIGOCENE("Middle Oligocene", 470),
  UPPER_OLIGOCENE("Upper Oligocene", 480),
  ORDOVICIAN("Ordovician", 490),
  OXFORDIAN("Oxfordian", 500),
  OXFORDIAN_ALNESS_SPICULITE("Oxfordian (Alness Spiculite)", 510),
  PALEOCENE("Paleocene", 520),
  PALEOCENE_AND_EOCENE("Paleocene and Eocene", 530),
  LOWER_PALEOCENE("Lower Paleocene", 540),
  MIDDLE_PALEOCENE("Middle Paleocene", 550),
  UPPER_PALEOCENE("Upper Paleocene", 560),
  PERMIAN("Permian", 570),
  LOWER_PERMIAN("Lower Permian", 580),
  LOWER_PERMIAN_ROTLIEGEND_OBSOLETE("Lower Permian (Rotliegend) Obsolete", 590),
  UPPER_PERMIAN("Upper Permian", 600),
  PERMO_TRIASSIC("Permo-Triassic", 610),
  PLIENSBACHIAN("Pliensbachian", 620),
  PLIOCENE("Pliocene", 630),
  LOWER_PLIOCENE("Lower Pliocene", 640),
  UPPER_PLIOCENE("Upper Pliocene", 650),
  PORTLANDIAN("Portlandian", 660),
  PRECAMBRIAN("Precambrian", 670),
  PURBECKIAN("Purbeckian", 680),
  QUATERNARY_PLIOCENE("Quaternary-Pliocene", 690),
  RYAZANIAN("Ryazanian", 700),
  SENONIAN("Senonian", 710),
  SILESIAN_UPPER_CARB("Silesian (Upper Carb.)", 720),
  SILURIAN("Silurian", 730),
  SINEMURIAN("Sinemurian", 740),
  STEPHANIAN("Stephanian", 750),
  TERTIARY("Tertiary", 760),
  TOARCIAN("Toarcian", 770),
  TOURNASIAN("Tournasian", 780),
  TRIASSIC("Triassic", 790),
  TRIASSIC_DEVONO_CARBONIFEROUS("Triassic, Devono-Carboniferous", 800),
  LOWER_TRIASSIC("Lower Triassic", 810),
  MIDDLE_TRIASSIC("Middle Triassic", 820),
  UPPER_TRIASSIC("Upper Triassic", 830),
  UNDIFFERENTIATED("Undifferentiated", 840),
  UNKNOWN("Unknown", 850),
  VALANGINIAN("Valanginian", 860),
  VOLGIAN("Volgian", 870),
  WENLOCK("Wenlock", 880),
  WESTPHALIAN("Westphalian", 890),
  WESTPHALIAN_A("Westphalian A", 900),
  WESTPHALIAN_B("Westphalian B", 910),
  WESTPHALIAN_C("Westphalian C", 920),
  WESTPHALIAN_D("Westphalian D", 930);

  private final String displayName;
  private final int displayOrder;

  ReservoirAge(String displayName, int displayOrder) {
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

  public static Map<String, String> getOptions() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirAge.class);
  }
}