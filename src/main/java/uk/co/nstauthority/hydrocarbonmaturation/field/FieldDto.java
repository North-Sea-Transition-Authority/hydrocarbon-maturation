package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

record FieldDto(Integer fieldIdentifier, String correctedLegalFieldName) implements SearchSelectable {

  static FieldDto from(Field field) {
    return new FieldDto(field.getFieldIdentifier(), field.getCorrectedLegalFieldName());
  }

  @Override
  public String getSelectionId() {
    return fieldIdentifier.toString();
  }

  @Override
  public String getSelectionText() {
    return correctedLegalFieldName;
  }
}