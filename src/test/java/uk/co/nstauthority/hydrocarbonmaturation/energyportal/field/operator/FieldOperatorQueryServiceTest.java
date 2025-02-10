package uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.field.FieldApi;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;

@ExtendWith(MockitoExtension.class)
class FieldOperatorQueryServiceTest {

  @Mock
  private FieldApi fieldApi;

  private FieldOperatorQueryService fieldOperatorQueryService;
  private static final int FIELD_IDENTIFIER = 100;

  @BeforeEach
  void setUp() {
    fieldOperatorQueryService = new FieldOperatorQueryService(
        fieldApi
    );
  }

  @Test
  void getFieldOperatorByFieldIdentifier_noOperator_shouldReturnEmpty() {
    when(fieldApi.findFieldById(
        eq(FIELD_IDENTIFIER),
        eq(FieldOperatorQueryService.FIELD_OPERATOR_PROJECTION_ROOT),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(Optional.empty());

    var result = fieldOperatorQueryService.getFieldOperatorByFieldIdentifier(FIELD_IDENTIFIER);

    assertThat(result).isEmpty();
  }

  @Test
  void getFieldOperatorByFieldIdentifier_shouldReturnOrgUnit() {
    var orgUnit = new OrganisationUnit();

    var field = new Field();
    field.setFieldOperator(orgUnit);

    when(fieldApi.findFieldById(
        eq(FIELD_IDENTIFIER),
        eq(FieldOperatorQueryService.FIELD_OPERATOR_PROJECTION_ROOT),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(Optional.of(field));

    var result = fieldOperatorQueryService.getFieldOperatorByFieldIdentifier(FIELD_IDENTIFIER);

    assertThat(result).isEqualTo(Optional.of(orgUnit));
  }

  @Test
  void getFieldOperatorIdMap_noFieldsFound_shouldReturnEmpty() {
    when(fieldApi.getFieldsByIds(
        eq(List.of(1,2)),
        eq(FieldOperatorQueryService.FIELDS_OPERATOR_PROJECTION_ROOT),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(List.of());

    var result = fieldOperatorQueryService.getFieldOperatorIdMap(List.of(1,2));

    assertThat(result).isEmpty();
  }

  @Test
  void getFieldOperatorIdMap_noOperator_shouldReturnPopulatedMap() {
    var orgUnit1 = new OrganisationUnit();
    var orgUnit2 = new OrganisationUnit();

    var field1 = new Field();
    field1.setFieldId(1);
    field1.setFieldOperator(orgUnit1);

    var field2 = new Field();
    field2.setFieldId(2);
    field2.setFieldOperator(orgUnit2);

    when(fieldApi.getFieldsByIds(
        eq(List.of(1,2)),
        eq(FieldOperatorQueryService.FIELDS_OPERATOR_PROJECTION_ROOT),
        any(RequestPurpose.class),
        any(LogCorrelationId.class)
    )).thenReturn(List.of(field1, field2));

    var result = fieldOperatorQueryService.getFieldOperatorIdMap(List.of(1,2));

    assertThat(result).isEqualTo(
        Map.ofEntries(
            entry(1, orgUnit1),
            entry(2, orgUnit2)
        )
    );
  }
}
