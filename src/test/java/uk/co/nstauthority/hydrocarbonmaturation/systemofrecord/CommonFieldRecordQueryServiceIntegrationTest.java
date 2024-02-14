package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Transactional
@IntegrationTest
public class CommonFieldRecordQueryServiceIntegrationTest {
  @Autowired
  private DSLContext dslContext;

  @Autowired
  private RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  private SearchResourceRecordsForm form;

  @BeforeEach
  void setUp() {
    setUpRegulatorTest();

    form = new SearchResourceRecordsForm();
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
      form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));

      status200Field1 = FieldIntegrationTestUtils.createField("Field status 200 1", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status200Field1, FieldStatus.STATUS_200, entityManager);

      status200Field2 = FieldIntegrationTestUtils.createField("Field status 200 2", entityManager);
      FieldIntegrationTestUtils.addFieldStatusHistory(status200Field2, FieldStatus.STATUS_200, entityManager);

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
      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

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

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactly(
              status300Field.getCorrectedLegalFieldName()
          );
    }

    @Test
    void whenMultipleFieldStatusesSelected_thenAllWithFieldStatusesReturned() {
      form.setFieldStatuses(List.of(FieldStatus.STATUS_200.getEnumName(), FieldStatus.STATUS_700.getEnumName()));

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

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

      var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

      assertThat(results)
          .extracting(ResourceRecordQueryResult::resourceName)
          .containsExactlyInAnyOrder(
              status200Field1.getCorrectedLegalFieldName()
          );
    }
  }

  private void setUpRegulatorTest() {
    var user = UserTestUtil.Builder()
        .withWuaId(1000L)
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(null)
        .build();

    entityManager.persist(team);
    entityManager.flush();
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        Set.of(RegulatorTeamRole.EXPLORATION.name()));
  }
}
