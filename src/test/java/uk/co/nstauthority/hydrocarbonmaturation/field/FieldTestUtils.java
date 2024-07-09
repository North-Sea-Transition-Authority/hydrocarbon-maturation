package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

public class FieldTestUtils {
  private FieldTestUtils() {
    throw new AssertionError();
  }

  public static Field createField(Integer fieldIdentifier) {
    var field = new Field();
    field.setFieldIdentifier(fieldIdentifier);
    field.setResource(ResourceTestUtils.buildResource(ResourceType.FIELD));
    field.setCorrectedLegalFieldName("%d Field".formatted(fieldIdentifier));
    return field;
  }

  public static Field createField(UUID resourceId, Integer fieldIdentifier) {
    var field = createField(fieldIdentifier);
    field.getResource().setId(resourceId);
    return field;
  }

  public static Field createField(String fieldName, OnshoreOffshore onshoreOffshore) {
    var field = new Field();
    field.setCommonNameForField(fieldName);
    field.setOnshoreOffshore(onshoreOffshore);
    return field;
  }
}
