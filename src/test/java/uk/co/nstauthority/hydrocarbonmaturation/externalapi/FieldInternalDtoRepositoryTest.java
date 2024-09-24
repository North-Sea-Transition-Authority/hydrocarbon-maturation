package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;

@IntegrationTest
@Transactional
class FieldInternalDtoRepositoryTest {
  @Autowired
  private EntityManager entityManager;

  @Autowired
  private FieldInternalDtoRepository fieldInternalDtoRepository;
  private Field field1;
  private Field field2;

  @BeforeEach
  void setUp() {
    field1 = createAndSaveField("field name", FieldStatus.STATUS_300);
    field2 = createAndSaveField( "name of field", FieldStatus.STATUS_400);
  }

  private Field createAndSaveField(String fieldName, FieldStatus fieldStatus) {
    var field = FieldIntegrationTestUtils.createField(fieldName, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_200, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, fieldStatus, entityManager);
    FieldIntegrationTestUtils.setGeographicalAreaForField(field, GeographicalArea.CENTRAL_NORTH_SEA, entityManager);

    return field;
  }

  @Test
  void searchFields_searchByFieldIdentifier() {
    var searchedIds = List.of(field1.getFieldIdentifier());
    var resultingFieldInternalDtos = fieldInternalDtoRepository.searchFields(null, null, searchedIds);

    assertThat(resultingFieldInternalDtos)
        .extracting(
            FieldInternalDto::fieldIdentifier,
            FieldInternalDto::correctedLegalFieldName,
            FieldInternalDto::onshoreOffshore,
            FieldInternalDto::determinationStatus,
            FieldInternalDto::organisationUnitId,
            FieldInternalDto::geographicArea,
            FieldInternalDto::fieldStatus
        )
        .containsExactly(
            tuple(
                field1.getFieldIdentifier(),
                field1.getCorrectedLegalFieldName(),
                field1.getOnshoreOffshore(),
                field1.getDeterminationStatus(),
                field1.getResponsibleOrganisationId(),
                GeographicalArea.CENTRAL_NORTH_SEA,
                FieldStatus.STATUS_300
            )
        );
  }

  @Test
  void searchFields_searchByFieldName() {
    var resultingFieldInternalDtos = fieldInternalDtoRepository.searchFields("field", null, null);

    assertThat(resultingFieldInternalDtos)
        .extracting(FieldInternalDto::correctedLegalFieldName)
        .containsExactly(field1.getCorrectedLegalFieldName(), field2.getCorrectedLegalFieldName());
  }

  @Test
  void searchFields_whenAllNull_assertAllFieldsReturned() {
    var resultingFieldInternalDtos = fieldInternalDtoRepository.searchFields(null, null, null);

    assertThat(resultingFieldInternalDtos)
        .extracting(FieldInternalDto::fieldIdentifier)
        .containsExactly(field1.getFieldIdentifier(), field2.getFieldIdentifier());
  }

  @Test
  void searchFields_searchByFieldStatus() {
    var resultingFieldInternalDtos = fieldInternalDtoRepository.searchFields(null, List.of(FieldStatus.STATUS_400), null);

    assertThat(resultingFieldInternalDtos)
        .extracting(FieldInternalDto::fieldIdentifier)
        .containsExactly(field2.getFieldIdentifier());
  }
}