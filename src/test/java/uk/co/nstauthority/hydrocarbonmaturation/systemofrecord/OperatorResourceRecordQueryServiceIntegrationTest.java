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
import java.util.UUID;
import java.util.stream.Stream;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
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

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @MockBean
  private LicenceQueryService licenceQueryService;

  @ParameterizedTest
  @EnumSource(value = Role.class, names = {"INDUSTRY_SUBMIT", "INDUSTRY_VIEWER"})
  void getProspectAndDiscoveryRecordSearchResults_whenUserNotRegulator_assertOnlyUserTeamsResourcesDisplayedAndAssertValues(
      Role role) {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(role), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    var generalDetailUserIsResponsibleFor = createGeneralDetail(submissionUserIsResponsibleFor, null);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsInGroupFor);
    createProspectAndDiscoveryLicence(1, "not relevant", generalDetailUserIsResponsibleFor);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor = 48;
    var generalDetailUserNotResponsibleFor = createGeneralDetail(submissionUserIsNotResponsibleFor, null);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor);
    createProspectAndDiscoveryLicence(1, "not relevant", generalDetailUserNotResponsibleFor);
    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));
    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    if (role == Role.MANAGE_TEAM) {
      assertThat(results).isEmpty();
    } else {
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::submissionDetailId,
              ResourceRecordQueryResult::organisationResponsibleId
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
  @EnumSource(value = Role.class, names = {"INDUSTRY_SUBMIT", "INDUSTRY_VIEWER"})
  void getProspectAndDiscoveryRecordSearchResults_whenUserIndustry_whenUserNotInAnyTeams_assertEmpty(
      Role role) {
    var organisationUnitIdUserIsNotIn = 46;
    setUpIndustryTest(Set.of(role), List.of());

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    createGeneralDetail(submissionUserIsResponsibleFor, null);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsNotIn);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor2 = 48;
    createGeneralDetail(submissionUserIsNotResponsibleFor, null);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor2);

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = Role.class, names = {"INDUSTRY_SUBMIT", "INDUSTRY_VIEWER"})
  void getProspectAndDiscoveryRecordSearchResults_whenUserIndustry_whenHasAllRoles_assertDisplayed(Role role) {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(role), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    var licenceApplicationReferenceResponsibleFor = "RT9";
    var generalDetailUserIsResponsibleFor = createGeneralDetail(submissionUserIsResponsibleFor,
        licenceApplicationReferenceResponsibleFor);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId = 1;

    var extantLicence = createProspectAndDiscoveryLicence(extantLicenceId, "licenceRef", generalDetailUserIsResponsibleFor);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor = 48;
    var licenceApplicationReferenceNotResponsibleFor = "RT10";
    var generalDetailUserIsNotResponsibleFor = createGeneralDetail(submissionUserIsNotResponsibleFor,
        licenceApplicationReferenceNotResponsibleFor);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor);

    createProspectAndDiscoveryLicence(extantLicenceId, "licenceRef", generalDetailUserIsNotResponsibleFor);
    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(new LicenceView(
            extantLicence.getLicenceReference(),
            extantLicenceId,
            LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::organisationResponsibleId,
            ResourceRecordQueryResult::licenceApplicationReference
        )
        .containsExactly(
            tuple(
                submissionUserIsResponsibleFor.getId(),
                organisationUnitIdUserIsInGroupFor,
                licenceApplicationReferenceResponsibleFor
            )
        );
  }

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

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceName
        )
        .containsExactly(
            resourceName2, resourceName1
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenUserIndustry_whenHasSubmitterAndViewer_andOnExtantLicence_assertDisplayed() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceUserIsResponsibleFor = buildProspectResource();
    var submissionUserIsResponsibleFor =
        buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsResponsibleFOr = "The resource name A";
    var generalDetailUserIsResponsibleFor = createGeneralDetail(submissionUserIsResponsibleFor, null);
    createResourceDetail(
        submissionUserIsResponsibleFor, resourceNameUserIsResponsibleFOr, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId = 1;

    var extantLicence = createProspectAndDiscoveryLicence(extantLicenceId, "licenceRef", generalDetailUserIsResponsibleFor);

    var resourceUserIsNotResponsibleFor = buildProspectResource();
    var submissionUserIsNotResponsibleFor =
        buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_PROSPECT, null);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor = 48;
    var nonExtantLicenceId = 2;
    var generalDetailUserIsNotResponsibleFor = createGeneralDetail(submissionUserIsNotResponsibleFor, null);
    createResourceDetail(
        submissionUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor);
    var nonExtantLicence = createProspectAndDiscoveryLicence(nonExtantLicenceId, "licenceRef", generalDetailUserIsNotResponsibleFor);

    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(
            new LicenceView(extantLicence.getLicenceReference(), extantLicenceId, LicenceStatus.EXTANT),
            new LicenceView(nonExtantLicence.getLicenceReference(), nonExtantLicenceId, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::organisationResponsibleId)
        .containsExactly(
            tuple(
                submissionUserIsResponsibleFor.getId(),
                organisationUnitIdUserIsInGroupFor
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenUserIndustry_whenUserSubmitterAndViewer_andNotOnAlicenceAndNotExtant_assertNoneDisplayed() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceWithoutLicence = buildProspectResource();
    var submissionWithoutLicence =
        buildSubmissionDetail(resourceWithoutLicence, SubmissionType.CREATE_PROSPECT, null);
    createGeneralDetail(submissionWithoutLicence, null);
    createResourceDetail(
        submissionWithoutLicence, "Not relevant name", organisationUnitIdUserIsInGroupFor);

    var resourceWithNotExtantLicence = buildProspectResource();
    var submissionWithNotExtantLicence =
        buildSubmissionDetail(resourceWithNotExtantLicence, SubmissionType.CREATE_PROSPECT, null);
    var nonExtantLicenceId = 2;
    var generalDetailWithNonExtantLicence = createGeneralDetail(submissionWithNotExtantLicence, null);
    createResourceDetail(
        submissionWithNotExtantLicence, "Not relevant name", organisationUnitIdUserIsInGroupFor);
    var notExtantLicence = createProspectAndDiscoveryLicence(nonExtantLicenceId, "licenceRef", generalDetailWithNonExtantLicence);
    when(licenceQueryService.getExtantLicencesByIds(List.of(notExtantLicence.getLicenceId().toString())))
        .thenReturn(List.of());
    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    assertThat(results).isEmpty();
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
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var resourceWithOnshoreGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithOnshoreGeographicalArea, GeographicalArea.ONSHORE, ResponsibleTeamType.EXPLORATION);
    var submissionWithOnshoreGeographicalArea =
        buildSubmissionDetail(resourceWithOnshoreGeographicalArea, SubmissionType.CREATE_PROSPECT, null);
    createResourceDetail(
        submissionWithOnshoreGeographicalArea, "Not relevant name", userOrgUnitId);
    var generalDetailWithOnshoreGeographicalArea = createGeneralDetail(submissionWithOnshoreGeographicalArea, null);
    var extantLicence2 = createProspectAndDiscoveryLicence(1, "licenceRef", generalDetailWithOnshoreGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence2.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var resourceWithNoGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithNoGeographicalArea, null, ResponsibleTeamType.EXPLORATION);
    var submissionWithNoGeographicalArea =
        buildSubmissionDetail(resourceWithNoGeographicalArea, SubmissionType.CREATE_PROSPECT, null);
    var generalDetailWithNoGeographicalArea = createGeneralDetail(submissionWithNoGeographicalArea, null);
    createResourceDetail(
        submissionWithNoGeographicalArea, "Not relevant name", userOrgUnitId);
    var extantLicence3 = createProspectAndDiscoveryLicence(1, "licenceRef", generalDetailWithNoGeographicalArea);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence3.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();
    form.setGeographicalArea(List.of(geographicalArea.name()));

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
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
  void getProspectAndDiscoveryRecordSearchResults_andFilterByPreferredView_assertPreferredAndNonPreferredReturned(
      boolean showOnlyPreferredView) {
    //the filter for preferred view should not work for operators
    var userOrgUnitId = 1;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT), List.of(userOrgUnitId));

    var preferredViewResource = buildProspectResource();
    var preferredViewSubmissionDetail =
        buildSubmissionDetail(preferredViewResource, SubmissionType.CREATE_PROSPECT, null);
    createResourceDetail(
        preferredViewSubmissionDetail, "Not relevant name", userOrgUnitId);
    buildResourceGroupLink(true, preferredViewResource);
    var preferredViewGeneralDetail = createGeneralDetail(preferredViewSubmissionDetail, null);
    var extantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", preferredViewGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var nonPreferredViewResource = buildProspectResource();
    var nonPreferredSubmissionDetail =
        buildSubmissionDetail(nonPreferredViewResource, SubmissionType.CREATE_PROSPECT, null);
    buildResourceGroupLink(false, nonPreferredViewResource);
    createResourceDetail(
        nonPreferredSubmissionDetail, "Not relevant name", userOrgUnitId);
    var nonPreferredGeneralDetail = createGeneralDetail(nonPreferredSubmissionDetail, null);
    var extantLicence2 = createProspectAndDiscoveryLicence(1, "licenceRef", nonPreferredGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence2.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();
    form.setShowOnlyPreferredViews(showOnlyPreferredView);

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId)
        .containsExactly(
            nonPreferredSubmissionDetail.getId(),
            preferredViewSubmissionDetail.getId()
        );
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
    var preferredViewGeneralDetail = createGeneralDetail(submissionDetailWithWithdrawnSubmission, null);
    var extantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", preferredViewGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

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
            .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));


    var nonArchivedResource = buildProspectResource();
    var nonArchivedSubmission = buildSubmissionDetail(nonArchivedResource, SubmissionType.CREATE_PROSPECT, null);
    var nonArchivedGeneralDetail = createGeneralDetail(nonArchivedSubmission, null);
    createResourceDetail(nonArchivedSubmission, "Not relevant name", organisationUnitIdUserIsInGroupFor);
    var nonArchivedextantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", nonArchivedGeneralDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(nonArchivedextantLicence.getLicenceId().toString())))
            .thenReturn(List.of(new LicenceView("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();
    form.setShowArchivedResources(true);

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
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

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

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
  void getFieldRecordsSearchResults_asIndustryInResponsibleOrganisation_assertOnlyUserTeamsResourcesDisplayedAndAssertValues() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT), List.of(organisationUnitIdUserIsInGroupFor));

    var resourceUserIsResponsibleFor = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
    var fixedInstant = Instant.parse("2022-12-03T10:15:30.00Z");
    buildSubmissionDetail(resourceUserIsResponsibleFor, SubmissionType.CREATE_CSR, fixedInstant);
    var resourceNameUserIsResponsibleFor = "Field A";
    var regulatorManagedInformationUserIsResponsibleFor = SubmissionIntegrationTestUtils
        .createRegulatorManagedInformation(resourceUserIsResponsibleFor, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var fieldUserIsResponsibleFor = SubmissionIntegrationTestUtils
        .createFieldWithParent(resourceUserIsResponsibleFor, resourceNameUserIsResponsibleFor, organisationUnitIdUserIsInGroupFor, entityManager);
    var fieldStatusHistoryForResponsibleField = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(fieldUserIsResponsibleFor, FieldStatus.STATUS_300, LocalDate.now(), entityManager);
    createFieldLicence(1, "relevant", fieldUserIsResponsibleFor);

    var resourceUserIsNotResponsibleFor =  SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
    buildSubmissionDetail(resourceUserIsNotResponsibleFor, SubmissionType.CREATE_CSR, null);
    var resourceNameUserIsNotResponsibleFor = "The resource name B";
    var organisationIdUserIsNotResponsibleFor = 48;
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(resourceUserIsNotResponsibleFor,
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA, GeographicalArea.CENTRAL_NORTH_SEA,
        entityManager);
    var fieldUserIsNotResponsibleFor = SubmissionIntegrationTestUtils
        .createFieldWithParent(resourceUserIsNotResponsibleFor, resourceNameUserIsNotResponsibleFor, organisationIdUserIsNotResponsibleFor, entityManager);
    SubmissionIntegrationTestUtils.createFieldStatusHistory(fieldUserIsNotResponsibleFor, FieldStatus.STATUS_300,
        LocalDate.now(), entityManager);
    createFieldLicence(2, "not relevant", fieldUserIsNotResponsibleFor);
    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceId,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::organisationResponsibleId,
            ResourceRecordQueryResult::responsibleTeam,
            ResourceRecordQueryResult::parentFieldName,
            ResourceRecordQueryResult::fieldStatus,
            ResourceRecordQueryResult::submissionAcceptedTimestamp
        )
        .containsExactly(
            tuple(
                resourceUserIsResponsibleFor.getId(),
                ResourceType.FIELD,
                resourceNameUserIsResponsibleFor,
                organisationUnitIdUserIsInGroupFor,
                regulatorManagedInformationUserIsResponsibleFor.getResponsibleTeamType(),
                fieldUserIsResponsibleFor.getParentDeterminedField().getCorrectedLegalFieldName(),
                fieldStatusHistoryForResponsibleField.getFieldStatus(),
                fixedInstant
            )
        );
  }

  @Test
  void getFieldRecordsSearchResults_asIndustryInResponsibleOrganisation_assertOnlyResourcesOnExtantLicencesDisplayedAndAssertValues() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpIndustryTest(Set.of(Role.INDUSTRY_SUBMIT), List.of(organisationUnitIdUserIsInGroupFor));

    var resource1 = buildFieldResource();
    buildSubmissionDetail(resource1, SubmissionType.CREATE_CSR, null);
    var resourceName1 = "Field A";
    var regulatorManagedInformation1 = SubmissionIntegrationTestUtils
        .createRegulatorManagedInformation(resource1, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var field1 = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource1, resourceName1, organisationUnitIdUserIsInGroupFor, entityManager);
    var fieldStatusHistoryForField1 = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(field1, FieldStatus.STATUS_300, LocalDate.now(), entityManager);
    createFieldLicence(1, "relevant", field1);

    var resource2 =  buildFieldResource();
    buildSubmissionDetail(resource2, SubmissionType.CREATE_CSR, null);
    var resourceName2 = "The resource name B";
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(resource2,
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA, GeographicalArea.NORTHERN_NORTH_SEA,
        entityManager);
    var field2 = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource2, resourceName2, organisationUnitIdUserIsInGroupFor, entityManager);
    SubmissionIntegrationTestUtils.createFieldStatusHistory(field2, FieldStatus.STATUS_300, LocalDate.now(),
        entityManager);
    // no licence created for second result

    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceId,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::organisationResponsibleId,
            ResourceRecordQueryResult::responsibleTeam,
            ResourceRecordQueryResult::parentFieldName,
            ResourceRecordQueryResult::fieldStatus,
            ResourceRecordQueryResult::archived
        )
        .containsExactly(
            tuple(
                resource1.getId(),
                ResourceType.FIELD,
                resourceName1,
                organisationUnitIdUserIsInGroupFor,
                regulatorManagedInformation1.getResponsibleTeamType(),
                field1.getParentDeterminedField().getCorrectedLegalFieldName(),
                fieldStatusHistoryForField1.getFieldStatus(),
                false
            )
        );
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

    var results = operatorResourceRecordQueryService.getResourceRecordSearchResults(form, USER);
    assertThat(results)
            .extracting(
                    ResourceRecordQueryResult::resourceId,
                    ResourceRecordQueryResult::resourceType,
                    ResourceRecordQueryResult::resourceName,
                    ResourceRecordQueryResult::organisationResponsibleId,
                    ResourceRecordQueryResult::responsibleTeam,
                    ResourceRecordQueryResult::parentFieldName,
                    ResourceRecordQueryResult::fieldStatus,
                    ResourceRecordQueryResult::archived
            )
            .containsExactly(
                    tuple(
                            nonArchivedResource.getId(),
                            ResourceType.FIELD,
                            nonArchivedResourceName,
                            organisationUnitIdUserIsInGroupFor,
                            nonArchivedRegulatorManagedInformation.getResponsibleTeamType(),
                            nonArchivedField.getParentDeterminedField().getCorrectedLegalFieldName(),
                            fieldStatusHistoryForNonArchivedField.getFieldStatus(),
                            nonArchivedResource.getArchived()
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

  private void buildResourceGroupLink(boolean preferredView, Resource resource) {
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setPreferredView(preferredView);
    resourceGroupLink.setResourceGroupId(UUID.randomUUID());
    entityManager.persist(resourceGroupLink);
    entityManager.flush();
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType,
                                                 Instant acceptedTimestamp) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager,
        acceptedTimestamp);
  }
}
