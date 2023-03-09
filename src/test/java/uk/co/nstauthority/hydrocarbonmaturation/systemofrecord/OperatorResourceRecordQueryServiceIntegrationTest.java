package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
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
  private TestEntityManager entityManager;

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
              ResourceRecordQueryResult::getOrganisationResponsibleId,
              result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
              result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
          )
          .containsExactly(
              tuple(
                  submissionUserIsResponsibleFor.getId(),
                  organisationUnitIdUserIsInGroupFor,
                  submissionUserIsResponsibleFor.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                  submissionUserIsResponsibleFor.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
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
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionUserIsResponsibleFor.getId(),
                organisationUnitIdUserIsInGroupFor,
                submissionUserIsResponsibleFor.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionUserIsResponsibleFor.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
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
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                submissionUserIsResponsibleFor.getId(),
                organisationUnitIdUserIsInGroupFor,
                submissionUserIsResponsibleFor.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionUserIsResponsibleFor.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
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

  private void setUpIndustryTest(Set<TeamRole> userRoles, List<Integer> userOrgUnitIds) {
    var user = ServiceUserDetailTestUtil.Builder()
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

    entityManager.persistAndFlush(team);

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

  private ResourceLink createResourceLink(Resource prospect, Resource discovery) {
    var resourceLink = new ResourceLink();
    resourceLink.setDiscoveryResource(discovery);
    resourceLink.setProspectResource(prospect);

    entityManager.persistAndFlush(resourceLink);

    return resourceLink;
  }

  private Licence createLicence(int id, String ref, GeneralDetail generalDetails) {

    return SubmissionIntegrationTestUtils.createLicence(id, ref, generalDetails, entityManager);
  }

  private OpportunityDetail createOpportunityDetail(OpportunityType opportunityType, SubmissionDetail submissionDetail) {
    var opportunityDetail = new OpportunityDetail();
    opportunityDetail.setOpportunityType(opportunityType);
    opportunityDetail.setSubmissionDetail(submissionDetail);

    entityManager.persistAndFlush(opportunityDetail);

    return opportunityDetail;
  }


  private TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock) {

    return SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail, centreOfInterestQuadrantBlock, entityManager);
  }

  private QuadrantAndBlock createQuadAndBlock(GeneralDetail generalDetails, String quadrantBlock) {

    return SubmissionIntegrationTestUtils.createQuadAndBlock(generalDetails, quadrantBlock, entityManager);
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail) {

    return SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
  }

  private ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId) {

    return SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, resourceName, organisationId, entityManager);
  }

  private Resource buildProspectResource() {

    return SubmissionIntegrationTestUtils.createResource(entityManager);
  }

  private Resource buildDiscoveryResource() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);

    entityManager.persistAndFlush(resource);

    return resource;
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType) {

    return SubmissionIntegrationTestUtils.createSubmissionDetail(resource, submissionType, entityManager);
  }
}
