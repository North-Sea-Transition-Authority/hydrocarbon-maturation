package uk.co.nstauthority.hydrocarbonmaturation.field;

import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

public class FieldTestUtils {
  private FieldTestUtils() {
    throw new AssertionError();
  }

  public static Field createField(Integer fieldIdentifier) {
    var field = new Field();
    field.setFieldIdentifier(fieldIdentifier);
    field.setResource(ResourceTestUtils.buildResource(ResourceType.FIELD));
    return field;
  }
}
