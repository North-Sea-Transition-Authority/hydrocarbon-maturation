package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum FieldStatus implements Displayable {
  STATUS_000("000: Shelved", 0, 0),
  STATUS_200("200: no CSR or FDP", 10, 200),
  STATUS_300("300: CSR submitted", 20, 300),
  STATUS_400("400: CSR approved, FDP under discussion", 30, 400),
  STATUS_500("500: FDP submitted", 50, 500),
  STATUS_600("600: FDP approved", 60, 600),
  STATUS_700("700: Producing", 60, 700),
  STATUS_799("799: Production suspended (possible reserves remain)", 80, 799),
  STATUS_800("800: Production suspended", 90, 800),
  STATUS_899("899: Production ceased (vent consent needed)", 100, 899),
  STATUS_900("900: Production ceased", 110, 900),
  ;
  private final String displayName;
  private final int displayOrder;
  private final int statusValue;

  FieldStatus(String displayName, int displayOrder, int statusValue) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.statusValue = statusValue;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }

  public int getStatusValue() {
    return statusValue;
  }
}
