package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

class LicenceApplicationDto implements SearchSelectable {

  private Integer licenceApplicationId;
  private String licenceApplicationRef;

  public Integer getLicenceApplicationId() {
    return licenceApplicationId;
  }

  public void setLicenceApplicationId(Integer licenceApplicationId) {
    this.licenceApplicationId = licenceApplicationId;
  }

  public String getLicenceApplicationRef() {
    return licenceApplicationRef;
  }

  public void setLicenceApplicationRef(String licenceApplicationRef) {
    this.licenceApplicationRef = licenceApplicationRef;
  }

  @Override
  public String getSelectionId() {
    return licenceApplicationId.toString();
  }

  @Override
  public String getSelectionText() {
    return getLicenceApplicationRef();
  }
}
