package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record FormationDto(String formationValue, String formationDisplay) implements SearchSelectable {

  @Override
  public String getSelectionId() {
    return formationValue;
  }

  @Override
  public String getSelectionText() {
    return formationDisplay;
  }
}
