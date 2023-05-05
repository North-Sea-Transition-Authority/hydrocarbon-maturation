package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@Service
public class SubmissionDetailService {

  public static final Integer FIRST_VERSION = 1;

  private final SubmissionDetailRepository submissionDetailRepository;
  private final ResourceLinkService resourceLinkService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionPersistenceService submissionPersistenceService;

  @Autowired
  public SubmissionDetailService(SubmissionDetailRepository submissionDetailRepository,
                                 ResourceLinkService resourceLinkService,
                                 ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                 SubmissionPersistenceService submissionPersistenceService) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.resourceLinkService = resourceLinkService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionPersistenceService = submissionPersistenceService;
  }

  public Optional<SubmissionDetail> getSubmissionDetail(UUID id) {
    return submissionDetailRepository.findById(id);
  }

  public Optional<SubmissionDetail> getLatestApprovedProspectSubmissionForResource(Resource resource) {
    // Finds the latest prospect update or create update submission that is approved.
    return submissionDetailRepository.findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(
        submissionPersistenceService.getSubmissionsByResource(resource),
        List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT),
        SubmissionDetailStatus.APPROVED
    );
  }

  public Optional<SubmissionDetail> getApprovedOrSubmittedSubmissionDetailByResource(Resource resource) {
    return submissionDetailRepository.findFirstBySubmissionInAndStatusInOrderByVersionNumberDesc(
        submissionPersistenceService.getSubmissionsByResource(resource),
        List.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)
    );
  }

  public Optional<SubmissionDetail> getLatestApprovedDiscoverySubmissionDetailForResource(Resource resource) {
    // Finds the latest approved discovery (update or create) submission that is approved for the resource.
    return submissionDetailRepository
        .findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(
            submissionPersistenceService.getSubmissionsByResource(resource),
            List.of(
                SubmissionType.CREATE_DISCOVERY,
                SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
                SubmissionType.UPDATE_DISCOVERY),
            SubmissionDetailStatus.APPROVED);
  }

  public Optional<SubmissionDetail> getLatestApprovedSubmissionDetailForResource(Resource resource) {
    return submissionDetailRepository
        .findFirstBySubmissionInAndStatusOrderByApprovedTimestampDesc(
            submissionPersistenceService.getSubmissionsByResource(resource),
            SubmissionDetailStatus.APPROVED);
  }

  public SubmissionDetail getSubmissionDetailForIdOrError(UUID id) {
    return getSubmissionDetail(id)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail with id %s not found".formatted(id)));
  }

  public List<SubmissionDetail> getSubmissionDetailsForResource(Resource resource) {
    return submissionDetailRepository.findSubmissionDetailsBySubmissionInOrderByVersionNumberDesc(
        submissionPersistenceService.getSubmissionsByResource(resource));
  }

  public String getSubmissionTypeName(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType().getTypeName();
  }

  public SubmissionDetail getSubmissionDetailByResourceDetailOrThrow(UUID resourceDetailId) {
    var generalDetail = resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(resourceDetailId);
    return generalDetail.getSubmissionDetail();
  }

  public boolean isLatestSubmissionForResourceRelinquishment(Resource resource) {
    return submissionDetailRepository
        .findFirstBySubmissionInAndSubmissionTypeInAndStatusInOrderBySubmittedTimestampDesc(
            submissionPersistenceService.getSubmissionsByResource(resource),
            List.of(SubmissionType.RELINQUISHMENT_DISCOVERY, SubmissionType.RELINQUISHMENT_PROSPECT),
            List.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED))
        .isPresent();
  }

  public Optional<SubmissionDetail> getLatestApprovedSubmissionForSubmission(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case UPDATE_DISCOVERY, RELINQUISHMENT_DISCOVERY ->
        getLatestApprovedDiscoverySubmissionDetailForResource(submissionDetail.getSubmission().getResource());
      case POST_DRILL_DRY_HOLE, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
        getLatestApprovedProspectSubmissionForResource(submissionDetail.getSubmission().getResource());
      case CREATE_DISCOVERY_FROM_PROSPECT ->
        resourceLinkService
            .getResourceLinksByDiscovery(submissionDetail.getSubmission().getResource())
            .stream()
            .map(ResourceLink::getProspectResource)
            .map(this::getLatestApprovedProspectSubmissionForResource)
            .flatMap(Optional::stream)
            .min(Comparator.comparing(SubmissionDetail::getApprovedTimestamp));
      case CREATE_PROSPECT, CREATE_DISCOVERY ->
          Optional.of(submissionDetail);
    };
  }

  public boolean isLatestSubmissionDetailForSubmissionWithStatuses(
      SubmissionDetail submissionDetail, Set<SubmissionDetailStatus> statuses) {
    return submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submissionDetail.getSubmission(), statuses)
        .map(foundSubmissionDetail -> foundSubmissionDetail.getId().equals(submissionDetail.getId()))
        .orElse(false);
  }

  public List<SubmissionDetail> getSubmissionDetailsBySubmission(Submission submission) {
    return submissionDetailRepository.findAllBySubmission(submission);
  }
}
