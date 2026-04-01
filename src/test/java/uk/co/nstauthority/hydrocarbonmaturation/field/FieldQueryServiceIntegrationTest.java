package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorAndLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@Transactional
@IntegrationTest
class FieldQueryServiceIntegrationTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private EntityManager entityManager;

  @MockitoBean
  private InternalTeamService internalTeamService;

  @MockitoBean
  private ResponsibleTeamService responsibleTeamService;

  @MockitoBean
  private IndustryTeamService industryTeamService;

  @MockitoBean
  private FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService;

  @Autowired
  private FieldQueryService fieldQueryService;

  @Test
  void getFieldByNameForSubmissionType_areaTeamUser_assertResponsibleTeamFieldsReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );

    var fieldResource2 = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field2 = FieldIntegrationTestUtils.createField("Field 2", fieldResource2, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field2, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource2,
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
        GeographicalArea.CENTRAL_NORTH_SEA,
        entityManager
    );

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getFieldByNameForSubmissionType("field", SubmissionType.CREATE_FDP, USER))
        .isEqualTo(List.of(new FieldView(field.getFieldIdentifier(), field.getCorrectedLegalFieldName())));
  }

  @Test
  void getFieldByNameForSubmissionType_operatorUser_assertOrgUnitFieldsReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_300, entityManager);
    FieldIntegrationTestUtils.setGeographicalAreaForField(field, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);

    var orgUnit = new OrganisationUnit();
    orgUnit.setOrganisationUnitId(1);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(1));
    when(fieldOperatorAndLicenceQueryService.getFieldOperatorIdMap(List.of(field.getFieldIdentifier())))
        .thenReturn(Map.of(field.getFieldIdentifier(), orgUnit));

    assertThat(fieldQueryService.getFieldByNameForSubmissionType("field", SubmissionType.CREATE_FDP, USER))
        .isEqualTo(List.of(new FieldView(field.getFieldIdentifier(), field.getCorrectedLegalFieldName())));
  }

  @Test
  void getFieldByNameForSubmissionType_fieldStatusMismatch_assertNotReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_900, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getFieldByNameForSubmissionType("field", SubmissionType.CREATE_FDP, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getFieldByNameForSubmissionType_fieldIsArchived_assertNotReturned() {
    var fieldResource = SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.FIELD);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getFieldByNameForSubmissionType("field", SubmissionType.CREATE_FDP, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getFieldByNameForSubmissionType_fieldHasDraftSubmission_assertNotReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );
    SubmissionDetailIntegrationTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP, fieldResource, SubmissionDetailStatus.DRAFT, entityManager);

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getFieldByNameForSubmissionType("field", SubmissionType.CREATE_FDP, USER))
        .isEqualTo(List.of());
  }

  @Test
  void getFieldByNameForSubmissionType_fieldHasWithdrawnSubmission_assertReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getFieldByNameForSubmissionType("field", SubmissionType.CREATE_FDP, USER))
        .isEqualTo(List.of(new FieldView(field.getFieldIdentifier(), field.getCorrectedLegalFieldName())));
  }

  @Test
  void getShelvedOrCopFieldByName_shelvedField_AssertReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.buildFieldWithoutStatusHistory("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_000, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getShelvedOrCopFieldByName("field", USER))
        .isEqualTo(List.of(new FieldView(field.getFieldIdentifier(), field.getCorrectedLegalFieldName())));
  }

  @Test
  void getShelvedOrCopFieldByName_copField_AssertReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_900, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getShelvedOrCopFieldByName("field", USER))
        .isEqualTo(List.of(new FieldView(field.getFieldIdentifier(), field.getCorrectedLegalFieldName())));
  }

  @Test
  void getShelvedOrCopFieldByName_fieldStatusMismatch_assertNotReturned() {
    var fieldResource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);
    var field = FieldIntegrationTestUtils.createField("Field 1", fieldResource, entityManager);
    FieldIntegrationTestUtils.addFieldStatusHistory(field, FieldStatus.STATUS_300, entityManager);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        fieldResource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager
    );

    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(responsibleTeamService.getResponsibleTeamTypesForUser(USER))
        .thenReturn(List.of(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND));

    assertThat(fieldQueryService.getShelvedOrCopFieldByName("field", USER))
        .isEqualTo(List.of());
  }

}