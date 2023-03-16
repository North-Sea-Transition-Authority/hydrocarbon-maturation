package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
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

  public static Licence createLicence(int id, String ref, GeneralDetail generalDetails, EntityManager entityManager) {
    var licence = new Licence();
    licence.setLicenceId(id);
    licence.setLicenceReference(ref);
    licence.setGeneralDetail(generalDetails);

    entityManager.persist(licence);
    entityManager.flush();

    return licence;
  }

  public static TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock,
                                                EntityManager entityManager) {
    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock(centreOfInterestQuadrantBlock);

    entityManager.persist(trapProperty);
    entityManager.flush();

    return trapProperty;
  }

  public static QuadrantAndBlock createQuadAndBlock(GeneralDetail generalDetails, String quadrantBlock, EntityManager entityManager) {
    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlock(quadrantBlock);

    entityManager.persist(quadBlocks);
    entityManager.flush();

    return quadBlocks;
  }

  public static GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail, EntityManager entityManager) {
    var generalDetails = new GeneralDetail();
    generalDetails.setSubmissionDetail(submissionDetail);

    entityManager.persist(generalDetails);
    entityManager.flush();

    return generalDetails;
  }

  public static ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId,
                                                    EntityManager entityManager) {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName(resourceName);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(organisationId);

    entityManager.persist(resourceDetail);
    entityManager.flush();

    return resourceDetail;
  }

  public static Resource createResource(EntityManager entityManager, ResourceType prospect) {
    var resource = new Resource();
    resource.setType(prospect);
    resource.setCreatedTimestamp(Instant.now());

    entityManager.persist(resource);
    entityManager.flush();

    return resource;
  }

  public static RegulatorManagedInformation createRegulatorManagedInformation(Resource resource,
                                                                              ResponsibleTeamType responsibleTeamType,
                                                                              EntityManager entityManager) {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);

    entityManager.persist(regulatorManagedInformation);
    entityManager.flush();

    return regulatorManagedInformation;
  }

  public static SubmissionDetail createSubmissionDetail(Resource resource, SubmissionType submissionType, EntityManager entityManager) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(10));
    submissionDetail.setSubmittedTimestamp(Instant.now().minusSeconds(5));
    submissionDetail.setApprovedTimestamp(Instant.now());
    submissionDetail.setResourceId(resource);

    entityManager.persist(submissionDetail);
    entityManager.flush();

    return submissionDetail;
  }
}