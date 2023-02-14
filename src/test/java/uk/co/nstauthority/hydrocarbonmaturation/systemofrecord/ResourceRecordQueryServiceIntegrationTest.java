package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.conf.RenderMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

@Transactional
@IntegrationTest
class ResourceRecordQueryServiceIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private ResourceRecordQueryService resourceRecordQueryService;

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private JooqSchemaProviderService jooqSchemaProviderService;

  @BeforeEach
  void setUp() {
    dslContext.settings().setRenderMapping(new RenderMapping().withDefaultSchema("remi"));
  }

  @Test
  void getResourceRecordSearchResults_whenOneFullSubmission_assertAllValuesAndOrderIsCorrect() {
    var resource1 = buildResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);

    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var license3 = createLicense(253, "BL10", generalDetails2);
    var license4 = createLicense(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setProspectResourceType(true);
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
                List.of(license3.getLicenceId(), license4.getLicenceId()),
                submissionDetail2.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail2.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            ),
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlock(),
                organisationId1,
                resourceName1,
                ResourceType.PROSPECT,
                List.of(license1.getLicenceId(), license2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenTwoSubmissionsForOneResource_assertLatestApprovedIsDisplayed() {
    var resource1 = buildResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var submissionDetail2 = buildSubmissionDetail(resource1, SubmissionType.PROSPECT_UPDATE);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var license3 = createLicense(253, "BL10", generalDetails2);
    var license4 = createLicense(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setProspectResourceType(true);
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
                List.of(license3.getLicenceId(), license4.getLicenceId()),
                submissionDetail2.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail2.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceName_assertOnlyFilteredDisplayed() {
    var resource1 = buildResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetail2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var license3 = createLicense(253, "BL10", generalDetails2);
    var license4 = createLicense(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResourceDetailIdForResourceSubmission(resourceDetails1.getId().toString());
    form.setProspectResourceType(true);

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
                List.of(license1.getLicenceId(), license2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResponsibleOrganisation_assertOnlyFilteredDisplayed() {
    var resource1 = buildResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var license3 = createLicense(253, "BL10", generalDetails2);
    var license4 = createLicense(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setResponsibleOrganisationId(organisationId1);
    form.setProspectResourceType(true);

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
                List.of(license1.getLicenceId(), license2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceType_assertOnlyDiscoveryDisplayed() {
    var resource1 = buildResource();
    resource1.setType(ResourceType.DISCOVERY);
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_DISCOVERY);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    resource2.setType(ResourceType.PROSPECT);
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var form = new SearchResourceRecordsForm();
    form.setDiscoveryResourceType(true);
    form.setProspectResourceType(false);

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
                List.of(license1.getLicenceId(), license2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceType_assertOnlyProspectDisplayed() {
    var resource1 = buildResource();
    resource1.setType(ResourceType.PROSPECT);
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    resource2.setType(ResourceType.DISCOVERY);
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var form = new SearchResourceRecordsForm();
    form.setDiscoveryResourceType(false);
    form.setProspectResourceType(true);

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
                List.of(license1.getLicenceId(), license2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnResourceType_assertAllResourceTypesDisplayed() {
    var resource1 = buildResource();
    resource1.setType(ResourceType.DISCOVERY);
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_DISCOVERY);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    resource2.setType(ResourceType.PROSPECT);
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);

    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var license3 = createLicense(253, "BL10", generalDetails2);
    var license4 = createLicense(351, "BL6", generalDetails2);
    var form = new SearchResourceRecordsForm();
    form.setDiscoveryResourceType(true);
    form.setProspectResourceType(true);

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
                submissionDetail2.getId(),
                centreOfInterestQuadrantBlock2.getQuadrantBlock(),
                organisationId2,
                resourceName2,
                ResourceType.PROSPECT,
                List.of(license3.getLicenceId(), license4.getLicenceId()),
                submissionDetail2.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail2.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            ),
            tuple(
                submissionDetail1.getId(),
                centreOfInterestQuadrantBlock1.getQuadrantBlock(),
                organisationId1,
                resourceName1,
                ResourceType.DISCOVERY,
                List.of(license1.getLicenceId(), license2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  @Test
  void getResourceRecordSearchResults_whenFilteringOnQuadAndBlock_assertOnlyFilteredDisplayed() {
    var resource1 = buildResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);
    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(generalDetails1, quadrantBlock1);
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var license1 = createLicense(951, "AL10", generalDetails1);
    var license2 = createLicense(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);
    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var license3 = createLicense(253, "BL10", generalDetails2);
    var license4 = createLicense(351, "BL6", generalDetails2);

    var form = new SearchResourceRecordsForm();
    form.setCentreOfInterestBlock(quadrantBlock1);
    form.setProspectResourceType(true);
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
                List.of(license1.getLicenceId(), license2.getLicenceId()),
                submissionDetail1.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
                submissionDetail1.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
            )
        );
  }

  private Licence createLicense(int id, String ref, GeneralDetail generalDetails) {
    var licence = new Licence();
    licence.setLicenceId(id);
    licence.setLicenceReference(ref);
    licence.setGeneralDetail(generalDetails);
    
    entityManager.persistAndFlush(licence);
    
    return licence;
  }

  private TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock) {
    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock(centreOfInterestQuadrantBlock);
    
    entityManager.persistAndFlush(trapProperty);
    
    return trapProperty;
  }

  private QuadrantAndBlock createQuadAndBlock(GeneralDetail generalDetails, String quadrantBlock) {
    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlock(quadrantBlock);
    
    entityManager.persistAndFlush(quadBlocks);
    
    return quadBlocks;
  }

  @NotNull
  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetails = new GeneralDetail();
    generalDetails.setSubmissionDetail(submissionDetail);

    entityManager.persistAndFlush(generalDetails);
    
    return generalDetails;
  }

  @NotNull
  private ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId) {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName(resourceName);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(organisationId);

    entityManager.persistAndFlush(resourceDetail);

    return resourceDetail;
  }

  @NotNull
  private Resource buildResource() {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    
    entityManager.persistAndFlush(resource);
    
    return resource;
  }

  @NotNull
  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(1));
    submissionDetail.setApprovedTimestamp(Instant.now());
    submissionDetail.setResourceId(resource);
    
    entityManager.persistAndFlush(submissionDetail);
    
    return submissionDetail;
  }
}