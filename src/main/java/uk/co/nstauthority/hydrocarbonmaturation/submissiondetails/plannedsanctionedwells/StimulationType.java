package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum StimulationType implements Displayable {
  MATRIX_ACIDISING("Matrix acidising", 10),
  HYDRAULIC_FRACTURING("Hydraulic fracturing", 20),
  NO_WELL_STIMULATION("No well stimulation is required", 30),;

  private final String displayName;
  private final int displayOrder;

  StimulationType(String displayName, int displayOrder) {
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
}
