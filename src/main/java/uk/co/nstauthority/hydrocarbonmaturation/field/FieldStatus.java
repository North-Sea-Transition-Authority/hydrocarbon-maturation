package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum FieldStatus implements Displayable {

  STATUS_200("200: under appraisal – no FDP", 10, 200),
  STATUS_300("300: under-appraisal – future FDP / CSR received", 20, 300),
  STATUS_400("400: appraisal – FDP under discussion) / CSR accepted", 30, 400),
  STATUS_500("500 (Appraisal – FDP submitted review) / FDP received", 40, 500),
  STATUS_600("600 (Construction – FDP accepted) / consented", 50, 600),
  STATUS_700("700 (Producing)", 60, 700),
  STATUS_799("799 (Production Suspended - possible reserves remain)", 70, 799),
  STATUS_800("800 (Production Suspended)", 80, 800),
  STATUS_899("899 (Production ceased - vent consent needed)", 90, 899),
  STATUS_900("900 (Production ceased)", 100, 900),
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
