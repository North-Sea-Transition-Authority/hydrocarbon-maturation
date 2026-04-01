package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Transactional
@IntegrationTest
class OperatorResourceRecordQueryServiceIntegrationTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private OperatorResourceRecordQueryService operatorResourceRecordQueryService;

  @Autowired
  private EntityManager entityManager;

  @MockitoBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @MockitoBean
  private LicenceQueryService licenceQueryService;

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenIncompleteResourceName_assertOrderedResultsAllContainTextIgnoreCasing() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.values()), List.of(organisationUnitIdUserIsInGroupFor));

    var resource1 = buildProspectResource();
    var submission1 =
        buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT, null);
    var resourceName1 = "Resource";
    var generalDetail1 = createGeneralDetail(submission1, null);
    createResourceDetail(
        submission1, resourceName1, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId1 = 1;
    createProspectAndDiscoveryLicence(extantLicenceId1, "licenceRef", generalDetail1);

    var resource2 = buildDiscoveryResource();
    var submission2 =
        buildSubmissionDetail(resource2, SubmissionType.CREATE_DISCOVERY, null);
    var resourceName2 = "A resource";
    var generalDetail2 = createGeneralDetail(submission2, null);
    createResourceDetail(
        submission2, resourceName2, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId2 = 1;
    createProspectAndDiscoveryLicence(extantLicenceId2, "licenceRef", generalDetail2);

    var resource3 = buildProspectResource();
    var submission3 =
        buildSubmissionDetail(resource3, SubmissionType.CREATE_DISCOVERY, null);
    var resourceName3 = "Something else";
    var generalDetail3 = createGeneralDetail(submission3, null);
    createResourceDetail(
        submission3, resourceName3, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId3 = 1;
    createProspectAndDiscoveryLicence(extantLicenceId3, "licenceRef", generalDetail3);

    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.getEnumName(), ResourceType.DISCOVERY.getEnumName()));
    form.setResourceNameFromResourceDetail("Resourc");

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceName
        )
        .containsExactly(
            resourceName2, resourceName1
        );
  }

  @ParameterizedTest
  @EnumSource(GeographicalArea.class)
  void getProspectAndDiscoveryRecordSearchResults_andFilterByGeographicalArea_assertOnlyTypeSpecifiedDisplayed(
      GeographicalArea geographicalArea) {
    var userOrgUnitId = 1;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT), List.of(userOrgUnitId));

    var resourceWithGeographicalArea = buildProspectResource();
    var submissionWithGeographicalArea =
        buildSubmissionDetail(resourceWithGeographicalArea, SubmissionType.CREATE_PROSPECT, null);
    buildRegulatorManagedInformation(resourceWithGeographicalArea, geographicalArea, ResponsibleTeamType.EXPLORATION);
    createResourceDetail(
        submissionWithGeographicalArea, "Not relevant name", userOrgUnitId);
    var generalDetailWithGeographicalArea = createGeneralDetail(submissionWithGeographicalArea, null);
    var extantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", generalDetailWithGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceDto("licenceRef", 1, LicenceStatus.EXTANT)));

    var resourceWithOnshoreGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithOnshoreGeographicalArea, GeographicalArea.ONSHORE, ResponsibleTeamType.EXPLORATION);
    var submissionWithOnshoreGeographicalArea =
        buildSubmissionDetail(resourceWithOnshoreGeographicalArea, SubmissionType.CREATE_PROSPECT, null);
    createResourceDetail(
        submissionWithOnshoreGeographicalArea, "Not relevant name", userOrgUnitId);
    var generalDetailWithOnshoreGeographicalArea = createGeneralDetail(submissionWithOnshoreGeographicalArea, null);
    var extantLicence2 = createProspectAndDiscoveryLicence(1, "licenceRef", generalDetailWithOnshoreGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence2.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceDto("licenceRef", 1, LicenceStatus.EXTANT)));

    var resourceWithNoGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithNoGeographicalArea, null, ResponsibleTeamType.EXPLORATION);
    var submissionWithNoGeographicalArea =
        buildSubmissionDetail(resourceWithNoGeographicalArea, SubmissionType.CREATE_PROSPECT, null);
    var generalDetailWithNoGeographicalArea = createGeneralDetail(submissionWithNoGeographicalArea, null);
    createResourceDetail(
        submissionWithNoGeographicalArea, "Not relevant name", userOrgUnitId);
    var extantLicence3 = createProspectAndDiscoveryLicence(1, "licenceRef", generalDetailWithNoGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence3.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceDto("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();
    form.setGeographicalArea(List.of(geographicalArea.name()));

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    if (geographicalArea == GeographicalArea.ONSHORE) {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::submissionDetailId)
          .containsExactly(
              submissionWithOnshoreGeographicalArea.getId(),
              submissionWithGeographicalArea.getId()
          );
    } else {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::submissionDetailId)
          .containsExactly(
              submissionWithGeographicalArea.getId()
          );
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getProspectAndDiscoveryRecordSearchResults_whenSubmissionWithdrawn_assertOnlyNonWithdrawnDisplayed(
      Boolean withdrawn) {
    if(!withdrawn) {
      withdrawn = null;
    }
    var userOrgUnitId = 1;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT), List.of(userOrgUnitId));

    var resourceWithWithdrawnSubmission = buildDiscoveryResource();
    var submissionDetailWithWithdrawnSubmission =
        buildSubmissionDetail(resourceWithWithdrawnSubmission, SubmissionType.CREATE_DISCOVERY, null);
    var withdrawnSubmission =
        SubmissionIntegrationTestUtils.createSubmission(resourceWithWithdrawnSubmission, withdrawn, entityManager);
    submissionDetailWithWithdrawnSubmission.setSubmission(withdrawnSubmission);
    entityManager.persist(submissionDetailWithWithdrawnSubmission);
    entityManager.flush();
    createResourceDetail(
        submissionDetailWithWithdrawnSubmission, "Not relevant name", userOrgUnitId);
    var generalDetail = createGeneralDetail(submissionDetailWithWithdrawnSubmission, null);
    var extantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", generalDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceDto("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    if (BooleanUtils.isTrue(withdrawn)) {
      assertThat(results).isEmpty();
    } else {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::submissionDetailId)
          .containsExactly(
              submissionDetailWithWithdrawnSubmission.getId()
          );
    }
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenUserIndustry_assertArchivedNotDisplayed() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER), List.of(organisationUnitIdUserIsInGroupFor));

    var archivedResource = buildArchivedProspectResource();
    var archivedSubmission = buildSubmissionDetail(archivedResource, SubmissionType.CREATE_PROSPECT, null);
    var archivedGeneralDetail = createGeneralDetail(archivedSubmission, null);
    createResourceDetail(archivedSubmission, "Not relevant name", organisationUnitIdUserIsInGroupFor);
    var archivedExtantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", archivedGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(archivedExtantLicence.getLicenceId().toString())))
            .thenReturn(List.of(new LicenceDto("licenceRef", 1, LicenceStatus.EXTANT)));


    var nonArchivedResource = buildProspectResource();
    var nonArchivedSubmission = buildSubmissionDetail(nonArchivedResource, SubmissionType.CREATE_PROSPECT, null);
    var nonArchivedGeneralDetail = createGeneralDetail(nonArchivedSubmission, null);
    createResourceDetail(nonArchivedSubmission, "Not relevant name", organisationUnitIdUserIsInGroupFor);
    var nonArchivedextantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", nonArchivedGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(nonArchivedextantLicence.getLicenceId().toString())))
            .thenReturn(List.of(new LicenceDto("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();
    form.setShowArchivedResources(true);

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results).extracting(
                    ResourceRecordQueryResult::submissionDetailId)
            .containsExactly(
                    nonArchivedSubmission.getId()
            );
  }

  @Test
  void getFieldRecordsSearchResults_whenIncompleteFieldName_assertResultsAllContainText() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT), List.of(organisationUnitIdUserIsInGroupFor));

    Stream.of("JH Field", "jh Field 2", "JH Field With More Text", "No sub string")
        .forEach(fieldName -> buildFieldResultWithFieldName(fieldName, organisationUnitIdUserIsInGroupFor));

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));
    form.setFieldNameForResourceSubmission("JH Fiel");

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    var fieldNames = List.of("JH Field", "jh Field 2", "JH Field With More Text");
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceName
        )
        .containsExactlyInAnyOrderElementsOf(
          fieldNames
        );
  }

  private void buildFieldResultWithFieldName(String fieldName, int organisationUnitIdUserIsInGroupFor) {
    var resource = buildFieldResource();
    buildSubmissionDetail(resource, SubmissionType.CREATE_CSR, null);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(resource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var field = SubmissionIntegrationTestUtils.createFieldWithParent(resource, fieldName,
        organisationUnitIdUserIsInGroupFor, entityManager);
    SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, LocalDate.now(),
        entityManager);
    createFieldLicence(1, "relevant", field);
    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));
  }

  @Test
  void getFieldRecordSearchResults_whenUserIndustry_assertArchivedNotDisplayed() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER), List.of(organisationUnitIdUserIsInGroupFor));

    var archivedResource = buildArchivedFieldResource();
    buildSubmissionDetail(archivedResource, SubmissionType.CREATE_FIELD, null);
    var archivedResourceName = "Field A";
    SubmissionIntegrationTestUtils
            .createRegulatorManagedInformation(archivedResource, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var archivedField = SubmissionIntegrationTestUtils
            .createFieldWithParent(archivedResource, archivedResourceName, organisationUnitIdUserIsInGroupFor, entityManager);
    SubmissionIntegrationTestUtils
            .createFieldStatusHistory(archivedField, FieldStatus.STATUS_300, LocalDate.now(), entityManager);
    createFieldLicence(1, "relevant", archivedField);

    var nonArchivedResource = buildFieldResource();
    buildSubmissionDetail(nonArchivedResource, SubmissionType.CREATE_FIELD, null);
    var nonArchivedResourceName = "Field A";
    var nonArchivedRegulatorManagedInformation = SubmissionIntegrationTestUtils
            .createRegulatorManagedInformation(nonArchivedResource, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var nonArchivedField = SubmissionIntegrationTestUtils
            .createFieldWithParent(nonArchivedResource, nonArchivedResourceName, organisationUnitIdUserIsInGroupFor, entityManager);
    var fieldStatusHistoryForNonArchivedField = SubmissionIntegrationTestUtils
            .createFieldStatusHistory(nonArchivedField, FieldStatus.STATUS_300, LocalDate.now(), entityManager);
    createFieldLicence(1, "relevant", nonArchivedField);

    when(licenceQueryService.getExtantLicencesByIds(any()))
            .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));
    form.setShowArchivedResources(true);

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results)
            .extracting(
                    ResourceRecordQueryResult::resourceId,
                    ResourceRecordQueryResult::resourceType,
                    ResourceRecordQueryResult::resourceName,
                    ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
                    ResourceRecordQueryResult::responsibleTeam,
                    ResourceRecordQueryResult::parentFieldName,
                    ResourceRecordQueryResult::fieldStatus,
                    ResourceRecordQueryResult::active
            )
            .containsExactly(
                    tuple(
                            nonArchivedResource.getId(),
                            ResourceType.FIELD,
                            nonArchivedResourceName,
                            null,
                            nonArchivedRegulatorManagedInformation.getResponsibleTeamType(),
                            nonArchivedField.getParentDeterminedField().getCorrectedLegalFieldName(),
                            fieldStatusHistoryForNonArchivedField.getFieldStatus(),
                            nonArchivedResource.getActive()
                    )
            );
  }

  private void setUpIndustryTest(Set<Role> userRoles, List<Integer> userOrgUnitIds) {
    var user = UserTestUtil.Builder()
        .withWuaId(USER.wuaId())
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    var userOrgGroupId = 1;

    var userOrgGroup = new OrganisationGroup();
    userOrgGroup.setOrganisationGroupId(userOrgGroupId);
    var userOrgUnits = userOrgUnitIds.stream()
        .map(id -> OrganisationUnitTestUtil.Builder().withOrganisationId(id).build())
        .toList();

    userOrgGroup.setOrganisationUnits(userOrgUnits);

    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(userOrgGroupId)))
        .thenReturn(List.of(userOrgGroup));

    Team team;

    try {
      team = entityManager.createQuery("SELECT t FROM Team t WHERE t.teamType = :teamType AND t.scopeId = :scopeId AND t.scopeType = :scopeType", Team.class)
          .setParameter("teamType", TeamType.INDUSTRY)
          .setParameter("scopeId", userOrgGroupId)
          .setParameter("scopeType", "ORGGRP")
          .getSingleResult();
    } catch (NoResultException exception) {
      team = null;
    }

    // Stub team if not already created
    if (team == null) {
      team = TeamTestUtil.Builder()
          .withTeamType(TeamType.INDUSTRY)
          .withId(null)
          .withScopeId(String.valueOf(userOrgGroupId))
          .withScopeType("ORGGRP")
          .buildScopedTeam();

      entityManager.persist(team);
      entityManager.flush();
    }

    Team finalTeam = team;
    userRoles.forEach(userRole -> {
      var teamRole = new TeamRole();
      teamRole.setRole(userRole);
      teamRole.setWuaId(user.wuaId());
      teamRole.setTeam(finalTeam);
      entityManager.persist(teamRole);
      entityManager.flush();
    });
  }

  private void buildRegulatorManagedInformation(
      Resource resource, GeographicalArea geographicalArea, ResponsibleTeamType responsibleTeamType) {
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        resource, responsibleTeamType, geographicalArea, entityManager);
  }

  private Licence createProspectAndDiscoveryLicence(int id, String ref, GeneralDetail generalDetails) {

    return SubmissionIntegrationTestUtils.createLicence(id, ref, generalDetails, entityManager);
  }

  private void createFieldLicence(int id, String ref, Field field) {
    FieldIntegrationTestUtils.createLicence(id, ref, field, entityManager);
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail, String licenceApplicationReference) {

    return SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager, licenceApplicationReference);
  }

  private void createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId) {
    SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, resourceName, organisationId, entityManager);
  }

  private Resource buildProspectResource() {
    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
  }

  private Resource buildArchivedProspectResource() {
    return SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.PROSPECT);
  }

  private Resource buildDiscoveryResource() {
    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
  }

  private Resource buildFieldResource() {
    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
  }

  private Resource buildArchivedFieldResource() {
    return SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.FIELD);
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource,
                                                 SubmissionType submissionType,
                                                 Instant acceptedTimestamp) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager,
        acceptedTimestamp);
  }
}
