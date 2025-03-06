package uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.field.FieldApi;
import uk.co.fivium.energyportalapi.generated.client.FieldProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.FieldsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

@Service
public class FieldOperatorAndLicenceQueryService {
  public static final FieldProjectionRoot FIELD_OPERATOR_PROJECTION_ROOT =
      new FieldProjectionRoot()
          .fieldId()
          .fieldOperator()
          .organisationUnitId()
          .name()
          .root();

  public static final FieldsProjectionRoot FIELDS_OPERATOR_PROJECTION_ROOT =
      new FieldsProjectionRoot()
          .fieldId()
          .fieldOperator()
          .organisationUnitId()
          .name()
          .root();

  public static final FieldProjectionRoot FIELD_OPERATOR_AND_LICENCE_PROJECTION_ROOT =
      new FieldProjectionRoot()
          .fieldId()
          .fieldOperator()
          .organisationUnitId()
          .name()
          .root()
          .licences()
          .id()
          .licenceRef()
          .licenceStatus()
          .root();

  public static final FieldsProjectionRoot FIELDS_OPERATOR_AND_LICENCE_PROJECTION_ROOT =
      new FieldsProjectionRoot()
          .fieldId()
          .fieldOperator()
          .organisationUnitId()
          .name()
          .root()
          .licences()
          .id()
          .licenceRef()
          .licenceStatus()
          .root();

  private final FieldApi fieldApi;

  @Autowired
  public FieldOperatorAndLicenceQueryService(FieldApi fieldApi) {
    this.fieldApi = fieldApi;
  }

  public Optional<OrganisationUnit> getFieldOperatorByFieldIdentifier(Integer fieldIdentifier) {
    return fieldApi.findFieldById(
        fieldIdentifier,
            FIELD_OPERATOR_PROJECTION_ROOT,
            new RequestPurpose("Find field by ID"),
            CorrelationIdUtil.getLogCorrelationId()
        ).map(Field::getFieldOperator);
  }

  public Map<Integer, OrganisationUnit> getFieldOperatorIdMap(List<Integer> ids) {
    return fieldApi.getFieldsByIds(
        ids,
            FIELDS_OPERATOR_PROJECTION_ROOT,
            new RequestPurpose("Get fields by IDs"),
            CorrelationIdUtil.getLogCorrelationId()
        )
        .stream()
        .filter(field -> field.getFieldOperator() != null)
        .collect(Collectors.toMap(Field::getFieldId, Field::getFieldOperator, (f1, f2) -> f1));
  }

  public Optional<Field> getFieldById(Integer fieldId) {
    return fieldApi.findFieldById(
        fieldId,
        FIELD_OPERATOR_AND_LICENCE_PROJECTION_ROOT,
        new RequestPurpose("Get field by ID"),
        CorrelationIdUtil.getLogCorrelationId()
    );
  }

  public Field getFieldByIdOrThrow(Integer fieldId) {
    return getFieldById(fieldId).orElseThrow(() -> new RemiEntityNotFoundException(String.format(
        "Could not find field for ID: %s", fieldId
    )));
  }

  public List<Field> getFieldsByIds(List<Integer> ids) {
    return fieldApi.getFieldsByIds(
        ids,
        FIELDS_OPERATOR_AND_LICENCE_PROJECTION_ROOT,
        new RequestPurpose("Get fields by IDs"),
        CorrelationIdUtil.getLogCorrelationId()
    );
  }

}
