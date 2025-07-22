package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicence;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementService;

@Transactional
@IntegrationTest
class RegulatorResourceRecordQueryServiceIntegrationTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;

  @Autowired
  private EntityManager entityManager;

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @Autowired
  private TeamManagementService teamManagementService;

  @MockBean
  private LicenceQueryService licenceQueryService;

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenOneFullSubmission_assertAllValuesAndOrderIsCorrect() {
    var resource1 = buildProspectResource();
    setUpRegulatorTest();
    var earlierInstant = Instant.parse("2024-12-02T10:15:30.00Z");
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT, earlierInstant);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var licenceApplicationReference1 = "RT9";
    var generalDetails1 = createGeneralDetail(submissionDetail1, licenceApplicationReference1);
    createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(
        generalDetails1,
        quadrantBlock1,
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createProspectAndDiscoveryLicence(951, "AL10", generalDetails1);
    var licence2 = createProspectAndDiscoveryLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    var laterInstant = Instant.parse("2024-12-03T10:15:30.00Z");
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT, laterInstant);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var licenceApplicationReference2 = "RT2";
    var generalDetails2 = createGeneralDetail(submissionDetail2, licenceApplicationReference2);
    createResourceDetail(submissionDetail2, resourceName2, organisationId2);

    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(
        generalDetails2,
        quadrantBlock2,
        "50",
        11,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createProspectAndDiscoveryLicence(253, "BL10", generalDetails2);
    var licence4 = createProspectAndDiscoveryLicence(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(List.of(results.get(0), results.get(1)))
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::centreOfInterestQuadBlock,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::licencesTheResourceIsOn,
            ResourceRecordQueryResult::submissionAcceptedTimestamp,
            ResourceRecordQueryResult::licenceApplicationReference
        )
        .containsExactly(
            tuple(
                submissionDetail2.getId(),
                centreOfInterestQuadrantBlock2.getQuadrantBlockReference(),
                organisationId2,
                resourceName2,
                ResourceType.PROSPECT,
                List.of(licence3.getLicenceId(), licence4.getLicenceId()),
                laterInstant,
                licenceApplicationReference2
            ),
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlockReference(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId()),
                earlierInstant,
                licenceApplicationReference1
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenIncompleteResourceName_assertOrderedResultsAllContainTextIgnoreCasing() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpRegulatorTest();

    var resource1 = buildProspectResource();
    var submission1 =
        buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT, Instant.now());
    var resourceName1 = "Resource";
    var generalDetail1 = createGeneralDetail(submission1, null);
    createResourceDetail(
        submission1, resourceName1, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId1 = 1;
     createProspectAndDiscoveryLicence(extantLicenceId1, "licenceRef", generalDetail1);

    var resource2 = buildDiscoveryResource();
    var submission2 =
        buildSubmissionDetail(resource2, SubmissionType.CREATE_DISCOVERY, Instant.now());
    var resourceName2 = "A resource";
    var generalDetail2 = createGeneralDetail(submission2, null);
    createResourceDetail(
        submission2, resourceName2, organisationUnitIdUserIsInGroupFor);
    var extantLicenceId2 = 1;
    createProspectAndDiscoveryLicence(extantLicenceId2, "licenceRef", generalDetail2);

    var resource3 = buildProspectResource();
    var submission3 =
        buildSubmissionDetail(resource3, SubmissionType.CREATE_DISCOVERY, Instant.now());
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

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceName
        )
        .containsExactly(
            resourceName2, resourceName1
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenTwoSubmissionsForOneResource_assertLatestApprovedIsDisplayed() {
    setUpRegulatorTest();
    var resource1 = buildProspectResource();
    var earlierInstant = Instant.parse("2022-12-03T10:15:30.00Z");
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT, earlierInstant);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1, null);
    createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    var quadrantBlock1 = "25/12";
    createQuadAndBlock(
        generalDetails1,
        quadrantBlock1,
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail1, quadrantBlock1);

    createProspectAndDiscoveryLicence(951, "AL10", generalDetails1);
    createProspectAndDiscoveryLicence(953, "AL6", generalDetails1);

    var laterInstant = Instant.parse("2024-12-03T10:15:30.00Z");
    var submissionDetail2 = buildSubmissionDetail(resource1, SubmissionType.PROSPECT_UPDATE, laterInstant);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2, null);
    createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(
        generalDetails2,
        quadrantBlock2,
        "50",
        11,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createProspectAndDiscoveryLicence(253, "BL10", generalDetails2);
    var licence4 = createProspectAndDiscoveryLicence(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(List.of(results.get(0)))
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::centreOfInterestQuadBlock,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::licencesTheResourceIsOn
        )
        .containsExactly(
            tuple(
                submissionDetail2.getId(),
                centreOfInterestQuadrantBlock2.getQuadrantBlockReference(),
                organisationId2,
                resourceName2,
                ResourceType.PROSPECT,
                List.of(licence3.getLicenceId(), licence4.getLicenceId())
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnResourceName_assertOnlyFilteredDisplayed() {
    var resource1 = buildProspectResource();
    setUpRegulatorTest();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT, Instant.now());
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1, null);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(
        generalDetails1,
        quadrantBlock1,
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createProspectAndDiscoveryLicence(951, "AL10", generalDetails1);
    var licence2 = createProspectAndDiscoveryLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT, Instant.now());
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2, null);
    createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    createQuadAndBlock(
        generalDetails2,
        quadrantBlock2,
        "50",
        11,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail2, quadrantBlock2);

    createProspectAndDiscoveryLicence(253, "BL10", generalDetails2);
    createProspectAndDiscoveryLicence(351, "BL6", generalDetails2);
    createOpportunityDetail(OpportunityType.PROSPECT, submissionDetail1);

    var form = new SearchResourceRecordsForm();
    form.setResourceNameFromResourceDetail(resourceDetails1.getResourceName());
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of(OpportunityType.PROSPECT.name()));
    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::centreOfInterestQuadBlock,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::licencesTheResourceIsOn
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlockReference(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId())
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnResponsibleOrganisation_assertOnlyFilteredDisplayed() {
    setUpRegulatorTest();
    var resource1 = buildProspectResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT, Instant.now());
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1, null);
    createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(
        generalDetails1,
        quadrantBlock1,
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createProspectAndDiscoveryLicence(951, "AL10", generalDetails1);
    var licence2 = createProspectAndDiscoveryLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT, Instant.now());
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2, null);
    createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    createQuadAndBlock(
        generalDetails2,
        quadrantBlock2,
        "50",
        11,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail2, quadrantBlock2);

    createProspectAndDiscoveryLicence(253, "BL10", generalDetails2);
    createProspectAndDiscoveryLicence(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResponsibleOrganisationId(organisationId1);
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::centreOfInterestQuadBlock,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::licencesTheResourceIsOn
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlockReference(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId())
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnResourceType_assertOnlyDiscoveryDisplayed() {
    var resource1 = buildDiscoveryResource();
    setUpRegulatorTest();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_DISCOVERY, Instant.now());
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var generalDetails1 = createGeneralDetail(submissionDetail1, null);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(
        generalDetails1,
        quadrantBlock1,
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createProspectAndDiscoveryLicence(951, "AL10", generalDetails1);
    var licence2 = createProspectAndDiscoveryLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT, Instant.now());

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.DISCOVERY.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));
    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::centreOfInterestQuadBlock,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::licencesTheResourceIsOn
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlockReference(),
                organisationId1,
                resourceName1,
                ResourceType.DISCOVERY,
                List.of(licence1.getLicenceId(), licence2.getLicenceId())
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnResourceType_assertOnlyProspectDisplayed() {
    var resource1 = buildProspectResource();
    setUpRegulatorTest();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT, Instant.now());
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var generalDetails1 = createGeneralDetail(submissionDetail1, null);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(
        generalDetails1,
        quadrantBlock1,
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createProspectAndDiscoveryLicence(951, "AL10", generalDetails1);
    var licence2 = createProspectAndDiscoveryLicence(953, "AL6", generalDetails1);

    var resource2 = buildDiscoveryResource();
    buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT, Instant.now());
    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::centreOfInterestQuadBlock,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::licencesTheResourceIsOn
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlockReference(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId())
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnResourceType_assertAllResourceTypesDisplayed() {
    setUpRegulatorTest();
    var discoveryResource = buildDiscoveryResource();

    var discoverySubmission = buildSubmissionDetail(discoveryResource, SubmissionType.CREATE_DISCOVERY, Instant.now());
    createResourceDetail(
        discoverySubmission, "NOT_RELEVANT_NAME", 1);
    createGeneralDetail(discoverySubmission, null);

    var prospectResource = buildProspectResource();

    var prospectSubmission = buildSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT, Instant.now());
    createResourceDetail(
        prospectSubmission, "NOT_RELEVANT_NAME", 2);

    createGeneralDetail(prospectSubmission, null);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name(), ResourceType.DISCOVERY.name()));

    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceType
        )
        .containsExactly(
            tuple(
                prospectSubmission.getId(),
                ResourceType.PROSPECT
            ),
            tuple(
                discoverySubmission.getId(),
                ResourceType.DISCOVERY
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnQuadAndBlock_assertOnlyFilteredDisplayed() {
    setUpRegulatorTest();
    var resourceMatchingFilter = buildProspectResource();
    var submissionMatchingFilter = buildSubmissionDetail(resourceMatchingFilter, SubmissionType.CREATE_PROSPECT, Instant.now());
    var resourceNameForMatchingFilter = "The resource name A";
    var generalDetailsMatchingFilter = createGeneralDetail(submissionMatchingFilter, null);
    createResourceDetail(submissionMatchingFilter, resourceNameForMatchingFilter, 1);
    var quadrantBlockMatchingFilter = "25/12";
    var centreOfInterestQuadrantBlockMatchingFilter =
        createQuadAndBlock(
            generalDetailsMatchingFilter,
            quadrantBlockMatchingFilter,
            "25",
            12,
            OnshoreOffshore.OFFSHORE
        );
    createTrapProperty(submissionMatchingFilter, quadrantBlockMatchingFilter);

    var resourceNotMatchingFilter = buildProspectResource();
    var submissionNotMatchingFilter = buildSubmissionDetail(resourceNotMatchingFilter, SubmissionType.CREATE_PROSPECT,
        Instant.now());
    var generalDetailsNotMatchingFilter = createGeneralDetail(submissionNotMatchingFilter, null);
    createResourceDetail(submissionNotMatchingFilter, "NOT_RELEVANT", 1);
    var quadrantBlockNotMatchingFilter = "50/11";
    createQuadAndBlock(
            generalDetailsNotMatchingFilter,
            quadrantBlockNotMatchingFilter,
            "50",
            11,
            OnshoreOffshore.OFFSHORE
        );
    createTrapProperty(submissionNotMatchingFilter, quadrantBlockNotMatchingFilter);

    var form = new SearchResourceRecordsForm();
    form.setCentreOfInterestBlock(quadrantBlockMatchingFilter);
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::centreOfInterestQuadBlock,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::resourceType
        )
        .containsExactly(
            tuple(
                submissionMatchingFilter.getId(),
                centreOfInterestQuadrantBlockMatchingFilter.getQuadrantBlockReference(),
                resourceNameForMatchingFilter,
                ResourceType.PROSPECT
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnLicence_assertOnlyFilteredDisplayed() {
    setUpRegulatorTest();
    var resourceWithWrongLicences = buildProspectResource();
    var submissionWithWrongLicences = buildSubmissionDetail(resourceWithWrongLicences, SubmissionType.CREATE_PROSPECT,
        Instant.now());
    var generalDetailsWithWrongLicences = createGeneralDetail(submissionWithWrongLicences, null);
    createResourceDetail(submissionWithWrongLicences, "NOT_RELEVANT_NAME", 46);
    createProspectAndDiscoveryLicence(951, "AL10", generalDetailsWithWrongLicences);

    var resourceRightLicence = buildProspectResource();
    var submissionWithRightLicence = buildSubmissionDetail(resourceRightLicence, SubmissionType.CREATE_PROSPECT, Instant.now());
    var generalDetailWithRightLicence = createGeneralDetail(submissionWithRightLicence, null);
    createResourceDetail(submissionWithRightLicence, "NOT_RELEVANT_NAME", 1);

    var licenceWithNotMatchingId = createProspectAndDiscoveryLicence(253, "BL10", generalDetailWithRightLicence);
    var licenceMatchingFilter = createProspectAndDiscoveryLicence(351, "BL6", generalDetailWithRightLicence);

    var form = new SearchResourceRecordsForm();
    form.setLicenceId("351");
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::licencesTheResourceIsOn
        )
        .containsExactly(
            tuple(
                submissionWithRightLicence.getId(),
                ResourceType.PROSPECT,
                List.of(licenceWithNotMatchingId.getLicenceId(), licenceMatchingFilter.getLicenceId())
            )
        );
  }

  @ParameterizedTest
  @EnumSource(OpportunityType.class)
  void getProspectAndDiscoveryRecordSearchResults_whenFilteringOnOpportunityType_assertOnlyFilteredDisplayed(
      OpportunityType opportunityType) {
    setUpRegulatorTest();
    var resourceWithNotMatchingOpportunityType = buildProspectResource();
    var submissionWithNotMatchingOpportunityType =
        buildSubmissionDetail(resourceWithNotMatchingOpportunityType, SubmissionType.CREATE_PROSPECT, Instant.now());

    createGeneralDetail(submissionWithNotMatchingOpportunityType, null);
    createResourceDetail(submissionWithNotMatchingOpportunityType, "NOT_RELEVANT_NAME", 1);

    var resourceWithMatchingOpportunityType = buildProspectResource();
    var submissionWithMatchingOpportunityType =
        buildSubmissionDetail(resourceWithMatchingOpportunityType, SubmissionType.CREATE_PROSPECT, Instant.now());
    createGeneralDetail(submissionWithMatchingOpportunityType, null);
    createResourceDetail(submissionWithMatchingOpportunityType, "NOT_RELEVANT_NAME", 2);

    createOpportunityDetail(opportunityType, submissionWithMatchingOpportunityType);
    createOpportunityDetail(null, submissionWithNotMatchingOpportunityType);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of(opportunityType.name()));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceType)
        .containsExactly(
            tuple(
                submissionWithMatchingOpportunityType.getId(),
                ResourceType.PROSPECT
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenNoFilter_asRegulator_assertAllDisplayed() {
    setUpRegulatorTest();
    var discoveryWithDiscoveryOpportunityType = buildDiscoveryResource();
    var submissionWithDiscoveryOpportunityType =
        buildSubmissionDetail(discoveryWithDiscoveryOpportunityType, SubmissionType.CREATE_DISCOVERY, Instant.now());

    createGeneralDetail(submissionWithDiscoveryOpportunityType, null);
    createResourceDetail(submissionWithDiscoveryOpportunityType, "NOT_RELEVANT_NAME", 1);

    var prospectWithLeadOpportunityType = buildProspectResource();
    var submissionWithLeadOpportunityType =
        buildSubmissionDetail(prospectWithLeadOpportunityType, SubmissionType.CREATE_PROSPECT, Instant.now());
    createGeneralDetail(submissionWithLeadOpportunityType, null);
    createResourceDetail(submissionWithLeadOpportunityType, "NOT_RELEVANT_NAME", 2);

    createOpportunityDetail(OpportunityType.LEAD, submissionWithLeadOpportunityType);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionWithDiscoveryOpportunityType);

    var form = new SearchResourceRecordsForm();

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceType)
        .containsExactly(
            tuple(
                submissionWithLeadOpportunityType.getId(),
                ResourceType.PROSPECT
            ),
            tuple(
                submissionWithDiscoveryOpportunityType.getId(),
                ResourceType.DISCOVERY
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenMatchesFilters_andHasLinkedDiscovery_assertNoResults() {
    setUpRegulatorTest();
    var resourceMatchingFiltersWithLinkedResource = buildProspectResource();
    var submissionMatchingFiltersWithLinkedResource = buildSubmissionDetail(resourceMatchingFiltersWithLinkedResource, SubmissionType.CREATE_PROSPECT,
        null);
    createResourceDetail(submissionMatchingFiltersWithLinkedResource, "NOT_RELEVANT_NAME", 2);

    var otherResourceSubmissionMatchingFiltersWithLinkedResource = buildProspectResource();
    var otherSubmissionMatchingFiltersWithLinkedResource = buildSubmissionDetail(
        otherResourceSubmissionMatchingFiltersWithLinkedResource, SubmissionType.CREATE_PROSPECT, Instant.now());
    createResourceDetail(otherSubmissionMatchingFiltersWithLinkedResource, "NOT_RELEVANT_NAME", 3);

    var linkedDiscovery = buildDiscoveryResource();

    createResourceLink(otherResourceSubmissionMatchingFiltersWithLinkedResource, linkedDiscovery);
    createResourceLink(resourceMatchingFiltersWithLinkedResource, linkedDiscovery);

    createOpportunityDetail(OpportunityType.PROSPECT, otherSubmissionMatchingFiltersWithLinkedResource);
    createOpportunityDetail(OpportunityType.PROSPECT, submissionMatchingFiltersWithLinkedResource);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("PROSPECT"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .isEmpty();
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenTwoMatchesFilters_andOneHasLinkedDiscovery_assertOneResult() {
    setUpRegulatorTest();
    var prospectResourceWithNoLink = buildProspectResource();
    var submissionWithNoLinkedDiscovery = buildSubmissionDetail(
        prospectResourceWithNoLink, SubmissionType.CREATE_PROSPECT, Instant.now());
    createResourceDetail(submissionWithNoLinkedDiscovery, "NOT_RELEVANT_NAME", 1);
    var prospectWithLinkedDiscovery = buildProspectResource();
    var submissionWithLinkedDiscovery =
        buildSubmissionDetail(prospectWithLinkedDiscovery, SubmissionType.CREATE_PROSPECT, Instant.now());
    createResourceDetail(submissionWithLinkedDiscovery, "NOT_RELEVANT_NAME", 2);

    var linkedDiscoveryResource = buildDiscoveryResource();

    createResourceLink(prospectWithLinkedDiscovery, linkedDiscoveryResource);

    createOpportunityDetail(OpportunityType.PROSPECT, submissionWithLinkedDiscovery);
    createOpportunityDetail(OpportunityType.PROSPECT, submissionWithNoLinkedDiscovery);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("PROSPECT"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceType
        )
        .containsExactly(
            tuple(
                submissionWithNoLinkedDiscovery.getId(),
                ResourceType.PROSPECT
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenOneDiscoveryMatchesFilters_andHasLinkedProspect_assertResult() {
    setUpRegulatorTest();
    var discoveryWithLinkedProspect = buildDiscoveryResource();
    var submissionWithLinkedProspect = buildSubmissionDetail(discoveryWithLinkedProspect,
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT, Instant.now());
    createResourceDetail(submissionWithLinkedProspect, "NOT_RELEVANT_NAME", 1);

    var linkedProspectResource = buildProspectResource();
    createResourceLink(linkedProspectResource, discoveryWithLinkedProspect);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionWithLinkedProspect);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.DISCOVERY.name()));
    form.setOpportunityTypes(List.of("DISCOVERY"));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceType
        )
        .containsExactly(
            tuple(
                submissionWithLinkedProspect.getId(),
                ResourceType.DISCOVERY
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilterOnLicenceRound_whenPartialMatch_assertResultReturned() {
    setUpRegulatorTest();
    var resourceWithMatchingLicenceApplication  = buildDiscoveryResource();
    var resourceNotMatchingLicenceApplication = buildDiscoveryResource();
    var submissionMatchingFilter = buildSubmissionDetail(resourceWithMatchingLicenceApplication ,
        SubmissionType.CREATE_DISCOVERY, Instant.now());
    var submissionNotMatchingFilter = buildSubmissionDetail(resourceNotMatchingLicenceApplication,
        SubmissionType.CREATE_DISCOVERY, Instant.now());
    var matchingFilterLicenceRound = "LICENCE_ROUND_9000";
    var matchingFilterGeneralDetail = createGeneralDetail(submissionMatchingFilter, null);
    var notMatchingFilterRound = "COOL_LICENCE";
    var notMatchingFilterGeneralDetail = createGeneralDetail(submissionNotMatchingFilter, null);
    notMatchingFilterGeneralDetail.setLicenceApplicationReference(notMatchingFilterRound);
    matchingFilterGeneralDetail.setLicenceApplicationReference(matchingFilterLicenceRound);

    createResourceDetail(submissionNotMatchingFilter, "not matching filter", 1);
    createResourceDetail(submissionMatchingFilter, "matching filter", 1);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionMatchingFilter);
    createOpportunityDetail(OpportunityType.PROSPECT, submissionNotMatchingFilter);

    var form = new SearchResourceRecordsForm();
    form.setLicenceRound("9000");

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceId,
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceName
        )
        .containsExactly(
            tuple(
                resourceWithMatchingLicenceApplication.getId(),
                submissionMatchingFilter.getId(),
                "matching filter")
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilterOnLicenceRound_whenNoMatch_assertNoResult() {
    setUpRegulatorTest();
    var resourceNotMatchingLicenceApplication = buildDiscoveryResource();
    var submissionNotMatchingLicenceApplication = buildSubmissionDetail(resourceNotMatchingLicenceApplication,
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT, Instant.now());
    var notMatchingFilterLicenceApplication = "LICENCE_ROUND_9000";
    createResourceDetail(submissionNotMatchingLicenceApplication, "NOT_RELEVANT", 1);
    var generalDetailNotMatchingLicenceApp = createGeneralDetail(submissionNotMatchingLicenceApplication, null);
    generalDetailNotMatchingLicenceApp.setLicenceApplicationReference(notMatchingFilterLicenceApplication);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionNotMatchingLicenceApplication);

    var form = new SearchResourceRecordsForm();
    form.setLicenceRound("cool");

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {"FIRST", "SECOND"})
  void getResourceSearchResults_filterOnLicenceRound_whenLicenceRoundOnOlderSubmissionOrCurrent_assertReturnsLatest(
      String licenceRoundFilter) {
    setUpRegulatorTest();

    var resourceWithTwoLicenceRounds = buildProspectResource();
    var earlierInstant = Instant.parse("2024-12-02T10:15:30.00Z");
    var firstSubmission = buildSubmissionDetail(resourceWithTwoLicenceRounds,
        SubmissionType.CREATE_PROSPECT, earlierInstant);
    var laterInstant = Instant.parse("2024-12-03T10:15:30.00Z");
    var latestSubmission = buildSubmissionDetail(resourceWithTwoLicenceRounds,
        SubmissionType.PROSPECT_UPDATE, laterInstant);

    //ensures the test is deterministic, latest submission is always later than first submission
    latestSubmission.setAcceptedTimestamp(Instant.now().plus(500000, ChronoUnit.MILLIS));
    entityManager.persist(latestSubmission);
    entityManager.flush();

    var firstLicencelicenceApplicationReference = "FIRST_LICENCE";
    var latestLicencelicenceApplicationReference = "SECOND_LICENCE";
    createResourceDetail(firstSubmission, "not_relevant_name", 1);
    createResourceDetail(latestSubmission, "not_relevant_name", 1);
    var firstSubmissionGeneralDetail = createGeneralDetail(firstSubmission, null);
    firstSubmissionGeneralDetail.setLicenceApplicationReference(firstLicencelicenceApplicationReference);
    entityManager.persist(firstSubmissionGeneralDetail);
    entityManager.flush();
    var latestGeneralDetail = createGeneralDetail(latestSubmission, null);
    latestGeneralDetail.setLicenceApplicationReference(latestLicencelicenceApplicationReference);
    entityManager.persist(latestGeneralDetail);
    entityManager.flush();
    var form = new SearchResourceRecordsForm();
    form.setLicenceRound(licenceRoundFilter);

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::resourceType
        )
        .containsExactly(
            tuple(
                latestSubmission.getId(),
                ResourceType.PROSPECT
            )
        );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenFilterOnLicenceRound_whenLicenceRoundOnNewerDraftSubmission_assertNoResult() {
    setUpRegulatorTest();
    var resourceWithLicenceApplications = buildProspectResource();
    var earlierInstant = Instant.parse("2024-12-03T10:15:30.00Z");
    var approvedSubmission = buildSubmissionDetail(resourceWithLicenceApplications,
        SubmissionType.CREATE_PROSPECT, earlierInstant);
    var laterInstant = Instant.parse("2024-12-03T10:15:30.00Z");
    var draftSubmission = buildSubmissionDetail(resourceWithLicenceApplications,
        SubmissionType.PROSPECT_UPDATE, laterInstant);
    draftSubmission.setStatus(SubmissionDetailStatus.DRAFT);
    var approvedLicencelicenceApplicationReference = "FIRST_LICENCE";
    var draftLicencelicenceApplicationReferenceMatchingFilter = "SECOND_LICENCE";
    createResourceDetail(approvedSubmission, "not_relevant", 1);
    createResourceDetail(draftSubmission, "not_relevant", 1);
    var approvedGeneralDetail = createGeneralDetail(approvedSubmission, null);
    approvedGeneralDetail.setLicenceApplicationReference(approvedLicencelicenceApplicationReference);
    var draftGeneralDetail = createGeneralDetail(draftSubmission, null);
    draftGeneralDetail.setLicenceApplicationReference(draftLicencelicenceApplicationReferenceMatchingFilter);

    var form = new SearchResourceRecordsForm();
    form.setLicenceRound("SECOND");

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .isEmpty();
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenRegulator_andNotOnAlicenceAndNotExtant_assertAllDisplayed() {
    setUpRegulatorTest();

    var resourceWithoutLicence = buildProspectResource();
    var submissionWithoutExtantLicence =
        buildSubmissionDetail(resourceWithoutLicence, SubmissionType.CREATE_PROSPECT, Instant.now());
    createGeneralDetail(submissionWithoutExtantLicence, null);
    createResourceDetail(
        submissionWithoutExtantLicence, "Not relevant name", 1);

    var resourceWithNotExtantLicence = buildProspectResource();
    var submissionWithNotExtantLicence =
        buildSubmissionDetail(resourceWithNotExtantLicence, SubmissionType.CREATE_PROSPECT, Instant.now());
    var nonExtantLicenceId = 2;
    var generalDetailWithNonExtantLicence = createGeneralDetail(submissionWithNotExtantLicence, null);
    createResourceDetail(
        submissionWithNotExtantLicence, "Not relevant name", 2);
    var notExtantLicence = createProspectAndDiscoveryLicence(nonExtantLicenceId, "licenceRef", generalDetailWithNonExtantLicence);
    when(licenceQueryService.getExtantLicencesByIds(List.of(notExtantLicence.getLicenceId().toString())))
        .thenReturn(List.of());
    var form = new SearchResourceRecordsForm();

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId)
        .containsExactly(
            tuple(
                submissionWithNotExtantLicence.getId(),
                2
            ),
            tuple(
                submissionWithoutExtantLicence.getId(),
                1
            )
        );
  }

  @ParameterizedTest
  @EnumSource(GeographicalArea.class)
  void getProspectAndDiscoveryRecordSearchResults_andFilterByGeographicalArea_assertOnlyTypeSpecifiedDisplayed(
      GeographicalArea geographicalArea) {
    setUpRegulatorTest();

    var resourceWithGeographicalArea = buildProspectResource();
    var submissionWithGeographicalArea =
        buildSubmissionDetail(resourceWithGeographicalArea, SubmissionType.CREATE_PROSPECT, Instant.now());
    buildRegulatorManagedInformation(resourceWithGeographicalArea, geographicalArea);
    createGeneralDetail(submissionWithGeographicalArea, null);
    createResourceDetail(
        submissionWithGeographicalArea, "Not relevant name", 1);

    var resourceWithOnshoreGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithOnshoreGeographicalArea, GeographicalArea.ONSHORE);
    var submissionWithOnshoreGeographicalArea =
        buildSubmissionDetail(resourceWithOnshoreGeographicalArea, SubmissionType.CREATE_PROSPECT, Instant.now());
    createGeneralDetail(submissionWithOnshoreGeographicalArea, null);
    createResourceDetail(
        submissionWithOnshoreGeographicalArea, "Not relevant name", 2);

    var resourceWithNoGeographicalArea = buildProspectResource();
    buildRegulatorManagedInformation(resourceWithOnshoreGeographicalArea, null);
    var submissionWithNosGeographicalArea =
        buildSubmissionDetail(resourceWithNoGeographicalArea, SubmissionType.CREATE_PROSPECT, Instant.now());
    createGeneralDetail(submissionWithNosGeographicalArea, null);
    createResourceDetail(
        submissionWithNosGeographicalArea, "Not relevant name", 2);

    var form = new SearchResourceRecordsForm();
    form.setGeographicalArea(List.of(geographicalArea.name()));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);
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

  @Test
  void getProspectAndDiscoveryRecordSearchResults_whenSubmittedUpdatesPresent_assertReturnedLatestApproved() {
    setUpRegulatorTest();
    var prospect = buildProspectResource();
    var submission = SubmissionIntegrationTestUtils.createSubmission(prospect, entityManager);

    var initialInstant = Instant.parse("2024-12-01T10:15:30.00Z");
    var submissionDetailInitial =
        buildSubmissionDetail(prospect, SubmissionType.CREATE_PROSPECT, initialInstant);
    submissionDetailInitial.setVersionNumber(1);
    submissionDetailInitial.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetailInitial.setSubmission(submission);
    createGeneralDetail(submissionDetailInitial, null);
    createResourceDetail(
        submissionDetailInitial, "Not relevant name", 1);
    entityManager.persist(submissionDetailInitial);
    entityManager.flush();

    var updateInstant = Instant.parse("2024-12-02T10:15:30.00Z");
    var submissionDetailSubmittedUpdate =
        buildSubmissionDetail(prospect, SubmissionType.CREATE_PROSPECT, updateInstant);
    submissionDetailSubmittedUpdate.setVersionNumber(2);
    submissionDetailSubmittedUpdate.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetailSubmittedUpdate.setSubmission(submission);
    entityManager.persist(submissionDetailSubmittedUpdate);
    entityManager.flush();
    createGeneralDetail(submissionDetailSubmittedUpdate, null);
    createResourceDetail(
        submissionDetailSubmittedUpdate, "Not relevant name", 1);

    var latestApprovedInstant = Instant.parse("2024-12-03T10:15:30.00Z");
    var submissionDetailApprovedUpdate =
        buildSubmissionDetail(prospect, SubmissionType.CREATE_PROSPECT, latestApprovedInstant);
    submissionDetailApprovedUpdate.setVersionNumber(3);
    submissionDetailApprovedUpdate.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetailApprovedUpdate.setSubmission(submission);
    entityManager.persist(submissionDetailApprovedUpdate);
    entityManager.flush();
    createGeneralDetail(submissionDetailApprovedUpdate, null);
    createResourceDetail(
        submissionDetailApprovedUpdate, "Not relevant name", 1);

    var draftInstant = Instant.parse("2024-12-03T10:15:30.00Z");
    var deletedDraftUpdate =
        buildSubmissionDetail(prospect, SubmissionType.CREATE_PROSPECT, draftInstant);
    deletedDraftUpdate.setVersionNumber(4);
    deletedDraftUpdate.setStatus(SubmissionDetailStatus.DELETED);
    deletedDraftUpdate.setSubmission(submission);
    entityManager.persist(deletedDraftUpdate);
    entityManager.flush();
    createGeneralDetail(deletedDraftUpdate, null);
    createResourceDetail(
        deletedDraftUpdate, "Not relevant name", 1);

    var form = new SearchResourceRecordsForm();

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::submissionDetailId)
        .containsExactly(
            submissionDetailApprovedUpdate.getId()
        );
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getProspectAndDiscoveryRecordSearchResults_whenSubmissionWithdrawn_assertOnlyNonWithdrawnDisplayed(
      Boolean withdrawn) {
    setUpRegulatorTest();
    if(!withdrawn) {
      withdrawn = null;
    }
    var resourceWithWithdrawnSubmission = buildDiscoveryResource();
    var submissionDetailWithWithdrawnSubmission =
        buildSubmissionDetail(resourceWithWithdrawnSubmission, SubmissionType.CREATE_DISCOVERY, Instant.now());
    var withdrawnSubmission =
        SubmissionIntegrationTestUtils.createSubmission(resourceWithWithdrawnSubmission, withdrawn, entityManager);
    submissionDetailWithWithdrawnSubmission.setSubmission(withdrawnSubmission);
    entityManager.persist(submissionDetailWithWithdrawnSubmission);
    entityManager.flush();
    createResourceDetail(
        submissionDetailWithWithdrawnSubmission, "Not relevant name", 1);
    var generalDetail = createGeneralDetail(submissionDetailWithWithdrawnSubmission, null);
    var extantLicence = createProspectAndDiscoveryLicence(1, "licenceRef", generalDetail);
    when(licenceQueryService.getExtantLicencesByIds(List.of(extantLicence.getLicenceId().toString())))
        .thenReturn(List.of(new LicenceDto("licenceRef", 1, LicenceStatus.EXTANT)));

    var form = new SearchResourceRecordsForm();

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

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
  void getProspectAndDiscoveryRecordSearchResults_andNotFilterByArchived_assertArchivedNotDisplayed() {
    setUpRegulatorTest();

    var archivedResource = buildArchivedProspectResource();
    var archivedSubmissionDetail =
            buildSubmissionDetail(archivedResource, SubmissionType.CREATE_PROSPECT, Instant.now());
    createGeneralDetail(archivedSubmissionDetail, null);
    buildResourceGroupLink(archivedResource);

    createResourceDetail(archivedSubmissionDetail, "Not relevant name", 1);

    var nonArchivedResource = buildProspectResource();
    var nonArchivedSubmissionDetail =
            buildSubmissionDetail(nonArchivedResource, SubmissionType.CREATE_PROSPECT, Instant.now());
    buildResourceGroupLink(nonArchivedResource);
    createGeneralDetail(nonArchivedSubmissionDetail, null);
    createResourceDetail(nonArchivedSubmissionDetail, "Not relevant name", 2);

    var form = new SearchResourceRecordsForm();
    form.setShowArchivedResources(false);

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::submissionDetailId)
          .containsExactly(
              nonArchivedSubmissionDetail.getId()
          );
  }

  @Test
  void getProspectAndDiscoveryRecordSearchResults_andFilterByArchived_assertArchivedDisplayed() {
    setUpRegulatorTest();

    var archivedResource = buildArchivedProspectResource();
    var archivedSubmissionDetail =
            buildSubmissionDetail(archivedResource, SubmissionType.CREATE_PROSPECT, Instant.now());
    createGeneralDetail(archivedSubmissionDetail, null);
    buildResourceGroupLink(archivedResource);

    createResourceDetail(archivedSubmissionDetail, "Not relevant name", 1);

    var nonArchivedResource = buildProspectResource();
    var nonArchivedSubmissionDetail =
            buildSubmissionDetail(nonArchivedResource, SubmissionType.CREATE_PROSPECT, Instant.now());
    buildResourceGroupLink(nonArchivedResource);
    createGeneralDetail(nonArchivedSubmissionDetail, null);
    createResourceDetail(nonArchivedSubmissionDetail, "Not relevant name", 2);

    var form = new SearchResourceRecordsForm();
    form.setShowArchivedResources(true);

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);
      assertThat(results)
          .extracting(
              ResourceRecordQueryResult::submissionDetailId)
          .containsExactlyInAnyOrder(
              archivedSubmissionDetail.getId(),
              nonArchivedSubmissionDetail.getId()
          );
  }

  @Test
  void getFieldRecordsSearchResults_asRegulator_assertCanSeeAllTeamsResourcesDisplayedAndAssertValues() {
    setUpRegulatorTest();

    var responsibleOrgId1 = 45;
    var resource1 = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
    buildSubmissionDetail(resource1, SubmissionType.CREATE_CSR, Instant.now());
    var resourceName1 = "Field A";
    var regulatorManagedInformation1 = SubmissionIntegrationTestUtils
        .createRegulatorManagedInformation(resource1, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var field1 = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource1, resourceName1, responsibleOrgId1, entityManager);
    var fieldStatusHistoryForField1 = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(field1, FieldStatus.STATUS_300, LocalDate.now(), entityManager);
    createFieldLicence(1, "relevant", field1);

    var responsibleOrgId2 = 46;
    var resource2 =  SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
    buildSubmissionDetail(resource2, SubmissionType.CREATE_CSR, Instant.now());
    var resourceName2 = "Field B";
    var regulatorManagedInformation2 = SubmissionIntegrationTestUtils
        .createRegulatorManagedInformation(resource2, ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var field2 = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource2, resourceName2, responsibleOrgId2, entityManager);
    var fieldStatusHistoryForField2 = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(field2, FieldStatus.STATUS_300, LocalDate.now(), entityManager);
    // no licence created for second result

    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceId,
            ResourceRecordQueryResult::resourceType,
            ResourceRecordQueryResult::resourceName,
            ResourceRecordQueryResult::prospectOrDiscoveryOrganisationId,
            ResourceRecordQueryResult::responsibleTeam,
            ResourceRecordQueryResult::parentFieldName,
            ResourceRecordQueryResult::fieldStatus
        )
        .contains(
            tuple(
                resource1.getId(),
                ResourceType.FIELD,
                resourceName1,
                null,
                regulatorManagedInformation1.getResponsibleTeamType(),
                field1.getParentDeterminedField().getCorrectedLegalFieldName(),
                fieldStatusHistoryForField1.getFieldStatus()
            ),
            tuple(
                resource2.getId(),
                ResourceType.FIELD,
                resourceName2,
                null,
                regulatorManagedInformation2.getResponsibleTeamType(),
                field2.getParentDeterminedField().getCorrectedLegalFieldName(),
                fieldStatusHistoryForField2.getFieldStatus()
            )
        );
  }
  @Test
  void getFieldRecordsSearchResults_whenNoSubmission_assertResultsReturned() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpRegulatorTest();

    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(resource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var field = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource, "Field name", organisationUnitIdUserIsInGroupFor, entityManager);
    SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, LocalDate.now(),
        entityManager);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));
    form.setFieldNameForResourceSubmission("Field name");

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::resourceId
        )
        .contains(
            resource.getId()
        );
  }

  @Test
  void getFieldRecordsSearchResults_whenIncompleteFieldName_assertResultsAllContainText() {
    var organisationUnitIdUserIsInGroupFor = 46;
    setUpRegulatorTest();

    Stream.of("JH Field", "jh Field 2", "JH Field With More Text", "No sub string")
        .forEach(fieldName -> buildFieldResultWithFieldName(fieldName, organisationUnitIdUserIsInGroupFor));

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));
    form.setFieldNameForResourceSubmission("JH Fiel");

    var results = regulatorResourceRecordQueryService.getResourceRecordSearchResults(form);

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
    var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
    var submission =
        buildSubmissionDetail(resource, SubmissionType.CREATE_CSR, Instant.now());
    var generalDetail = createGeneralDetail(submission, null);
    SubmissionIntegrationTestUtils.createRegulatorManagedInformation(resource,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, GeographicalArea.NORTHERN_NORTH_SEA, entityManager);
    var field = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource, fieldName, organisationUnitIdUserIsInGroupFor, entityManager);
    SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, LocalDate.now(),
        entityManager);
    createProspectAndDiscoveryLicence(1, "relevant", generalDetail);
    when(licenceQueryService.getExtantLicencesByIds(any()))
        .thenReturn(List.of(LicenceViewTestUtil.Builder().build()));
  }

  private ResourceLink createResourceLink(Resource prospect, Resource discovery) {
    var resourceLink = new ResourceLink();
    resourceLink.setDiscoveryResource(discovery);
    resourceLink.setProspectResource(prospect);

    entityManager.persist(resourceLink);
    entityManager.flush();

    return resourceLink;
  }

  private Licence createProspectAndDiscoveryLicence(int id, String ref, GeneralDetail generalDetails) {

    return SubmissionIntegrationTestUtils.createLicence(id, ref, generalDetails, entityManager);
  }

  private FieldLicence createFieldLicence(int id, String ref, Field field) {

    return FieldIntegrationTestUtils.createLicence(id, ref, field, entityManager);
  }

  private OpportunityDetail createOpportunityDetail(OpportunityType opportunityType, SubmissionDetail submissionDetail) {
    var opportunityDetail = new OpportunityDetail();
    opportunityDetail.setOpportunityType(opportunityType);
    opportunityDetail.setSubmissionDetail(submissionDetail);

    entityManager.persist(opportunityDetail);
    entityManager.flush();

    return opportunityDetail;
  }

  private TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock) {

    return SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail, centreOfInterestQuadrantBlock, entityManager);
  }

  private QuadrantAndBlock createQuadAndBlock(
      GeneralDetail generalDetails,
      String quadrantBlockReference,
      String quad,
      Integer block,
      OnshoreOffshore onshoreOffshore
      ) {

    return SubmissionIntegrationTestUtils.createQuadAndBlock(
        generalDetails,
        quadrantBlockReference,
        quad,
        block,
        onshoreOffshore,
        entityManager);
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail, String licenceApplicationReference) {

    return SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager, licenceApplicationReference);
  }

  private ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId) {

    return SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, resourceName, organisationId, entityManager);
  }

  private Resource buildProspectResource() {

    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
  }

  private Resource buildArchivedProspectResource() {

    return SubmissionIntegrationTestUtils.createArchivedResource(entityManager, ResourceType.PROSPECT);
  }

  private RegulatorManagedInformation buildRegulatorManagedInformation(
      Resource resource, GeographicalArea geographicalArea) {
    return SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        resource, ResponsibleTeamType.EXPLORATION, geographicalArea, entityManager);
  }

  private ResourceGroupLink buildResourceGroupLink(Resource resource) {
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setResourceGroupId(UUID.randomUUID());
    entityManager.persist(resourceGroupLink);
    entityManager.flush();
    return resourceGroupLink;
  }

  private Resource buildDiscoveryResource() {

    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType,
                                                 Instant acceptedTimestamp) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);

    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager,
        acceptedTimestamp);
  }

  private void setUpRegulatorTest() {
    var user = UserTestUtil.Builder()
        .withWuaId(USER.wuaId())
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