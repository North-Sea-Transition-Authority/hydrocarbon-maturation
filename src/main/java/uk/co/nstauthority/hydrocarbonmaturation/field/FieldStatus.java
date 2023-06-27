package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

enum FieldStatus implements Displayable {

  UNDER_APPRAISAL_NO_FDP_200("200: under appraisal – no FDP", 10),
  UNDER_APPRAISAL_FUTURE_FDP_CSR_RECEIVED_300("300: under-appraisal – future FDP / CSR received", 20),
  APPRAISAL_FDP_UNDER_DISCUSSION_CSR_APPROVED_400("400: appraisal – FDP under discussion) / CSR approved", 30),
  APPRAISAL_FDP_SUBMITTED_REVIEW_FDP_RECEIVED_500("500 (Appraisal – FDP submitted review) / FDP received", 40),
  CONSTRUCTION_FDP_APPROVED_CONSENTED_600("600 (Construction – FDP approved) / consented", 50),
  PRODUCING_700("700 (Producing)", 60),
  PRODUCTION_SUSPENDED_POSSIBLE_RESERVES_REMAIN_799("799 (Production Suspended - possible reserves remain)", 70),
  PRODUCTION_SUSPENDED_800("800 (Production Suspended)", 80),
  PRODUCTION_CEASED_VENT_CONSENT_NEEDED_899("899 (Production ceased - vent consent needed)", 90),
  PRODUCTION_CEASED_900("900 (Production ceased)", 100),
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
