package uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.field.FieldApi;
import uk.co.fivium.energyportalapi.generated.client.FieldProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.FieldsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@Service
public class FieldOperatorQueryService {
  public static final FieldProjectionRoot FIELD_OPERATOR_PROJECTION_ROOT =
      new FieldProjectionRoot()
          .fieldOperator()
          .organisationUnitId()
          .name()
          .root();

  public static final FieldsProjectionRoot FIELDS_OPERATOR_PROJECTION_ROOT =
      new FieldsProjectionRoot()
          .fieldOperator()
          .organisationUnitId()
          .name()
          .root();

  private final FieldApi fieldApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public FieldOperatorQueryService(FieldApi fieldApi, EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.fieldApi = fieldApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  public Optional<OrganisationUnit> getFieldOperatorByFieldIdentifier(Integer fieldIdentifier) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        fieldApi.findFieldById(
                fieldIdentifier,
                FIELD_OPERATOR_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId
            )
            .map(Field::getFieldOperator)
    );
  }

  public Map<Integer, OrganisationUnit> getFieldOperatorIdMap(List<Integer> ids) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        fieldApi.getFieldsByIds(
                ids,
                FIELDS_OPERATOR_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId
            )
            .stream()
            .filter(field -> field.getFieldOperator() != null)
            .collect(Collectors.toMap(Field::getFieldId, Field::getFieldOperator, (f1, f2) -> f1))
    );
  }
}
