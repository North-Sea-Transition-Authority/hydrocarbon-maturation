package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.jooq.DSLContext;
import org.jooq.conf.RenderMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
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
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Transactional
@IntegrationTest
class ResourceRecordQueryServiceIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private ResourceRecordQueryService resourceRecordQueryService;

  @Autowired
  private TestEntityManager entityManager;

  @MockBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private LicenceQueryService licenceQueryService;


  @BeforeEach
  void setUp() {
    dslContext.settings().setRenderMapping(new RenderMapping().withDefaultSchema("remi"));
  }

  @Test
  void getResourceRecordSearchResults_whenOneFullSubmission_assertAllValuesAndOrderIsCorrect() {
    var resource1 = buildProspectResource();
    whenIsRegulatorTrue();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createLicence(951, "AL10", generalDetails1);
    var licence2 = createLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);

    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createLicence(253, "BL10", generalDetails2);
    var licence4 = createLicence(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(List.of(results.get(0), results.get(1)))
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            ResourceRecordQueryResult::getResourceName,
            ResourceRecordQueryResult::getResourceType,
            ResourceRecordQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionDetail2.getId(),
                centreOfInterestQuadrantBlock2.getQuadrantBlock(),
                organisationId2,
                resourceName2,
                ResourceType.PROSPECT,
                List.of(licence3.getLicenceId(), licence4.getLicenceId()),
                submissionDetail2.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail2.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            ),
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlock(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenTwoSubmissionsForOneResource_assertLatestApprovedIsDisplayed() {
    whenIsRegulatorTrue();
    var resource1 = buildProspectResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createLicence(951, "AL10", generalDetails1);
    var licence2 = createLicence(953, "AL6", generalDetails1);

    var submissionDetail2 = buildSubmissionDetail(resource1, SubmissionType.PROSPECT_UPDATE);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createLicence(253, "BL10", generalDetails2);
    var licence4 = createLicence(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(List.of(results.get(0)))
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            ResourceRecordQueryResult::getResourceName,
            ResourceRecordQueryResult::getResourceType,
            ResourceRecordQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionDetail2.getId(),
                centreOfInterestQuadrantBlock2.getQuadrantBlock(),
                organisationId2,
                resourceName2,
                ResourceType.PROSPECT,
                List.of(licence3.getLicenceId(), licence4.getLicenceId()),
                submissionDetail2.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail2.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceName_assertOnlyFilteredDisplayed() {
    var resource1 = buildProspectResource();
    whenIsRegulatorTrue();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createLicence(951, "AL10", generalDetails1);
    var licence2 = createLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetail2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createLicence(253, "BL10", generalDetails2);
    var licence4 = createLicence(351, "BL6", generalDetails2);
    var opportunityDetail = createOpportunityDetail(OpportunityType.PROSPECT, submissionDetail1);

    var form = new SearchResourceRecordsForm();
    form.setResourceDetailIdForResourceSubmission(resourceDetails1.getId().toString());
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of(OpportunityType.PROSPECT.name()));
    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            ResourceRecordQueryResult::getResourceName,
            ResourceRecordQueryResult::getResourceType,
            ResourceRecordQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlock(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResponsibleOrganisation_assertOnlyFilteredDisplayed() {
    whenIsRegulatorTrue();
    var resource1 = buildProspectResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createLicence(951, "AL10", generalDetails1);
    var licence2 = createLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createLicence(253, "BL10", generalDetails2);
    var licence4 = createLicence(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResponsibleOrganisationId(organisationId1);
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            ResourceRecordQueryResult::getResourceName,
            ResourceRecordQueryResult::getResourceType,
            ResourceRecordQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlock(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceType_assertOnlyDiscoveryDisplayed() {
    var resource1 = buildDiscoveryResource();
    whenIsRegulatorTrue();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_DISCOVERY);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createLicence(951, "AL10", generalDetails1);
    var licence2 = createLicence(953, "AL6", generalDetails1);

    var resource2 = buildProspectResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.DISCOVERY.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));
    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            ResourceRecordQueryResult::getResourceName,
            ResourceRecordQueryResult::getResourceType,
            ResourceRecordQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlock(),
                organisationId1,
                resourceName1,
                ResourceType.DISCOVERY,
                List.of(licence1.getLicenceId(), licence2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceType_assertOnlyProspectDisplayed() {
    var resource1 = buildProspectResource();
    whenIsRegulatorTrue();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createLicence(951, "AL10", generalDetails1);
    var licence2 = createLicence(953, "AL6", generalDetails1);

    var resource2 = buildDiscoveryResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            ResourceRecordQueryResult::getResourceName,
            ResourceRecordQueryResult::getResourceType,
            ResourceRecordQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlock(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(licence1.getLicenceId(), licence2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceType_assertAllResourceTypesDisplayed() {
    whenIsRegulatorTrue();
    var discoveryResource = buildDiscoveryResource();

    var discoverySubmission = buildSubmissionDetail(discoveryResource, SubmissionType.CREATE_DISCOVERY);
    var resourceDetails1 = createResourceDetail(
        discoverySubmission, "NOT_RELEVANT_NAME", 1);
    var discoveryGeneralDetail = createGeneralDetail(discoverySubmission);

    var prospectResource = buildProspectResource();

    var prospectSubmission = buildSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
    createResourceDetail(
        prospectSubmission, "NOT_RELEVANT_NAME", 2);

    var prospectGeneralDetail = createGeneralDetail(prospectSubmission);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name(), ResourceType.DISCOVERY.name()));

    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getResourceType,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                prospectSubmission.getId(),
                ResourceType.PROSPECT,
                prospectSubmission.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                prospectSubmission.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            ),
            tuple(
                discoverySubmission.getId(),
                ResourceType.DISCOVERY,
                discoverySubmission.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                discoverySubmission.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnQuadAndBlock_assertOnlyFilteredDisplayed() {
    whenIsRegulatorTrue();
    var resourceMatchingFilter = buildProspectResource();
    var submissionMatchingFilter = buildSubmissionDetail(resourceMatchingFilter, SubmissionType.CREATE_PROSPECT);
    var resourceNameForMatchingFilter = "The resource name A";
    var generalDetailsMatchingFilter = createGeneralDetail(submissionMatchingFilter);
    var resourceDetailsMatchingFilter = createResourceDetail(
        submissionMatchingFilter, resourceNameForMatchingFilter, 1);
    var quadrantBlockMatchingFilter = "25/12";
    var centreOfInterestQuadrantBlockMatchingFilter =
        createQuadAndBlock(generalDetailsMatchingFilter, quadrantBlockMatchingFilter);
    var trapPropertyWithCentreOfInterestMatchingFilter =
        createTrapProperty(submissionMatchingFilter, quadrantBlockMatchingFilter);

    var resourceNotMatchingFilter = buildProspectResource();
    var submissionNotMatchingFilter = buildSubmissionDetail(resourceNotMatchingFilter, SubmissionType.CREATE_PROSPECT);
    var generalDetailsNotMatchingFilter = createGeneralDetail(submissionNotMatchingFilter);
    createResourceDetail(submissionNotMatchingFilter, "NOT_RELEVANT", 1);
    var quadrantBlockNotMatchingFilter = "50/11";
    var centreOfInterestQuadrantBlockNotMatchingFilter =
        createQuadAndBlock(generalDetailsNotMatchingFilter, quadrantBlockNotMatchingFilter);
    var trapPropertyWithNotMatchingFilterQuadBlock
        = createTrapProperty(submissionNotMatchingFilter, quadrantBlockNotMatchingFilter);

    var form = new SearchResourceRecordsForm();
    form.setCentreOfInterestBlock(quadrantBlockMatchingFilter);
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordQueryResult::getResourceName,
            ResourceRecordQueryResult::getResourceType,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionMatchingFilter.getId(),
                centreOfInterestQuadrantBlockMatchingFilter.getQuadrantBlock(),
                resourceNameForMatchingFilter,
                ResourceType.PROSPECT,
                submissionMatchingFilter.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionMatchingFilter.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnLicence_assertOnlyFilteredDisplayed() {
    whenIsRegulatorTrue();
    var resourceWithWrongLicences = buildProspectResource();
    var submissionWithWrongLicences = buildSubmissionDetail(resourceWithWrongLicences, SubmissionType.CREATE_PROSPECT);
    var generalDetailsWithWrongLicences = createGeneralDetail(submissionWithWrongLicences);
    createResourceDetail(submissionWithWrongLicences, "NOT_RELEVANT_NAME", 46);
    var notMatchingLicence = createLicence(951, "AL10", generalDetailsWithWrongLicences);

    var resourceRightLicence = buildProspectResource();
    var submissionWithRightLicence = buildSubmissionDetail(resourceRightLicence, SubmissionType.CREATE_PROSPECT);
    var generalDetailWithRightLicence = createGeneralDetail(submissionWithRightLicence);
    createResourceDetail(submissionWithRightLicence, "NOT_RELEVANT_NAME", 1);

    var licenceWithNotMatchingId = createLicence(253, "BL10", generalDetailWithRightLicence);
    var licenceMatchingFilter = createLicence(351, "BL6", generalDetailWithRightLicence);

    var form = new SearchResourceRecordsForm();
    form.setLicenceId("351");
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("NOT_SET"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getResourceType,
            ResourceRecordQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionWithRightLicence.getId(),
                ResourceType.PROSPECT,
                List.of(licenceWithNotMatchingId.getLicenceId(), licenceMatchingFilter.getLicenceId()),
                submissionWithRightLicence.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithRightLicence.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @ParameterizedTest
  @EnumSource(OpportunityType.class)
  void getResourceRecordSearchResults_whenFilteringOnOpportunityType_assertOnlyFilteredDisplayed(
      OpportunityType opportunityType) {
    whenIsRegulatorTrue();
    var resourceWithNotMatchingOpportunityType = buildProspectResource();
    var submissionWithNotMatchingOpportunityType =
        buildSubmissionDetail(resourceWithNotMatchingOpportunityType, SubmissionType.CREATE_PROSPECT);

    createGeneralDetail(submissionWithNotMatchingOpportunityType);
    createResourceDetail(submissionWithNotMatchingOpportunityType, "NOT_RELEVANT_NAME", 1);

    var resourceWithMatchingOpportunityType = buildProspectResource();
    var submissionWithMatchingOpportunityType =
        buildSubmissionDetail(resourceWithMatchingOpportunityType, SubmissionType.CREATE_PROSPECT);
    createGeneralDetail(submissionWithMatchingOpportunityType);
    createResourceDetail(submissionWithMatchingOpportunityType, "NOT_RELEVANT_NAME", 2);

    createOpportunityDetail(opportunityType, submissionWithMatchingOpportunityType);
    createOpportunityDetail(null, submissionWithNotMatchingOpportunityType);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of(opportunityType.name()));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getResourceType,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                submissionWithMatchingOpportunityType.getId(),
                ResourceType.PROSPECT,
                submissionWithMatchingOpportunityType.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithMatchingOpportunityType.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenNoFilter_asRegulator_assertAllDisplayed() {
    whenIsRegulatorTrue();
    var discoveryWithDiscoveryOpportunityType = buildDiscoveryResource();
    var submissionWithDiscoveryOpportunityType =
        buildSubmissionDetail(discoveryWithDiscoveryOpportunityType, SubmissionType.CREATE_DISCOVERY);

    createGeneralDetail(submissionWithDiscoveryOpportunityType);
    createResourceDetail(submissionWithDiscoveryOpportunityType, "NOT_RELEVANT_NAME", 1);

    var prospectWithLeadOpportunityType = buildProspectResource();
    var submissionWithLeadOpportunityType =
        buildSubmissionDetail(prospectWithLeadOpportunityType, SubmissionType.CREATE_PROSPECT);
    createGeneralDetail(submissionWithLeadOpportunityType);
    createResourceDetail(submissionWithLeadOpportunityType, "NOT_RELEVANT_NAME", 2);

    createOpportunityDetail(OpportunityType.LEAD, submissionWithLeadOpportunityType);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionWithDiscoveryOpportunityType);

    var form = new SearchResourceRecordsForm();

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getResourceType,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                submissionWithLeadOpportunityType.getId(),
                ResourceType.PROSPECT,
                submissionWithLeadOpportunityType.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithLeadOpportunityType.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            ),
            tuple(
                submissionWithDiscoveryOpportunityType.getId(),
                ResourceType.DISCOVERY,
                submissionWithDiscoveryOpportunityType.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithDiscoveryOpportunityType.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenMatchesFilters_andHasLinkedDiscovery_assertNoResults() {
    whenIsRegulatorTrue();
    var resourceMatchingFiltersWithLinkedResource = buildProspectResource();
    var submissionMatchingFiltersWithLinkedResource = buildSubmissionDetail(resourceMatchingFiltersWithLinkedResource, SubmissionType.CREATE_PROSPECT);
    createResourceDetail(submissionMatchingFiltersWithLinkedResource, "NOT_RELEVANT_NAME", 2);

    var otherResourceSubmissionMatchingFiltersWithLinkedResource = buildProspectResource();
    var otherSubmissionMatchingFiltersWithLinkedResource = buildSubmissionDetail(
        otherResourceSubmissionMatchingFiltersWithLinkedResource, SubmissionType.CREATE_PROSPECT);
    createResourceDetail(otherSubmissionMatchingFiltersWithLinkedResource, "NOT_RELEVANT_NAME", 3);

    var linkedDiscovery = buildDiscoveryResource();

    createResourceLink(otherResourceSubmissionMatchingFiltersWithLinkedResource, linkedDiscovery);
    createResourceLink(resourceMatchingFiltersWithLinkedResource, linkedDiscovery);

    createOpportunityDetail(OpportunityType.PROSPECT, otherSubmissionMatchingFiltersWithLinkedResource);
    createOpportunityDetail(OpportunityType.PROSPECT, submissionMatchingFiltersWithLinkedResource);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("PROSPECT"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .isEmpty();
  }

  @Test
  void getResourceRecordSearchResults_whenTwoMatchesFilters_andOneHasLinkedDiscovery_assertOneResult() {
    whenIsRegulatorTrue();
    var prospectResourceWithNoLink = buildProspectResource();
    var submissionWithNoLinkedDiscovery = buildSubmissionDetail(
        prospectResourceWithNoLink, SubmissionType.CREATE_PROSPECT);
    createResourceDetail(submissionWithNoLinkedDiscovery, "NOT_RELEVANT_NAME", 1);
    var prospectWithLinkedDiscovery = buildProspectResource();
    var submissionWithLinkedDiscovery =
        buildSubmissionDetail(prospectWithLinkedDiscovery, SubmissionType.CREATE_PROSPECT);
    createResourceDetail(submissionWithLinkedDiscovery, "NOT_RELEVANT_NAME", 2);

    var linkedDiscoveryResource = buildDiscoveryResource();

    createResourceLink(prospectWithLinkedDiscovery, linkedDiscoveryResource);

    createOpportunityDetail(OpportunityType.PROSPECT, submissionWithLinkedDiscovery);
    createOpportunityDetail(OpportunityType.PROSPECT, submissionWithNoLinkedDiscovery);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.name()));
    form.setOpportunityTypes(List.of("PROSPECT"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getResourceType,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionWithNoLinkedDiscovery.getId(),
                ResourceType.PROSPECT,
                submissionWithNoLinkedDiscovery.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithNoLinkedDiscovery.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenOneDiscoveryMatchesFilters_andHasLinkedProspect_assertResult() {
    whenIsRegulatorTrue();
    var discoveryWithLinkedProspect = buildDiscoveryResource();
    var submissionWithLinkedProspect = buildSubmissionDetail(discoveryWithLinkedProspect,
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    createResourceDetail(submissionWithLinkedProspect, "NOT_RELEVANT_NAME", 1);

    var linkedProspectResource = buildProspectResource();
    createResourceLink(linkedProspectResource, discoveryWithLinkedProspect);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionWithLinkedProspect);

    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.DISCOVERY.name()));
    form.setOpportunityTypes(List.of("DISCOVERY"));

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getResourceType,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                submissionWithLinkedProspect.getId(),
                ResourceType.DISCOVERY,
                submissionWithLinkedProspect.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithLinkedProspect.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilterOnLicenceRound_whenPartialMatch_assertResultReturned() {
    whenIsRegulatorTrue();
    var resourceWithMatchingLicenceApplication  = buildDiscoveryResource();
    var resourceNotMatchingLicenceApplication = buildDiscoveryResource();
    var submissionMatchingFilter = buildSubmissionDetail(resourceWithMatchingLicenceApplication ,
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var submissionNotMatchingFilter = buildSubmissionDetail(resourceNotMatchingLicenceApplication,
        SubmissionType.CREATE_PROSPECT);
    var matchingFilterLicenceRound = "LICENCE_ROUND_9000";
    var matchingFilterGeneralDetail = createGeneralDetail(submissionMatchingFilter);
    var notMatchingFilterRound = "COOL_LICENCE";
    var notMatchingFilterGeneralDetail = createGeneralDetail(submissionNotMatchingFilter);
    notMatchingFilterGeneralDetail.setLicenceApplicationReference(notMatchingFilterRound);
    matchingFilterGeneralDetail.setLicenceApplicationReference(matchingFilterLicenceRound);

    createResourceDetail(submissionNotMatchingFilter, "default", 1);
    createResourceDetail(submissionMatchingFilter, "default", 1);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionMatchingFilter);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionNotMatchingFilter);

    var form = new SearchResourceRecordsForm();
    form.setLicenceRound("9000");

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId
        )
        .containsExactly(
            submissionMatchingFilter.getId()
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilterOnLicenceRound_whenNoMatch_assertNoResult() {
    whenIsRegulatorTrue();
    var resourceNotMatchingLicenceApplication = buildDiscoveryResource();
    var submissionNotMatchingLicenceApplication = buildSubmissionDetail(resourceNotMatchingLicenceApplication,
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var notMatchingFilterLicenceApplication = "LICENCE_ROUND_9000";
    createResourceDetail(submissionNotMatchingLicenceApplication, "NOT_RELEVANT", 1);
    var generalDetailNotMatchingLicenceApp = createGeneralDetail(submissionNotMatchingLicenceApplication);
    generalDetailNotMatchingLicenceApp.setLicenceApplicationReference(notMatchingFilterLicenceApplication);
    createOpportunityDetail(OpportunityType.DISCOVERY, submissionNotMatchingLicenceApplication);

    var form = new SearchResourceRecordsForm();
    form.setLicenceRound("cool");

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {"FIRST", "SECOND"})
  void getResourceSearchResults_filterOnLicenceRound_whenLicenceRoundOnOlderSubmissionOrCurrent_assertReturnsLatest(
      String licenceRoundFilter) {
    whenIsRegulatorTrue();

    var resourceWithTwoLicenceRounds = buildProspectResource();
    var firstSubmission = buildSubmissionDetail(resourceWithTwoLicenceRounds,
        SubmissionType.CREATE_PROSPECT);
    var latestSubmission = buildSubmissionDetail(resourceWithTwoLicenceRounds,
        SubmissionType.PROSPECT_UPDATE);

    //ensures the test is deterministic, latest submission is always later than first submission
    latestSubmission.setApprovedTimestamp(Instant.now().plus(500000, ChronoUnit.MILLIS));
    entityManager.persistAndFlush(latestSubmission);

    var firstLicenceRoundAndReference = "FIRST_LICENCE";
    var latestLicenceRoundAndReference = "SECOND_LICENCE";
    createResourceDetail(firstSubmission, "not_relevant_name", 1);
    createResourceDetail(latestSubmission, "not_relevant_name", 1);
    var firstSubmissionGeneralDetail = createGeneralDetail(firstSubmission);
    firstSubmissionGeneralDetail.setLicenceApplicationReference(firstLicenceRoundAndReference);
    entityManager.persistAndFlush(firstSubmissionGeneralDetail);
    var latestGeneralDetail = createGeneralDetail(latestSubmission);
    latestGeneralDetail.setLicenceApplicationReference(latestLicenceRoundAndReference);
    entityManager.persistAndFlush(latestGeneralDetail);
    var form = new SearchResourceRecordsForm();
    form.setLicenceRound(licenceRoundFilter);

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getResourceType,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            tuple(
                latestSubmission.getId(),
                ResourceType.PROSPECT,
                latestSubmission.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                latestSubmission.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilterOnLicenceRound_whenLicenceRoundOnNewerDraftSubmission_assertNoResult() {
    whenIsRegulatorTrue();
    var resourceWithLicenceApplications = buildProspectResource();
    var approvedSubmission = buildSubmissionDetail(resourceWithLicenceApplications,
        SubmissionType.CREATE_PROSPECT);
    var draftSubmission = buildSubmissionDetail(resourceWithLicenceApplications,
        SubmissionType.PROSPECT_UPDATE);
    draftSubmission.setStatus(SubmissionDetailStatus.DRAFT);
    var approvedLicenceRoundAndReference = "FIRST_LICENCE";
    var draftLicenceRoundAndReferenceMatchingFilter = "SECOND_LICENCE";
    createResourceDetail(approvedSubmission, "not_relevant", 1);
    createResourceDetail(draftSubmission, "not_relevant", 1);
    var approvedGeneralDetail = createGeneralDetail(approvedSubmission);
    approvedGeneralDetail.setLicenceApplicationReference(approvedLicenceRoundAndReference);
    var draftGeneralDetail = createGeneralDetail(draftSubmission);
    draftGeneralDetail.setLicenceApplicationReference(draftLicenceRoundAndReferenceMatchingFilter);

    var form = new SearchResourceRecordsForm();
    form.setLicenceRound("SECOND");

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);

    assertThat(results)
        .isEmpty();
  }

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

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);
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

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);
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

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);
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

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);
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
  void getResourceRecordSearchResults_whenUserIndustry_whenHasSubmitterAndViewer_andNotOnAlicenceAndNotExtant_assertNoneDisplayed() {
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

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results).isEmpty();
  }

  @Test
  void getResourceRecordSearchResults_whenRegulator_andNotOnAlicenceAndNotExtant_assertAllDisplayed() {
    whenIsRegulatorTrue();

    var resourceWithoutLicence = buildProspectResource();
    var submissionWithoutExtantLicence =
        buildSubmissionDetail(resourceWithoutLicence, SubmissionType.CREATE_PROSPECT);
    var generalDetailWithoutLicence = createGeneralDetail(submissionWithoutExtantLicence);
    createResourceDetail(
        submissionWithoutExtantLicence, "Not relevant name", 1);

    var resourceWithNotExtantLicence = buildProspectResource();
    var submissionWithNotExtantLicence =
        buildSubmissionDetail(resourceWithNotExtantLicence, SubmissionType.CREATE_PROSPECT);
    var nonExtantLicenceId = 2;
    var generalDetailWithNonExtantLicence = createGeneralDetail(submissionWithNotExtantLicence);
    createResourceDetail(
        submissionWithNotExtantLicence, "Not relevant name", 2);
    var notExtantLicence = createLicence(nonExtantLicenceId, "licenceRef", generalDetailWithNonExtantLicence);
    when(licenceQueryService.getExtantLicencesByIds(List.of(notExtantLicence.getLicenceId().toString())))
        .thenReturn(List.of());
    var form = new SearchResourceRecordsForm();

    var results = resourceRecordQueryService.getResourceRecordSearchResults(form);
    assertThat(results).extracting(
            ResourceRecordQueryResult::getSubmissionDetailId,
            ResourceRecordQueryResult::getOrganisationResponsibleId,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            result -> result.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                submissionWithNotExtantLicence.getId(),
                2,
                submissionWithNotExtantLicence.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithNotExtantLicence.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            ),
            tuple(
                submissionWithoutExtantLicence.getId(),
                1,
                submissionWithoutExtantLicence.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionWithoutExtantLicence.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
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

  private void whenIsRegulatorTrue() {
    var user = ServiceUserDetailTestUtil.Builder()
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

    entityManager.persistAndFlush(team);
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        Set.of(RegulatorTeamRole.EXPLORATION.name()));
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
}