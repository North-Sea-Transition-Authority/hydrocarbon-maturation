package uk.co.nstauthority.hydrocarbonmaturation.fds;

import java.util.Objects;

public class ErrorItem {

  private final int displayOrder;
  private final String fieldName;
  private final String errorMessage;

  public ErrorItem(int displayOrder, String fieldName, String errorMessage) {
    this.displayOrder = displayOrder;
    this.fieldName = fieldName;
    this.errorMessage = errorMessage;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ErrorItem errorItem)) {
      return false;
    }
    return displayOrder == errorItem.displayOrder
        && Objects.equals(fieldName, errorItem.fieldName)
        && Objects.equals(errorMessage, errorItem.errorMessage);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(displayOrder, fieldName, errorMessage);
  }
}
