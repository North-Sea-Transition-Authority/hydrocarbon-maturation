package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.EntityManager;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

@Transactional
@IntegrationTest
class ResourceRecordSummaryQueryServiceIntegrationTest {
  @Autowired
  private DSLContext dslContext;

  @Autowired
  private ResourceRecordSummaryQueryService resourceRecordSummaryQueryService;

  @Autowired
  private EntityManager entityManager;

  @Test
  void getResourceRecord_WhenTwoResourceRecords_ReturnsResourceRecordQueried() {
    var resource1 = buildResource();
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

    var resource2 = buildResource();
    var responsibleTeam = buildResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B - to be queried";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);

    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(generalDetails2, quadrantBlock2);
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createLicence(253, "BL10", generalDetails2);
    var licence4 = createLicence(351, "BL6", generalDetails2);

    var result = resourceRecordSummaryQueryService.getResourceRecord(resource2.getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            ResourceRecordSummaryQueryResult::getSubmissionDetailId,
            ResourceRecordSummaryQueryResult::getCentreOfInterestQuadBlock,
            ResourceRecordSummaryQueryResult::getOrganisationResponsibleId,
            ResourceRecordSummaryQueryResult::getResourceName,
            ResourceRecordSummaryQueryResult::getResourceType,
            ResourceRecordSummaryQueryResult::getResponsibleTeam,
            ResourceRecordSummaryQueryResult::getLicencesTheResourceIsOn,
            r -> r.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS),
            r -> r.getApprovedOn().toInstant().truncatedTo(ChronoUnit.MILLIS)
        )
        .containsExactly(
            submissionDetail2.getId(),
            centreOfInterestQuadrantBlock2.getQuadrantBlock(),
            organisationId2,
            resourceName2,
            ResourceType.PROSPECT,
            ResponsibleTeamType.EXPLORATION,
            List.of(licence3.getLicenceId(), licence4.getLicenceId()),
            submissionDetail2.getCreatedTimestamp().truncatedTo(ChronoUnit.MILLIS),
            submissionDetail2.getApprovedTimestamp().truncatedTo(ChronoUnit.MILLIS)
        );
  }

  private Licence createLicence(int id, String ref, GeneralDetail generalDetails) {

    return SubmissionIntegrationTestUtils.createLicence(id, ref, generalDetails, entityManager);
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

  private Resource buildResource() {

    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
  }

  private ResponsibleTeam buildResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType) {

    return SubmissionIntegrationTestUtils.createResponsibleTeam(resource, responsibleTeamType, entityManager);
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType) {

    return SubmissionIntegrationTestUtils.createSubmissionDetail(resource, submissionType, entityManager);
  }
}