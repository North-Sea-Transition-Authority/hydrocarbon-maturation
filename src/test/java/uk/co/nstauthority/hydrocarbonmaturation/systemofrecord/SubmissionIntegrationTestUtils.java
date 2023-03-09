package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.SubmissionRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

public class SubmissionIntegrationTestUtils {

  public static ResourceRecordSummaryView emptyResourceRecordSummaryView(UUID submissionDetailId, int responsibleOrganisationId) {
    return new ResourceRecordSummaryView(submissionDetailId, "", responsibleOrganisationId, "", List.of(), List.of(), "");
  }

  public static SubmissionRecordSummaryView emptySubmissionRecordSummaryView(UUID submissionDetailId,
                                                                             int responsibleOrganisationId,
                                                                             SubmissionType submissionType) {
    return new SubmissionRecordSummaryView(submissionDetailId, submissionType, "",
        responsibleOrganisationId, "", List.of(1),
        List.of("1"), ResponsibleTeamType.EXPLORATION.getDisplayName(), ResponsibleTeamType.EXPLORATION);
  }

  public static Licence createLicence(int id, String ref, GeneralDetail generalDetails, TestEntityManager entityManager) {
    var licence = new Licence();
    licence.setLicenceId(id);
    licence.setLicenceReference(ref);
    licence.setGeneralDetail(generalDetails);

    entityManager.persistAndFlush(licence);

    return licence;
  }

  public static TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock,
                                                TestEntityManager entityManager) {
    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock(centreOfInterestQuadrantBlock);

    entityManager.persistAndFlush(trapProperty);

    return trapProperty;
  }

  public static QuadrantAndBlock createQuadAndBlock(GeneralDetail generalDetails, String quadrantBlock, TestEntityManager entityManager) {
    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlock(quadrantBlock);

    entityManager.persistAndFlush(quadBlocks);

    return quadBlocks;
  }

  public static GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail, TestEntityManager entityManager) {
    var generalDetails = new GeneralDetail();
    generalDetails.setSubmissionDetail(submissionDetail);

    entityManager.persistAndFlush(generalDetails);

    return generalDetails;
  }

  public static ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId,
                                                    TestEntityManager entityManager) {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName(resourceName);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(organisationId);

    entityManager.persistAndFlush(resourceDetail);

    return resourceDetail;
  }

  public static Resource createResource(TestEntityManager entityManager) {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);

    entityManager.persistAndFlush(resource);

    return resource;
  }

  public static Resource buildProspectResource(TestEntityManager entityManager) {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);

    entityManager.persistAndFlush(resource);

    return resource;
  }

  public static ResponsibleTeam createResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType,
                                                      TestEntityManager entityManager) {
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(responsibleTeamType);

    entityManager.persistAndFlush(responsibleTeam);

    return responsibleTeam;
  }

  public static SubmissionDetail createSubmissionDetail(Resource resource, SubmissionType submissionType, TestEntityManager entityManager) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(10));
    submissionDetail.setSubmittedTimestamp(Instant.now().minusSeconds(5));
    submissionDetail.setApprovedTimestamp(Instant.now());
    submissionDetail.setResourceId(resource);

    entityManager.persistAndFlush(submissionDetail);

    return submissionDetail;
  }
}