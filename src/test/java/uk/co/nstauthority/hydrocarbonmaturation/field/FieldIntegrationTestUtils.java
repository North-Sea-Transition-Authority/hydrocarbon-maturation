package uk.co.nstauthority.hydrocarbonmaturation.field;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;

public class FieldIntegrationTestUtils {
  private static final LocalDate FIXED_DATE = LocalDate.of(2002, 10, 19);

  private FieldIntegrationTestUtils() {
    throw new AssertionError();
  }

  private static Field buildField(String fieldName, EntityManager entityManager) {
    var field = new Field();
    field.setCorrectedLegalFieldName(fieldName);

    field.setResource(ResourceIntegrationTestUtils.buildFieldResource(entityManager));

    entityManager.persist(field);
    entityManager.flush();

    return field;
  }

  /**
   * Create and persist a field resource and field with status STATUS_200 set at FIXED_DATE.
   * Note the fieldStatusHistory is also persisted.
   * @param fieldName corrected legal field name of new field. It is reccomended to always set this and assert of the
   *                 field name rather than an id to make debugging more readable.
   * @param entityManager the entityManager to persist the entities
   * @return the created field with an associated field status history with STATUS_200
   */
  public static Field createField(String fieldName, EntityManager entityManager) {
    var field = buildField(fieldName, entityManager);
    addFieldStatusHistory(field, FieldStatus.STATUS_200, entityManager);

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

  /**
   * Create and persist a RegulatorManagedInformation entity and set the geographical area
   * @param field the field with a resource to link to the RegulatorManagedInformation
   * @param geographicalArea the geographical area to be set for the field
   * @param entityManager to persist the entity to
   * @return the created RegulatorManagedInformation
   */
  public static RegulatorManagedInformation setGeographicalAreaForField(Field field, GeographicalArea geographicalArea, EntityManager entityManager) {
    var regulatorManagedInformation = new RegulatorManagedInformation();

    regulatorManagedInformation.setResource(field.getResource());
    regulatorManagedInformation.setGeographicalArea(geographicalArea);

    entityManager.persist(regulatorManagedInformation);
    entityManager.flush();

    return regulatorManagedInformation;
  }

  public static FieldStatusHistory addFieldStatusHistory(Field field, FieldStatus fieldStatus, EntityManager entityManager) {
    return addFieldStatusHistory(field, fieldStatus, FIXED_DATE, entityManager);
  }
}
