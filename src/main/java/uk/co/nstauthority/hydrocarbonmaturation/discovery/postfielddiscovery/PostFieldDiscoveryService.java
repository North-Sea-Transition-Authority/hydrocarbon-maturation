package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication.PostFieldDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@Service
public class PostFieldDiscoveryService {

  private final ResourceService resourceService;
  private final PostFieldDiscoveryLinkRepository postFieldDiscoveryLinkRepository;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SubmissionPersistenceService submissionPersistenceService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final PostFieldDuplicationService postFieldDuplicationService;

  @Autowired
  public PostFieldDiscoveryService(ResourceService resourceService,
                                   PostFieldDiscoveryLinkRepository postFieldDiscoveryLinkRepository,
                                   RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                                   SubmissionPersistenceService submissionPersistenceService,
                                   SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                   SubmissionDetailService submissionDetailService,
                                   ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                   PostFieldDuplicationService postFieldDuplicationService) {
    this.resourceService = resourceService;
    this.postFieldDiscoveryLinkRepository = postFieldDiscoveryLinkRepository;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.postFieldDuplicationService = postFieldDuplicationService;
  }

  @Transactional
  public SubmissionDetail createNewDiscoveryFromField(Resource field, User user, ResourceDetailsForm form) {
    var discovery = resourceService.createResource(ResourceType.DISCOVERY);

    var fieldRegulatorManagedInfo = regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(field);

    fieldRegulatorManagedInfo.ifPresent(regulatorManagedInformation -> regulatorManagedInformationPersistenceService
        .saveResponsibleTeamAndGeoByResource(discovery, regulatorManagedInformation.getResponsibleTeamType()));

    var submission = submissionPersistenceService.createSubmission(discovery);

    var submissionDetail = submissionDetailPersistenceService
            .createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY, user);

    var fieldSubmissionDetail = submissionDetailService.getLatestApprovedSubmissionDetailForResource(field);
    fieldSubmissionDetail.ifPresent(fieldDetail ->
        postFieldDuplicationService.duplicateSubmissionDetails(fieldDetail, submissionDetail)
    );

    resourceDetailsPersistenceService.createInitialResourceDetail(form, submissionDetail);

    createPostFieldDiscoveryLink(field, discovery);

    return submissionDetail;
  }

  @Transactional
  public void createPostFieldDiscoveryLink(Resource field, Resource discovery) {
    var postFieldDiscoveryLink = new PostFieldDiscoveryLink();
    postFieldDiscoveryLink.setFieldResource(field);
    postFieldDiscoveryLink.setDiscoveryResource(discovery);

    postFieldDiscoveryLinkRepository.save(postFieldDiscoveryLink);
  }
}
