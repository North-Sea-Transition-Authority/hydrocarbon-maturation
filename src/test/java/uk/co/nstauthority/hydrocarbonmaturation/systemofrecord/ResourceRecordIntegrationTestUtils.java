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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

public class ResourceRecordIntegrationTestUtils {

  public static ResourceRecordSummaryView emptyResourceRecordSummaryView(UUID submissionDetailId, int responsibleOrganisationId) {
    return new ResourceRecordSummaryView(submissionDetailId, "", responsibleOrganisationId, "", List.of(), List.of(), "");
  }

  static Licence createLicence(int id, String ref, GeneralDetail generalDetails, TestEntityManager entityManager) {
    var licence = new Licence();
    licence.setLicenceId(id);
    licence.setLicenceReference(ref);
    licence.setGeneralDetail(generalDetails);

    entityManager.persistAndFlush(licence);

    return licence;
  }

  static TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock,
                                  TestEntityManager entityManager) {
    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock(centreOfInterestQuadrantBlock);

    entityManager.persistAndFlush(trapProperty);

    return trapProperty;
  }

  static QuadrantAndBlock createQuadAndBlock(GeneralDetail generalDetails, String quadrantBlock, TestEntityManager entityManager) {
    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlock(quadrantBlock);

    entityManager.persistAndFlush(quadBlocks);

    return quadBlocks;
  }

  static GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail, TestEntityManager entityManager) {
    var generalDetails = new GeneralDetail();
    generalDetails.setSubmissionDetail(submissionDetail);

    entityManager.persistAndFlush(generalDetails);

    return generalDetails;
  }

  static ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId,
                                      TestEntityManager entityManager) {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName(resourceName);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(organisationId);

    entityManager.persistAndFlush(resourceDetail);

    return resourceDetail;
  }

  static Resource createResource(TestEntityManager entityManager) {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);

    entityManager.persistAndFlush(resource);

    return resource;
  }

  static ResponsibleTeam createResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType,
                                               TestEntityManager entityManager) {
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(responsibleTeamType);

    entityManager.persistAndFlush(responsibleTeam);

    return responsibleTeam;
  }

  static SubmissionDetail createSubmissionDetail(Resource resource, SubmissionType submissionType, TestEntityManager entityManager) {
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