package uk.co.nstauthority.hydrocarbonmaturation.field;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;

public class FieldIntegrationTestUtils {
  private static final LocalDate FIXED_DATE = LocalDate.of(2002, 10, 19);

  private FieldIntegrationTestUtils() {
    throw new AssertionError();
  }

  public static Field createField(String fieldName, EntityManager entityManager) {
    var field = new Field();
    field.setCorrectedLegalFieldName(fieldName);

    field.setResource(ResourceIntegrationTestUtils.buildFieldResource(entityManager));

    entityManager.persist(field);
    entityManager.flush();

    return field;
  }

  public static FieldStatusHistory addFieldStatusHistory(Field field, FieldStatus fieldStatus, LocalDate localDate, EntityManager entityManager) {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setField(field);
    fieldStatusHistory.setFieldStatus(fieldStatus);
    fieldStatusHistory.setStatusDate(localDate);

    entityManager.persist(fieldStatusHistory);
    entityManager.flush();

    return fieldStatusHistory;
  }

  public static FieldStatusHistory addFieldStatusHistory(Field field, FieldStatus fieldStatus, EntityManager entityManager) {
    return addFieldStatusHistory(field, fieldStatus, FIXED_DATE, entityManager);
  }
}
