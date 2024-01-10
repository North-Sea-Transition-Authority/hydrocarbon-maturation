package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;

@Transactional
@IntegrationTest
class OperatorResourceRecordQueryServiceIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private OperatorResourceRecordQueryService operatorResourceRecordQueryService;

  @Autowired
  private EntityManager entityManager;

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private LicenceQueryService licenceQueryService;

  @ParameterizedTest
  @EnumSource(IndustryTeamRole.class)
  void getResourceRecordSearchResults_whenUserNotRegulator_assertOnlyUserTeamsResourcesDisplayedAndAssertValues(
      IndustryTeamRole industryTeamRole) {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(industryTeamRole), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    var generalDetailUserIsResponsibleFor = createGeneralDetail(submissionUserIsResponsibleFor);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsInGroupFor);
    createLicence(1, "not relevant", generalDetailUserIsResponsibleFor);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor = 48;
    var generalDetailUserNotResponsibleFor = createGeneralDetail(submissionUserIsNotResponsibleFor);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor);
    createLicence(1, "not relevant", generalDetailUserNotResponsibleFor);
    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));
    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    if (industryTeamRole == IndustryTeamRole.ACCESS_MANAGER) {
      assertThat(results).isEmpty();
    } else {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::getSubmissionDetailId,
              ResourceRecordQueryResult::getOrganisationResponsibleId
          )
          .containsExactly(
              tuple(
                  submissionUserIsResponsibleFor.getId(),
                  organisationUnitIdUserIsInGroupFor
              )
          );
    }
  }

  @ParameterizedTest
  @EnumSource(IndustryTeamRole.class)
  void getResourceRecordSearchResults_whenUserIndustry_whenUserNotInAnyTeams_assertEmpty(
      IndustryTeamRole industryTeamRole) {
    var organisationUnitIdUserIsNotIn = 46;
    setUpIndustryTest(Set.of(industryTeamRole), List.of());

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    createGeneralDetail(submissionUserIsResponsibleFor);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsNotIn);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor2 = 48;
    createGeneralDetail(submissionUserIsNotResponsibleFor);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor2);

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results).isEmpty();
  }

  @Test
  void getResourceRecordSearchResults_whenUserIndustry_whenHasAllRoles_assertDisplayed() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(IndustryTeamRole.values()), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    var generalDetailUserIsResponsibleFor = createGeneralDetail(submissionUserIsResponsibleFor);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId = 1;

    var extantLicence = createLicence(extantLicenceId, "licenceRef", generalDetailUserIsResponsibleFor);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor = 48;
    var generalDetailUserIsNotResponsibleFor = createGeneralDetail(submissionUserIsNotResponsibleFor);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor);

    var extantLicence2 = createLicence(extantLicenceId, "licenceRef", generalDetailUserIsNotResponsibleFor);
    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(new LicenceView(
            extantLicence.getLicenceReference(),
            extantLicenceId,
            LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getOrganisationResponsibleId
        )
        .containsExactly(
            tuple(
                submissionUserIsResponsibleFor.getId(),
                organisationUnitIdUserIsInGroupFor
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenUserIndustry_whenHasSubmitterAndViewer_andOnExtantLicence_assertDisplayed() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(IndustryTeamRole.SUBMITTER, IndustryTeamRole.VIEWER), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    var generalDetailUserIsResponsibleFor = createGeneralDetail(submissionUserIsResponsibleFor);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId = 1;

    var extantLicence = createLicence(extantLicenceId, "licenceRef", generalDetailUserIsResponsibleFor);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor = 48;
    var nonExtantLicenceId = 2;
    var generalDetailUserIsNotResponsibleFor = createGeneralDetail(submissionUserIsNotResponsibleFor);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor);
    var nonExtantLicence = createLicence(nonExtantLicenceId, "licenceRef", generalDetailUserIsNotResponsibleFor);

    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(
            new LicenceView(extantLicence.getLicenceReference(), extantLicenceId, LicenceStatus.EXTANT),
            new LicenceView(nonExtantLicence.getLicenceReference(), nonExtantLicenceId, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getOrganisationResponsibleId)
        .containsExactly(
            tuple(
                submissionUserIsResponsibleFor.getId(),
                organisationUnitIdUserIsInGroupFor
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenUserIndustry_whenUserSubmitterAndViewer_andNotOnAlicenceAndNotExtant_assertNoneDisplayed() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(IndustryTeamRole.SUBMITTER, IndustryTeamRole.VIEWER), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceWithoutLicence = buildProspectResource();
    var submissionWithoutLicence =
        buildSubmissionDetail(resourceWithoutLicence, SubmissionType.CREATE_PROSPECT);
    var generalDetailWithoutLicence = createGeneralDetail(submissionWithoutLicence);
    createResourceDetail(
        submissionWithoutLicence, "Not relevant name", organisationUnitIdUserIsInGroupFor);

    var resourceWithNotExtantLicence = buildProspectResource();
    var submissionWithNotExtantLicence =
        buildSubmissionDetail(resourceWithNotExtantLicence, SubmissionType.CREATE_PROSPECT);
    var nonExtantLicenceId = 2;
    var generalDetailWithNonExtantLicence = createGeneralDetail(submissionWithNotExtantLicence);
    createResourceDetail(
        submissionWithNotExtantLicence, "Not relevant name", organisationUnitIdUserIsInGroupFor);
    var notExtantLicence = createLicence(nonExtantLicenceId, "licenceRef", generalDetailWithNonExtantLicence);
    when(licenceQueryService.getExtantLicencesByIds(List.of(notExtantLicence.getLicenceId().toString())))
        .thenReturn(List.of());
    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(GeographicalArea.class)
  void getResourceRecordSearchResults_andFilterByGeographicalArea_assertOnlyTypeSpecifiedDisplayed(
      GeographicalArea geographicalArea) {
    var userOrgUnitId = 1;
    setUpIndustryTest(Set.of(IndustryTeamRole.SUBMITTER), List.of(userOrgUnitId));

    var resourceWithGeographicalArea = buildProspectResource();
    var submissionWithGeographicalArea =
        buildSubmissionDetail(resourceWithGeographicalArea, SubmissionType.CREATE_PROSPECT);
    buildRegulatorManagedInformation(resourceWithGeographicalArea, geographicalArea);
    createResourceDetail(
        submissionWithGeographicalArea, "Not relevant name", userOrgUnitId);
    var generalDetailWithGeographicalArea = createGeneralDetail(submissionWithGeographicalArea);
    var extantLicence = createLicence(1, "licenceRef", generalDetailWithGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var resourceWithOnshoreGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithOnshoreGeographicalArea, GeographicalArea.ONSHORE);
    var submissionWithOnshoreGeographicalArea =
        buildSubmissionDetail(resourceWithOnshoreGeographicalArea, SubmissionType.CREATE_PROSPECT);
    createResourceDetail(
        submissionWithOnshoreGeographicalArea, "Not relevant name", userOrgUnitId);
    var generalDetailWithOnshoreGeographicalArea = createGeneralDetail(submissionWithOnshoreGeographicalArea);
    var extantLicence2 = createLicence(1, "licenceRef", generalDetailWithOnshoreGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence2.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var resourceWithNoGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithNoGeographicalArea, null);
    var submissionWithNoGeographicalArea =
        buildSubmissionDetail(resourceWithNoGeographicalArea, SubmissionType.CREATE_PROSPECT);
    var generalDetailWithNoGeographicalArea = createGeneralDetail(submissionWithNoGeographicalArea);
    createResourceDetail(
        submissionWithNoGeographicalArea, "Not relevant name", userOrgUnitId);
    var extantLicence3 = createLicence(1, "licenceRef", generalDetailWithNoGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence3.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();
    form.setGeographicalArea(List.of(geographicalArea.name()));

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    if (geographicalArea == GeographicalArea.ONSHORE) {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::getSubmissionDetailId)
          .containsExactly(
              submissionWithOnshoreGeographicalArea.getId(),
              submissionWithGeographicalArea.getId()
          );
    } else {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::getSubmissionDetailId)
          .containsExactly(
              submissionWithGeographicalArea.getId()
          );
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getResourceRecordSearchResults_andFilterByPreferredView_assertPreferredAndNonPreferredReturned(
      boolean showOnlyPreferredView) {
    //the filter for preferred view should not work for operators
    var userOrgUnitId = 1;
    setUpIndustryTest(Set.of(IndustryTeamRole.SUBMITTER), List.of(userOrgUnitId));

    var preferredViewResource = buildProspectResource();
    var preferredViewSubmissionDetail =
        buildSubmissionDetail(preferredViewResource, SubmissionType.CREATE_PROSPECT);
    createResourceDetail(
        preferredViewSubmissionDetail, "Not relevant name", userOrgUnitId);
    buildResourceGroupLink(true, preferredViewResource);
    var preferredViewGeneralDetail = createGeneralDetail(preferredViewSubmissionDetail);
    var extantLicence = createLicence(1, "licenceRef", preferredViewGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var nonPreferredViewResource = buildProspectResource();
    var nonPreferredSubmissionDetail =
        buildSubmissionDetail(nonPreferredViewResource, SubmissionType.CREATE_PROSPECT);
    buildResourceGroupLink(false, nonPreferredViewResource);
    createResourceDetail(
        nonPreferredSubmissionDetail, "Not relevant name", userOrgUnitId);
    var nonPreferredGeneralDetail = createGeneralDetail(nonPreferredSubmissionDetail);
    var extantLicence2 = createLicence(1, "licenceRef", nonPreferredGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence2.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();
    form.setShowOnlyPreferredViews(showOnlyPreferredView);

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId)
        .containsExactly(
            nonPreferredSubmissionDetail.getId(),
            preferredViewSubmissionDetail.getId()
        );
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getResourceRecordSearchResults_whenSubmissionWithdrawn_assertOnlyNonWithdrawnDisplayed(
      Boolean withdrawn) {
    if(!withdrawn) {
      withdrawn = null;
    }
    var userOrgUnitId = 1;
    setUpIndustryTest(Set.of(IndustryTeamRole.SUBMITTER), List.of(userOrgUnitId));

    var resourceWithWithdrawnSubmission = buildDiscoveryResource();
    var submissionDetailWithWithdrawnSubmission =
        buildSubmissionDetail(resourceWithWithdrawnSubmission, SubmissionType.CREATE_DISCOVERY);
    var withdrawnSubmission =
        SubmissionIntegrationTestUtils.createSubmission(resourceWithWithdrawnSubmission, withdrawn, entityManager);
    submissionDetailWithWithdrawnSubmission.setSubmission(withdrawnSubmission);
    entityManager.persist(submissionDetailWithWithdrawnSubmission);
    entityManager.flush();
    createResourceDetail(
        submissionDetailWithWithdrawnSubmission, "Not relevant name", userOrgUnitId);
    var preferredViewGeneralDetail = createGeneralDetail(submissionDetailWithWithdrawnSubmission);
    var extantLicence = createLicence(1, "licenceRef", preferredViewGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    if (BooleanUtils.isTrue(withdrawn)) {
      assertThat(results).isEmpty();
    } else {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::getSubmissionDetailId)
          .containsExactly(
              submissionDetailWithWithdrawnSubmission.getId()
          );
    }
  }

  private void setUpIndustryTest(Set<TeamRole> userRoles, List<Integer> userOrgUnitIds) {
    var user = UserTestUtil.Builder()
        .withWuaId(1000L)
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    var userOrgGroupId = 1;

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(null)
        .withOrganisationGroupId(userOrgGroupId)
        .build();

    entityManager.persist(team);
    entityManager.flush();

    var userOrgGroup = new OrganisationGroup();
    userOrgGroup.setOrganisationGroupId(userOrgGroupId);
    var userOrgUnits = userOrgUnitIds.stream()
        .map(id -> OrganisationUnitTestUtil.Builder().withOrganisationId(id).build())
        .toList();

    userOrgGroup.setOrganisationUnits(userOrgUnits);

    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(userOrgGroupId)))
        .thenReturn(List.of(userOrgGroup));

    // Add user to team with the previously defined roles
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        TeamRoleUtil.getRoleNames(userRoles));
  }

  private RegulatorManagedInformation buildRegulatorManagedInformation(
      Resource resource, GeographicalArea geographicalArea) {
    return SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        resource, ResponsibleTeamType.EXPLORATION, geographicalArea, entityManager);
  }

  private Licence createLicence(int id, String ref, GeneralDetail generalDetails) {

    return SubmissionIntegrationTestUtils.createLicence(id, ref, generalDetails, entityManager);
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail) {

    return SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
  }

  private ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId) {

    return SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, resourceName, organisationId, entityManager);
  }

  private Resource buildProspectResource() {
    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
  }

  private Resource buildDiscoveryResource() {
    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
  }

  private ResourceGroupLink buildResourceGroupLink(boolean preferredView, Resource resource) {
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setPreferredView(preferredView);
    resourceGroupLink.setResourceGroupId(UUID.randomUUID());
    entityManager.persist(resourceGroupLink);
    entityManager.flush();
    return resourceGroupLink;
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager);
  }
}
