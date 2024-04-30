package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

public class UndevelopedDiscoveryRecoverableForm {
  private DecimalInput oneC = new DecimalInput();
  private DecimalInput twoC = new DecimalInput();
  private DecimalInput threeC = new DecimalInput();

  public DecimalInput getOneC() {
    return oneC;
  }

  public void setOneC(DecimalInput oneC) {
    this.oneC = oneC;
  }

  public DecimalInput getTwoC() {
    return twoC;
  }

  public void setTwoC(DecimalInput twoC) {
    this.twoC = twoC;
  }

  public DecimalInput getThreeC() {
    return threeC;
  }

  public void setThreeC(DecimalInput threeC) {
    this.threeC = threeC;
  }
}
