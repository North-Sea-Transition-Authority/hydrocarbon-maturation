package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionRepository;

@Service
public class SubmissionDetailService {

  public static final Integer FIRST_VERSION = 1;

  private final SubmissionDetailRepository submissionDetailRepository;
  private final ResourceLinkService resourceLinkService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionPersistenceService submissionPersistenceService;
  private final SubmissionRepository submissionRepository;

  @Autowired
  public SubmissionDetailService(SubmissionDetailRepository submissionDetailRepository,
                                 ResourceLinkService resourceLinkService,
                                 ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                 SubmissionPersistenceService submissionPersistenceService,
                                 SubmissionRepository submissionRepository) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.resourceLinkService = resourceLinkService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.submissionRepository = submissionRepository;
  }

  public Optional<SubmissionDetail> getSubmissionDetail(UUID id) {
    if (id == null) {
      return Optional.empty();
    }
    return submissionDetailRepository.findById(id);
  }

  public List<SubmissionDetail> getSubmissionDetailsByLegacyResDataId(Integer legacyResDataId) {
    return submissionDetailRepository.findByLegacyResDataId(legacyResDataId);
  }

  public Optional<SubmissionDetail> getLatestApprovedProspectSubmissionForResource(Resource resource) {
    // Finds the latest prospect update or create update submission that is approved.
    return submissionDetailRepository.findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByAcceptedTimestampDesc(
        submissionPersistenceService.getSubmissionsByResource(resource),
        List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT),
        SubmissionDetailStatus.ACCEPTED
    );
  }

  public Optional<SubmissionDetail> getLatestApprovedDiscoverySubmissionDetailForResource(Resource resource) {
    // Finds the latest approved discovery (update or create) submission that is approved for the resource.
    return submissionDetailRepository
        .findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByAcceptedTimestampDesc(
            submissionPersistenceService.getSubmissionsByResource(resource),
            List.of(
                SubmissionType.CREATE_DISCOVERY,
                SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
                SubmissionType.UPDATE_DISCOVERY),
            SubmissionDetailStatus.ACCEPTED);
  }

  public Optional<SubmissionDetail> getLatestApprovedSubmissionDetailForResource(Resource resource) {
    return submissionDetailRepository
        .findFirstBySubmissionInAndStatusOrderByAcceptedTimestampDesc(
            submissionPersistenceService.getSubmissionsByResource(resource),
            SubmissionDetailStatus.ACCEPTED);
  }

  public Optional<SubmissionDetail> getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes(
      Resource resource,
      List<SubmissionType> submissionTypes) {
    return submissionDetailRepository
        .findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByAcceptedTimestampDesc(
            submissionPersistenceService.getSubmissionsByResource(resource),
            submissionTypes,
            SubmissionDetailStatus.ACCEPTED);
  }

  public Optional<SubmissionDetail> getMostRecentlySubmittedSubmissionDetailForResource(Resource resource) {
    return getMostRecentlySubmittedDetailForResourceWithSubmissionTypes(
        resource,
        SubmissionType.getSubmissionTypesForResourceType(resource.getType())
    );
  }

  public SubmissionDetail getSubmissionDetailForIdOrError(UUID id) {
    return getSubmissionDetail(id)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail with id %s not found".formatted(id)));
  }

  public List<SubmissionDetail> getExtantSubmissionDetailsForResource(Resource resource) {
    return submissionPersistenceService.getSubmissionsByResource(resource)
        .stream()
        .map(submission -> getLatestExtantVersionSubmissionDetailBySubmissionAndStatuses(
            submission,
            Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.DRAFT)
        ))
        .flatMap(Optional::stream)
        .toList();
  }

  public String getSubmissionTypeNameWithAcronym(SubmissionDetail submissionDetail) {
    var submissionType = submissionDetail.getSubmissionType();
    return getSubmissionTypeNameWithAcronym(submissionType);
  }

  public String getSubmissionTypeNameWithAcronym(SubmissionType submissionType) {
    return submissionType.getAcronym() == null
        ? "%s".formatted(submissionType.getTypeName())
        : "%s (%s)".formatted(submissionType.getTypeName(), submissionType.getAcronym());
  }

  public String getSubmissionTypeNameLowercaseWithAcronym(SubmissionDetail submissionDetail) {
    var submissionType = submissionDetail.getSubmissionType();
    return getSubmissionTypeNameLowercaseWithAcronym(submissionType);
  }

  public String getSubmissionTypeNameLowercaseWithAcronym(SubmissionType submissionType) {
    return submissionType.getAcronym() == null
        ? "%s".formatted(submissionType.getTypeName().toLowerCase())
        : "%s (%s)".formatted(submissionType.getTypeName().toLowerCase(), submissionType.getAcronym());
  }

  public SubmissionDetail getSubmissionDetailByResourceDetailOrThrow(UUID resourceDetailId) {
    var generalDetail = resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(resourceDetailId);
    return generalDetail.getSubmissionDetail();
  }

  public boolean isLatestSubmissionForResourceRelinquishment(Resource resource) {
    return getMostRecentlySubmittedDetailForResourceWithSubmissionTypes(
        resource,
        List.of(SubmissionType.RELINQUISHMENT_DISCOVERY, SubmissionType.RELINQUISHMENT_PROSPECT))
        .isPresent();
  }

  public Optional<SubmissionDetail> getLatestApprovedSubmissionForSubmission(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case UPDATE_DISCOVERY, RELINQUISHMENT_DISCOVERY ->
          getLatestApprovedDiscoverySubmissionDetailForResource(submissionDetail.getSubmission().getResource());
      case POST_DRILL_DRY_HOLE, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
          getLatestApprovedProspectSubmissionForResource(submissionDetail.getSubmission().getResource());
      case CREATE_DISCOVERY_FROM_PROSPECT -> resourceLinkService
          .getResourceLinksByDiscovery(submissionDetail.getSubmission().getResource())
          .stream()
          .map(ResourceLink::getProspectResource)
          .map(this::getLatestApprovedProspectSubmissionForResource)
          .flatMap(Optional::stream)
          .min(Comparator.comparing(SubmissionDetail::getAcceptedTimestamp));
      case CREATE_PROSPECT, CREATE_DISCOVERY, CREATE_FIELD, CREATE_CSR, CREATE_FDP, CREATE_FDPA, CREATE_COP ->
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

  public Optional<SubmissionDetail> getLatestExtantVersionSubmissionDetailBySubmissionAndStatuses(
      Submission submission, Set<SubmissionDetailStatus> statuses
  ) {
    return submissionDetailRepository.findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
        submission,
        statuses
    );
  }

  public boolean openCorrectionExists(Set<SubmissionDetailStatus> statuses, Resource resource) {
    return submissionDetailRepository.existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
        statuses, resource
    );
  }

  public boolean hasFieldSubmissionDataBeenCarriedForward(SubmissionDetail submissionDetail) {
    if (BooleanUtils.isFalse(submissionDetail.isFieldSubmission())) {
      return false;
    }

    var resource = submissionDetail.getSubmission().getResource();
    var allSubmissionDetails = submissionRepository.findSubmissionsByResource(resource)
        .stream()
        .map(submissionDetailRepository::findAllBySubmission)
        .flatMap(Collection::stream)
        .filter(storedSubmissionDetail -> !storedSubmissionDetail.equals(submissionDetail))
        .toList();

    return !allSubmissionDetails.isEmpty();
  }

  public boolean hasDraftOrSubmittedSubmission(Resource resource) {
    var extantSubmissionDetails = getExtantSubmissionDetailsForResource(resource);

    return extantSubmissionDetails.stream()
        .anyMatch(SubmissionDetail::isDraftOrSubmittedSubmission);
  }

  /*
   * Find the submissionDetail that was submitted most recently for a given resource
   * The most recently submitted submission may either be SUBMITTED or ACCEPTED
   * @param resource  the resource which we want to check  the submissionDetails of
   * @param submissionTypes the allowed submissionTypes for the desired  submissionDetail
   * @return the submissionDetail most recently submitted or empty if there has not yet been one
   */
  private Optional<SubmissionDetail> getMostRecentlySubmittedDetailForResourceWithSubmissionTypes(
      Resource resource,
      List<SubmissionType> submissionTypes) {
    return submissionDetailRepository.findFirstBySubmissionInAndSubmissionTypeInAndStatusInOrderBySubmittedTimestampDesc(
        submissionPersistenceService.getSubmissionsByResource(resource),
        submissionTypes,
        List.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)
    );
  }
}
