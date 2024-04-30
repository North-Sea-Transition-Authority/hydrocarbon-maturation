package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

public class UndevelopedDiscoveryVolumetricsForm {

  private DecimalInput oilInPlace = new DecimalInput();
  private UndevelopedDiscoveryRecoverableForm oilRecoverable = new UndevelopedDiscoveryRecoverableForm();
  private DecimalInput gasInPlace = new DecimalInput();
  private UndevelopedDiscoveryRecoverableForm gasRecoverable = new UndevelopedDiscoveryRecoverableForm();
  private String volumetricDifferenceReason;

  public DecimalInput getOilInPlace() {
    return oilInPlace;
  }

  public void setOilInPlace(DecimalInput oilInPlace) {
    this.oilInPlace = oilInPlace;
  }

  public DecimalInput getGasInPlace() {
    return gasInPlace;
  }

  public void setGasInPlace(DecimalInput gasInPlace) {
    this.gasInPlace = gasInPlace;
  }

  public String getVolumetricDifferenceReason() {
    return volumetricDifferenceReason;
  }

  public void setVolumetricDifferenceReason(String volumetricDifferenceReason) {
    this.volumetricDifferenceReason = volumetricDifferenceReason;
  }

  public UndevelopedDiscoveryRecoverableForm getOilRecoverable() {
    return oilRecoverable;
  }

  public void setOilRecoverable(
      UndevelopedDiscoveryRecoverableForm oilRecoverable) {
    this.oilRecoverable = oilRecoverable;
  }

  public UndevelopedDiscoveryRecoverableForm getGasRecoverable() {
    return gasRecoverable;
  }

  public void setGasRecoverable(
      UndevelopedDiscoveryRecoverableForm gasRecoverable) {
    this.gasRecoverable = gasRecoverable;
  }
}