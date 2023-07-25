package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum FieldStatus implements Displayable {

  STATUS_200("200: under appraisal – no FDP", 10),
  STATUS_300("300: under-appraisal – future FDP / CSR received", 20),
  STATUS_400("400: appraisal – FDP under discussion) / CSR approved", 30),
  STATUS_500("500 (Appraisal – FDP submitted review) / FDP received", 40),
  STATUS_600("600 (Construction – FDP approved) / consented", 50),
  STATUS_700("700 (Producing)", 60),
  STATUS_799("799 (Production Suspended - possible reserves remain)", 70),
  STATUS_800("800 (Production Suspended)", 80),
  STATUS_899("899 (Production ceased - vent consent needed)", 90),
  STATUS_900("900 (Production ceased)", 100),
  ;
  private final String displayName;
  private final int displayOrder;

  FieldStatus(String displayName, int displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
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
}
