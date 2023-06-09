package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record AgeSeriesDto(String ageSeriesValue, String ageSeriesDisplay) implements SearchSelectable {

  @Override
  public String getSelectionId() {
    return ageSeriesValue;
  }

  @Override
  public String getSelectionText() {
    return ageSeriesDisplay;
  }
}
