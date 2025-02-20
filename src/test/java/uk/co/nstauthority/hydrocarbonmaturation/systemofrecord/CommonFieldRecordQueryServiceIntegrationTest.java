package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementService;

@Transactional
@IntegrationTest
public class CommonFieldRecordQueryServiceIntegrationTest {
  private static final User USER = UserTestUtil.Builder().build();
  @Autowired
  private DSLContext dslContext;

  @Autowired
  private RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TeamManagementService teamManagementService;

  private SearchResourceRecordsForm form;

  @BeforeEach
  void setUp() {
    setUpRegulatorTest();

    form = SearchResourceRecordsForm.from(SearchFieldRecordsFilter.createInstance());
  }

  @Nested
  class GivenNoFilters {
    private final LocalDate FIXED_DATE = LocalDate.of(2002, 11, 3);

    @Test
    void whenFieldStatusDatesAreSequential_assertOrderedByFieldStatusDate() {
      var statusUpdatedLast = FieldIntegrationTestUtils.createField("Field status 500", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(statusUpdatedLast, FieldStatus.STATUS_500, FIXED_DATE, entityManager);

      var statusUpdateEarlier = FieldIntegrationTestUtils.createField("Field status 300 1", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(statusUpdateEarlier, FieldStatus.STATUS_300, FIXED_DATE.minusDays(2), entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(statusUpdateEarlier, FieldStatus.STATUS_600, FIXED_DATE.minusDays(1), entityManager);

      var statusUpdatedFirst = FieldIntegrationTestUtils.createField("Field status 300 2", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(statusUpdatedFirst, FieldStatus.STATUS_500, FIXED_DATE.minusMonths(1), entityManager);

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceId)
          .containsExactly(
              statusUpdatedLast.getResource().getId(),
              statusUpdateEarlier.getResource().getId(),
              statusUpdatedFirst.getResource().getId()
          );
    }

    @Test
    void whenFieldStatusDatesAreEqual_assertOrderedByFieldName() {
      var fieldAlpha = FieldIntegrationTestUtils.createField("Field alpha", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(fieldAlpha, FieldStatus.STATUS_500, FIXED_DATE, entityManager);

      var fieldBeta = FieldIntegrationTestUtils.createField("Field beta", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(fieldBeta, FieldStatus.STATUS_300, FIXED_DATE.minusDays(2), entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(fieldBeta, FieldStatus.STATUS_600, FIXED_DATE, entityManager);

      var fieldAlpha1 = FieldIntegrationTestUtils.createField("Field alpha 1", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(fieldAlpha1, FieldStatus.STATUS_500, FIXED_DATE, entityManager);

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceId)
          .containsExactly(
              fieldAlpha.getResource().getId(),
              fieldAlpha1.getResource().getId(),
              fieldBeta.getResource().getId()
          );
    }
  }

  @Nested
  class GivenFilteringByFieldStatus {
    private Field status200Field1;
    private Field status200Field2;
    private Field status300Field;
    private Field status700Field1;
    private Field status700Field2;

    @BeforeEach
    void setUp() {
      status200Field1 = FieldIntegrationTestUtils.createField("Field status 200 1", entityManager);

      status200Field2 = FieldIntegrationTestUtils.createField("Field status 200 2", entityManager);

      status300Field = FieldIntegrationTestUtils.createField("Field status 300", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status300Field, FieldStatus.STATUS_200, entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status300Field, FieldStatus.STATUS_300, entityManager);

      status700Field1 = FieldIntegrationTestUtils.createField("Field status 700 1", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status700Field1, FieldStatus.STATUS_200, entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status700Field1, FieldStatus.STATUS_400, entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status700Field1, FieldStatus.STATUS_600, entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status700Field1, FieldStatus.STATUS_700, entityManager);

      status700Field2 = FieldIntegrationTestUtils.createField("Field status 700 2", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status700Field2, FieldStatus.STATUS_200, entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status700Field2, FieldStatus.STATUS_700, entityManager);
    }

    @Test
    void whenNoFieldStatusSelected_thenAllReturned() {
      form.setFieldStatuses(List.of());
      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceId)
          .containsExactlyInAnyOrder(
              status200Field1.getResource().getId(),
              status200Field2.getResource().getId(),
              status300Field.getResource().getId(),
              status700Field1.getResource().getId(),
              status700Field2.getResource().getId()
          );
    }

    @Test
    void whenOneFieldStatusSelected_thenAllWithFieldStatusReturned() {
      form.setFieldStatuses(List.of(FieldStatus.STATUS_300.getEnumName()));

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactly(
              status300Field.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenMultipleFieldStatusesSelected_thenAllWithFieldStatusesReturned() {
      form.setFieldStatuses(List.of(FieldStatus.STATUS_200.getEnumName(), FieldStatus.STATUS_700.getEnumName()));

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              status200Field1.getCorrectedLegalFieldName(),
              status200Field2.getCorrectedLegalFieldName(),
              status700Field1.getCorrectedLegalFieldName(),
              status700Field2.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenAlsoFilteringByName_thenOnlyFieldWithStatusAndNameIsReturned() {
      status200Field1.setCorrectedLegalFieldName("Duplicated name");
      status700Field1.setCorrectedLegalFieldName("Duplicated name");
      entityManager.persist(status200Field1);
      entityManager.persist(status700Field1);
      entityManager.flush();

      form.setFieldStatuses(List.of(FieldStatus.STATUS_200.getEnumName()));
      form.setFieldNameForResourceSubmission("Duplicated name");

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              status200Field1.getCorrectedLegalFieldName()
          );
    }
  }

  @Nested
  class GivenFilteringByGeographicalArea {

    private Field nnsField1;
    private Field nnsField2;
    private Field wosField;
    private Field onshoreField;

    @BeforeEach
    void setUp() {
      nnsField1 = FieldIntegrationTestUtils.createField("Northern North Sea Field 1", entityManager);
      FieldIntegrationTestUtils.setGeographicalAreaForField(nnsField1, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);

      nnsField2 = FieldIntegrationTestUtils.createField("Northern North Sea Field 2", entityManager);
      FieldIntegrationTestUtils.setGeographicalAreaForField(nnsField2, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);

      wosField = FieldIntegrationTestUtils.createField("West of Shetland Field", entityManager);
      FieldIntegrationTestUtils.setGeographicalAreaForField(wosField, GeographicalArea.WEST_OF_SHETLAND, entityManager);

      onshoreField = FieldIntegrationTestUtils.createField("Onshore Field", entityManager);
      FieldIntegrationTestUtils.setGeographicalAreaForField(onshoreField, GeographicalArea.ONSHORE, entityManager);
    }

    @Test
    void whenNoGeographicalAreaSelected_thenAllReturned() {
      form.setGeographicalArea(new ArrayList<>());

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              nnsField1.getCorrectedLegalFieldName(),
              nnsField2.getCorrectedLegalFieldName(),
              wosField.getCorrectedLegalFieldName(),
              onshoreField.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenOneGeographicalAreaSelected_thenAllWithAreaTypeReturned() {
      form.setGeographicalArea(List.of(GeographicalArea.NORTHERN_NORTH_SEA.getEnumName()));

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              nnsField1.getCorrectedLegalFieldName(),
              nnsField2.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenMultipleGeographicalAreaSelected_thenAllWithEitherAreaTypesReturned() {
      form.setGeographicalArea(List.of(
          GeographicalArea.NORTHERN_NORTH_SEA.getEnumName(),
          GeographicalArea.WEST_OF_SHETLAND.getEnumName())
      );

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              nnsField1.getCorrectedLegalFieldName(),
              nnsField2.getCorrectedLegalFieldName(),
              wosField.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenAlsoFilteringByFieldName_thenReturnsFieldThatMeetsAllConditions() {
      form.setFieldNameForResourceSubmission("Field 1");
      form.setGeographicalArea(List.of(GeographicalArea.NORTHERN_NORTH_SEA.getEnumName()));

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactly(
              nnsField1.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenAlsoFilteringByFieldName_andByFieldStatus_thenReturnsFieldThatMeetsAllConditions() {
      form.setFieldNameForResourceSubmission("Field");
      form.setGeographicalArea(List.of(GeographicalArea.NORTHERN_NORTH_SEA.getEnumName()));

      FieldIntegrationTestUtils.addFieldStatusHistory(nnsField1, FieldStatus.STATUS_300, entityManager);
      form.setFieldStatuses(List.of(FieldStatus.STATUS_300.getEnumName()));

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactly(
              nnsField1.getCorrectedLegalFieldName()
          );
    }
  }

  @Nested
  class GivenFilteringByParentField {
    private Field parentField;
    private Field childField1;
    private Field childField2;
    private Field fieldWithNoChildren;

    @BeforeEach
    void setUp() {
      parentField = FieldIntegrationTestUtils.createField("Parent field", entityManager);

      childField1 = FieldIntegrationTestUtils.createField("Child field 1", entityManager);
      childField1.setParentDeterminedField(parentField);

      childField2 = FieldIntegrationTestUtils.createField("Child field 2", entityManager);
      childField2.setParentDeterminedField(parentField);

      fieldWithNoChildren = FieldIntegrationTestUtils.createField("Field with no children", entityManager);
    }

    @Test
    void whenNoParentFieldSelected_thenReturnAll() {
      form.setParentFieldName(null);

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              childField1.getCorrectedLegalFieldName(),
              childField2.getCorrectedLegalFieldName(),
              parentField.getCorrectedLegalFieldName(),
              fieldWithNoChildren.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenParentFieldSelected_andParentHasChildren_thenReturnAllChildrenOfParent() {
      form.setParentFieldName(parentField.getCorrectedLegalFieldName());

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              childField1.getCorrectedLegalFieldName(),
              childField2.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenParentFieldSelected_andParentHasNoChildren_thenReturnEmpty() {
      form.setParentFieldName(fieldWithNoChildren.getCorrectedLegalFieldName());

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .isEmpty();
    }

    @Test
    void whenParentFieldSelected_andParentHasChildren_andAlsoFilteringByStatus_thenOnlyReturnChildWithStatus() {
      form.setParentFieldName(parentField.getCorrectedLegalFieldName());

      FieldIntegrationTestUtils.addFieldStatusHistory(childField1, FieldStatus.STATUS_300, entityManager);
      form.setFieldStatuses(List.of(FieldStatus.STATUS_300.getEnumName()));

      assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactly(childField1.getCorrectedLegalFieldName());
    }
  }

  @Test
  void getFieldsWithSubmissionsAndWithout() {
    var field1 = FieldIntegrationTestUtils.createField("Field 1", entityManager);
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(field1.getResource(), entityManager);
    SubmissionIntegrationTestUtils.createSubmissionDetail(submission, SubmissionType.CREATE_CSR, entityManager);
    var field2 = FieldIntegrationTestUtils.createField("Field 2", entityManager);
    assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
        .extracting(ResourceRecordQueryResult::resourceId)
        .containsExactlyInAnyOrder(
            field1.getResource().getId(),
            field2.getResource().getId()
        );
  }

  @Test
  void getFieldsWithMultipleSubmissions() {
    var field1 = FieldIntegrationTestUtils.createField("Field 1", entityManager);
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(field1.getResource(), entityManager);
    SubmissionIntegrationTestUtils.createSubmissionDetail(submission, SubmissionType.CREATE_CSR, entityManager);
    var field2 = FieldIntegrationTestUtils.createField("Field 2", entityManager);
    var submission2 = SubmissionTestUtils.createSubmissionForIntegrationTest(field2.getResource(), entityManager);
    SubmissionIntegrationTestUtils.createSubmissionDetail(submission2, SubmissionType.CREATE_FDP, entityManager);
    var submission3 = SubmissionTestUtils.createSubmissionForIntegrationTest(field2.getResource(), entityManager);
    SubmissionIntegrationTestUtils.createSubmissionDetail(
        submission3,
        SubmissionType.CREATE_CSR,
        SubmissionDetailStatus.DRAFT,
        entityManager
    );
    var field3 = FieldIntegrationTestUtils.createField("Field 3", entityManager);
    assertThat(regulatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER))
        .extracting(ResourceRecordQueryResult::resourceId)
        .containsExactlyInAnyOrder(
            field1.getResource().getId(),
            field2.getResource().getId(),
            field3.getResource().getId()
        );
  }

  private void setUpRegulatorTest() {
    var user = UserTestUtil.Builder()
        .withWuaId(1000L)
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    Team team;

    try {
      team = entityManager.createQuery("SELECT t FROM Team t WHERE t.teamType = :teamType", Team.class)
          .setParameter("teamType", TeamType.REGULATOR)
          .getSingleResult();
    } catch (NoResultException exception) {
      team = null;
    }

    // Stub team if not already created
    if (team == null) {
      team = TeamTestUtil.Builder()
          .withTeamType(TeamType.REGULATOR)
          .withId(null)
          .buildStaticTeam();

      entityManager.persist(team);
      entityManager.flush();
    }

    Team finalTeam = team;
    var teamRole = new TeamRole();
    teamRole.setRole(Role.EXPLORATION);
    teamRole.setWuaId(user.wuaId());
    teamRole.setTeam(finalTeam);
    entityManager.persist(teamRole);
    entityManager.flush();
  }
}
