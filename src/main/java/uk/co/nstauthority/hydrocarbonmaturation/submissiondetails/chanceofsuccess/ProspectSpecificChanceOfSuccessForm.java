package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;

public class ProspectSpecificChanceOfSuccessForm {

  private DecimalInput trapPresence = new DecimalInput();
  private String trapPresenceComments;
  private DecimalInput trapEffectiveness = new DecimalInput();
  private String trapEffectivenessComments;
  private DecimalInput sourcePresence = new DecimalInput();
  private String sourcePresenceComments;
  private DecimalInput sourceEffectiveness = new DecimalInput();
  private String sourceEffectivenessComments;
  private DecimalInput reservoirPresence = new DecimalInput();
  private String reservoirPresenceComments;
  private DecimalInput reservoirEffectiveness = new DecimalInput();
  private String reservoirEffectivenessComments;

  public DecimalInput getTrapPresence() {
    return trapPresence;
  }

  public void setTrapPresence(DecimalInput trapPresence) {
    this.trapPresence = trapPresence;
  }

  public String getTrapPresenceComments() {
    return trapPresenceComments;
  }

  public void setTrapPresenceComments(String trapPresenceComments) {
    this.trapPresenceComments = trapPresenceComments;
  }

  public DecimalInput getTrapEffectiveness() {
    return trapEffectiveness;
  }

  public void setTrapEffectiveness(DecimalInput trapEffectiveness) {
    this.trapEffectiveness = trapEffectiveness;
  }

  public String getTrapEffectivenessComments() {
    return trapEffectivenessComments;
  }

  public void setTrapEffectivenessComments(String trapEffectivenessComments) {
    this.trapEffectivenessComments = trapEffectivenessComments;
  }

  public DecimalInput getSourcePresence() {
    return sourcePresence;
  }

  public void setSourcePresence(DecimalInput sourcePresence) {
    this.sourcePresence = sourcePresence;
  }

  public String getSourcePresenceComments() {
    return sourcePresenceComments;
  }

  public void setSourcePresenceComments(String sourcePresenceComments) {
    this.sourcePresenceComments = sourcePresenceComments;
  }

  public DecimalInput getSourceEffectiveness() {
    return sourceEffectiveness;
  }

  public void setSourceEffectiveness(DecimalInput sourceEffectiveness) {
    this.sourceEffectiveness = sourceEffectiveness;
  }

  public String getSourceEffectivenessComments() {
    return sourceEffectivenessComments;
  }

  public void setSourceEffectivenessComments(String sourceEffectivenessComments) {
    this.sourceEffectivenessComments = sourceEffectivenessComments;
  }

  public DecimalInput getReservoirPresence() {
    return reservoirPresence;
  }

  public void setReservoirPresence(DecimalInput reservoirPresence) {
    this.reservoirPresence = reservoirPresence;
  }

  public String getReservoirPresenceComments() {
    return reservoirPresenceComments;
  }

  public void setReservoirPresenceComments(String reservoirPresenceComments) {
    this.reservoirPresenceComments = reservoirPresenceComments;
  }

  public DecimalInput getReservoirEffectiveness() {
    return reservoirEffectiveness;
  }

  public void setReservoirEffectiveness(
      DecimalInput reservoirEffectiveness) {
    this.reservoirEffectiveness = reservoirEffectiveness;
  }

  public String getReservoirEffectivenessComments() {
    return reservoirEffectivenessComments;
  }

  public void setReservoirEffectivenessComments(String reservoirEffectivenessComments) {
    this.reservoirEffectivenessComments = reservoirEffectivenessComments;
  }
}
