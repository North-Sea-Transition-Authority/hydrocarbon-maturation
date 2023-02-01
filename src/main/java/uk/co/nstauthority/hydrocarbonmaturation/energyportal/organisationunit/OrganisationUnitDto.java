package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

class OrganisationUnitDto implements SearchSelectable {
  private Integer organisationUnitId;

  private String organisationUnitName;
  private String companyHouseNumber;

  public Integer getOrganisationUnitId() {
    return organisationUnitId;
  }

  public void setOrganisationUnitId(Integer organisationUnitId) {
    this.organisationUnitId = organisationUnitId;
  }

  public String getOrganisationUnitName() {
    return organisationUnitName;
  }

  public void setOrganisationUnitName(String organisationUnitName) {
    this.organisationUnitName = organisationUnitName;
  }

  public String getCompanyHouseNumber() {
    return companyHouseNumber;
  }

  public void setCompanyHouseNumber(String companyHouseNumber) {
    this.companyHouseNumber = companyHouseNumber;
  }

  @Override
  public String getSelectionId() {
    return organisationUnitId.toString();
  }

  @Override
  public String getSelectionText() {
    if (companyHouseNumber != null) {
      return "%s (%s)".formatted(organisationUnitName, companyHouseNumber);
    }
    return organisationUnitName;
  }
}
