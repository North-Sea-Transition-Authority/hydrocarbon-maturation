package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum Formation implements Displayable {

  ASGARD("Asgard", 10),
  AUK("Auk", 20),
  BALDER("Balder", 30),
  BEATRICE("Beatrice", 40),
  BEAULY("Beauly", 50),
  BERYL("Beryl", 60),
  BOSIES_BANK("Bosie's Bank", 70),
  BRAEMORE_MUDSTONE("Braemore Mudstone", 80),
  BROOM("Broom", 90),
  BRORA_COAL("Brora Coal", 100),
  BRYNE("Bryne", 110),
  BUCHAN("Buchan", 120),
  BUNTER_SHALE("Bunter Shale", 130),
  CAISTER("Caister", 140),
  CAITHNESS_FLAGSTONE("Caithness Flagstone", 150),
  CEMENTSTONE("Cementstone", 160),
  CLEAVER("Cleaver", 170),
  COMMODORE("Commodore", 180),
  CORMORANT("Cormorant", 190),
  CRUISER("Cruiser", 200),
  DORNOCH("Dornoch", 210),
  DUNNET_HEAD_SANDSTONE("Dunnet Head Sandstone", 220),
  EKOFISK("Ekofisk", 230),
  ERIKSSON("Eriksson", 240),
  ETIVE("Etive", 250),
  FARSUND("Farsund", 260),
  FELL("Fell", 270),
  FINDHORN("Findhorn", 280),
  FLETT("Flett", 290),
  FOULA("Foula", 300),
  FRASERBURGH("Fraserburgh", 310),
  FULMAR("Fulmar", 320),
  GOLSPIE("Golspie", 330),
  HALIBUT_CARBONATE("Halibut Carbonate", 340),
  HEATHER("Heather", 350),
  HERRING("Herring", 360),
  HIDRA("Hidra", 370),
  HOD("Hod", 380),
  HORDA("Horda", 390),
  HOY_SANDSTONE("Hoy Sandstone", 400),
  HUTTON("Hutton", 410),
  JORSALFARE("Jorsalfare", 420),
  KIMMERIDGE_CLAY("Kimmeridge Clay", 430),
  KINNESSWOOD("Kinnesswood", 440),
  KYRRE("Kyrre", 450),
  LADYS_WALK("Lady's Walk", 460),
  LAMBA("Lamba", 470),
  LARK("Lark", 480),
  LEMAN("Leman", 490),
  LINNHE("Linnhe", 500),
  LISTA("Lista", 510),
  LOSSIEHEAD("Lossiehead", 520),
  LOWER_CLAIR("Lower Clair", 530),
  MACBETH("Macbeth", 540),
  MACKEREL("Mackerel", 550),
  MAINS("Mains", 560),
  MANDAL("Mandal", 570),
  MAUREEN("Maureen", 580),
  MIDDLE_CLAIR("Middle Clair", 590),
  MILLSTONE_GRIT("Millstone Grit", 600),
  MORRISON("Morrison", 610),
  MOUSA("Mousa", 620),
  NANSEN("Nansen", 630),
  NAUST("Naust", 640),
  NESS("Ness", 650),
  ORCADIA("Orcadia", 660),
  ORRIN("Orrin", 670),
  OTTER_BANK("Otter Bank", 680),
  OUSDALE_ARKOSE("Ousdale Arkose", 690),
  PENTLAND("Pentland", 700),
  PIPER("Piper", 710),
  RANNOCH("Rannoch", 720),
  RODBY("Rodby", 730),
  RONA("Rona", 740),
  ROYAL_SOVEREIGN("Royal Sovereign", 750),
  SAUDA("Sauda", 760),
  SCREMERSTON("Scremerston", 770),
  SELE("Sele", 780),
  SHEARWATER_SALT("Shearwater Salt", 790),
  SILVERPIT("Silverpit", 800),
  SKADE("Skade", 810),
  SKAGERRAK("Skagerrak", 820),
  SMITH_BANK("Smith Bank", 830),
  SOLA("Sola", 840),
  SOLAN_BANK("Solan Bank", 850),
  STATFJORD("Statfjord", 860),
  STOTFIELD_CALCRETE("Stotfield Calcrete", 870),
  STRATH_RORY("Strath Rory", 880),
  STRUIE("Struie", 890),
  SULLOM("Sullom", 900),
  SVARTE("Svarte", 910),
  TARBERT("Tarbert", 920),
  TAU("Tau", 930),
  TAYPORT("Tayport", 940),
  TEITH("Teith", 950),
  TOR("Tor", 960),
  TURBOT_ANDYDRITE("Turbot Andydrite", 970),
  ULA("Ula", 980),
  UPPER_CLAIR("Upper Clair", 990),
  UPPER_GREENSAND("Upper Greensand", 1000),
  UTSIRA("Utsira", 1010),
  VAILA("Vaila", 1020),
  VALHALL("Valhall", 1030),
  VICTORY("Victory", 1040),
  WESTOE("Westoe", 1050),
  WICK("Wick", 1060),
  YESNABY_SANDSTONE("Yesnaby Sandstone", 1070),
  YOREDALE("Yoredale", 1080);

  private final String displayName;
  private final int displayOrder;


  Formation(String displayName, int displayOrder) {
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

  public static Map<String, String> getFormationNames() {
    return DisplayableEnumOptionUtil.getDisplayableOptions(Formation.class);
  }
}
