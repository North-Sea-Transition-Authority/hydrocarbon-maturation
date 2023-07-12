package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

record FieldView(Integer fieldId, String legalFieldName) implements SearchSelectable {
  @Override
  public String getSelectionId() {
    return fieldId.toString();
  }

  @Override
  public String getSelectionText() {
    return legalFieldName;
  }
}
