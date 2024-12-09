package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Transactional
@IntegrationTest
class FieldPersistenceServiceIntegrationTest {

  @Autowired
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private FieldPersistenceService fieldPersistenceService;

  @BeforeEach
  public void setUp() {
    setUpAreaUserTest();
  }

  @Test
  void saveFieldFromForm() {
    var oldField = FieldIntegrationTestUtils.buildFieldWithFieldNumber("Good Field 1", entityManager, 1);

    var resource = ResourceIntegrationTestUtils.buildFieldResource(entityManager);

    var form = new FieldForm();
    form.setLegalFieldName("legal name");
    form.setCorrectedLegalFieldName("corrected legal name");
    form.setCommonNameForField("common name");
    form.setPrePearsOrganisationId("123");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setGeographicalArea(GeographicalArea.NORTHERN_NORTH_SEA);
    form.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    form.setDeterminationStatus(DeterminationStatus.DETERMINED);
    form.setInfrastructureHost(true);
    form.setAssociatedDiscoveries(List.of());
    form.setFieldLocatedInUkcs(true);
    form.setFieldLocatedInEngland(null);
    form.setIsOnLicence(false);
    form.setLicences(List.of());

    fieldPersistenceService.saveFieldFromFieldForm(form, resource);

    assertThat(fieldPersistenceService.getFieldOrThrow(resource)).extracting(
        Field::getLegalFieldName,
        Field::getCorrectedLegalFieldName,
        Field::getCommonNameForField,
        Field::getPrePearsOrganisationId,
        Field::getOnshoreOffshore,
        Field::getDeterminationStatus,
        Field::getInfrastructureHost,
        Field::getFieldLocatedInUkcs,
        Field::getIsOnPrePearsLicence,
        Field::getFieldNumber
    ).containsExactly(
        form.getLegalFieldName(),
        form.getCorrectedLegalFieldName(),
        form.getCommonNameForField(),
        Integer.valueOf(form.getPrePearsOrganisationId()),
        form.getOnshoreOffshore(),
        form.getDeterminationStatus(),
        form.getInfrastructureHost(),
        form.getFieldLocatedInUkcs(),
        form.getIsOnLicence(),
        oldField.getFieldNumber() + 1
    );

    assertThat(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .extracting(
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea
        ).containsExactly(
            form.getResponsibleTeamType(),
            form.getGeographicalArea()
        );
  }

  private void setUpAreaUserTest() {
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
          .setParameter("teamType", TeamType.NORTHERN_OFFSHORE_AREA)
          .getSingleResult();
    } catch (NoResultException exception) {
      team = null;
    }

    // Stub team if not already created
    if (team == null) {
      team = TeamTestUtil.Builder()
          .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
          .withId(null)
          .buildStaticTeam();

      entityManager.persist(team);
      entityManager.flush();
    }

    Team finalTeam = team;
    var teamRole = new TeamRole();
    teamRole.setRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM);
    teamRole.setWuaId(user.wuaId());
    teamRole.setTeam(finalTeam);
    entityManager.persist(teamRole);
    entityManager.flush();
  }

}
