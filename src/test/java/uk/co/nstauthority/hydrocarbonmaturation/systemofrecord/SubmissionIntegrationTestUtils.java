package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.SubmissionRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

public class SubmissionIntegrationTestUtils {

  public static ResourceRecordSummaryView emptyResourceRecordSummaryView(UUID submissionDetailId, int responsibleOrganisationId) {
    return new ResourceRecordSummaryView(
        submissionDetailId,
        "",
        responsibleOrganisationId,
        "",
        List.of(),
        List.of(),
        null,
        null,
        null);
  }

  public static SubmissionRecordSummaryView emptyDiscoveryOrProspectSubmissionRecordSummaryView(UUID submissionDetailId,
                                                                                                int responsibleOrganisationId,
                                                                                                SubmissionType submissionType) {
    return new SubmissionRecordSummaryView(submissionDetailId, submissionType, "",
        responsibleOrganisationId, "", List.of(1),
        List.of("1"), ResponsibleTeamType.EXPLORATION.getDisplayName(), ResponsibleTeamType.EXPLORATION,
        null, null);
  }

  public static SubmissionQuerySearchRecord getSubmissionQuerySearchRecord(SubmissionDetail submissionDetail) {
    return new SubmissionQuerySearchRecord(
        submissionDetail.getStatus(),
        submissionDetail.getAcceptedTimestamp() != null
            ? Timestamp.from(submissionDetail.getAcceptedTimestamp())
            : null,
        submissionDetail.getId(),
        submissionDetail.getSubmissionType()
    );
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

  public static QuadrantAndBlock createQuadAndBlock(
      GeneralDetail generalDetails,
      String quadrantBlockReference,
      String quadrant,
      Integer block,
      OnshoreOffshore onshoreOffshore,
      EntityManager entityManager) {
    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlockReference(quadrantBlockReference);
    quadBlocks.setQuadrant(quadrant);
    quadBlocks.setBlock(block);
    quadBlocks.setOnshoreOffshore(onshoreOffshore);

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

  public static ResourceLink createResourceLink(Resource prospect, Resource discovery, EntityManager entityManager) {
    var resourceLink = new ResourceLink();
    resourceLink.setDiscoveryResource(discovery);
    resourceLink.setProspectResource(prospect);

    entityManager.persist(resourceLink);
    entityManager.flush();

    return resourceLink;
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

  /*
   * Note that this method creates 2 fields - the new desired field and a parent with status 200
   */
  public static Field createFieldWithParent(Resource resource,
                                            String correctedLegalFieldName,
                                            int responsibleOrganisationId,
                                            EntityManager entityManager) {
    var parentResource = createResource(entityManager, ResourceType.FIELD);
    entityManager.persist(parentResource);
    entityManager.flush();

    var parentField = new Field();
    parentField.setResource(parentResource);
    parentField.setCorrectedLegalFieldName("Parent name");

    entityManager.persist(parentField);
    entityManager.flush();

    var field = new Field();
    field.setResource(resource);
    field.setCorrectedLegalFieldName(correctedLegalFieldName);
    field.setResponsibleOrganisationId(responsibleOrganisationId);
    field.setParentDeterminedField(parentField);

    entityManager.persist(field);
    entityManager.flush();

    return field;
  }

  public static Resource createResource(EntityManager entityManager, ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setCreatedTimestamp(Instant.now());

    entityManager.persist(resource);
    entityManager.flush();

    return resource;
  }

  public static RegulatorManagedInformation createRegulatorManagedInformation(Resource resource,
                                                                              ResponsibleTeamType responsibleTeamType,
                                                                              GeographicalArea geographicalArea,
                                                                              EntityManager entityManager) {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    regulatorManagedInformation.setGeographicalArea(geographicalArea);

    entityManager.persist(regulatorManagedInformation);
    entityManager.flush();
    return regulatorManagedInformation;
  }

  public static SubmissionDetail createSubmissionDetail(Submission submission,
                                                        SubmissionType submissionType,
                                                        EntityManager entityManager) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail.setVersionNumber(1);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(100));
    submissionDetail.setCreatedByWuaId(1);
    submissionDetail.setSubmittedTimestamp(Instant.now().minusSeconds(50));
    submissionDetail.setAcceptedTimestamp(Instant.now());
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    return submissionDetail;
  }

  public static SubmissionDetail createSubmissionDetail(Submission submission,
                                                        SubmissionType submissionType,
                                                        EntityManager entityManager,
                                                        Instant acceptedTimestamp) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail.setVersionNumber(1);
    submissionDetail.setCreatedTimestamp(Instant.now().minusSeconds(100));
    submissionDetail.setCreatedByWuaId(1);
    submissionDetail.setSubmittedTimestamp(Instant.now().minusSeconds(50));
    submissionDetail.setAcceptedTimestamp(acceptedTimestamp);
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    return submissionDetail;
  }

  public static Submission createSubmission(Resource resource, EntityManager entityManager) {
    var submission = new Submission();
    submission.setResource(resource);
    entityManager.persist(submission);
    entityManager.flush();
    return submission;
  }

  public static Submission createSubmission(Resource resource, Boolean withdrawn, EntityManager entityManager) {
    var submission = new Submission();
    submission.setResource(resource);
    submission.setWithdrawn(withdrawn);
    entityManager.persist(submission);
    entityManager.flush();
    return submission;
  }

  public static FieldStatusHistory createFieldStatusHistory(Field field,
                                                            FieldStatus status,
                                                            LocalDate statusDate,
                                                            EntityManager entityManager) {
    var fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setField(field);
    fieldStatusHistory.setFieldStatus(status);
    fieldStatusHistory.setStatusDate(statusDate);
    entityManager.persist(fieldStatusHistory);
    entityManager.flush();
    return fieldStatusHistory;
  }
}