package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Service
public class SubmissionDetailService {

  public static final Integer FIRST_VERSION = 1;

  private final SubmissionDetailRepository submissionDetailRepository;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final ResourceLinkService resourceLinkService;

  @Autowired
  public SubmissionDetailService(SubmissionDetailRepository submissionDetailRepository,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                 ResourceLinkService resourceLinkService) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.resourceLinkService = resourceLinkService;
  }

  public Optional<SubmissionDetail> getSubmissionDetail(UUID id) {
    return submissionDetailRepository.findById(id);
  }

  public Optional<SubmissionDetail> getLatestApprovedProspectSubmissionForResource(Resource resource) {
    // Finds the latest prospect update or create update submission that is approved.
    return submissionDetailRepository.findFirstByResourceIdAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(
        resource,
        List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT),
        SubmissionDetailStatus.APPROVED
    );
  }

  public Optional<SubmissionDetail> getLatestApprovedDiscoverySubmissionDetailForResource(Resource resource) {
    // Finds the latest approved discovery (update or create) submission that is approved for the resource.
    return submissionDetailRepository
        .findFirstByResourceIdAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(
            resource,
            List.of(
                SubmissionType.CREATE_DISCOVERY,
                SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
                SubmissionType.UPDATE_DISCOVERY),
            SubmissionDetailStatus.APPROVED);
  }

  public Optional<SubmissionDetail> getLatestApprovedSubmissionDetailForResource(Resource resource) {
    return submissionDetailRepository
        .findFirstByResourceIdAndStatusOrderByApprovedTimestampDesc(resource, SubmissionDetailStatus.APPROVED);
  }

  public SubmissionDetail getSubmissionDetailForIdOrError(UUID id) {
    return getSubmissionDetail(id)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail with id %s not found".formatted(id)));
  }

  public List<SubmissionDetail> getSubmissionDetailsForResource(Resource resource) {
    return submissionDetailRepository.findSubmissionDetailsByResourceId(resource);
  }

  public String getSubmissionTypeName(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType().getTypeName();
  }

  public SubmissionDetail getSubmissionDetailByGeneralDetailOrThrow(UUID generalDetailId) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(generalDetailId);
    return generalDetail.getSubmissionDetail();
  }

  public Optional<SubmissionDetail> getLatestApprovedSubmissionForSubmission(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case UPDATE_DISCOVERY, CREATE_DISCOVERY, RELINQUISHMENT_DISCOVERY ->
        getLatestApprovedDiscoverySubmissionDetailForResource(submissionDetail.getResourceId());
      case POST_DRILL_DRY_HOLE, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
        getLatestApprovedProspectSubmissionForResource(submissionDetail.getResourceId());
      case CREATE_DISCOVERY_FROM_PROSPECT ->
        resourceLinkService
            .getResourceLinksByDiscovery(submissionDetail.getResourceId())
            .stream()
            .map(ResourceLink::getProspectResource)
            .map(this::getLatestApprovedProspectSubmissionForResource)
            .flatMap(Optional::stream)
            .min(Comparator.comparing(SubmissionDetail::getApprovedTimestamp));
      case CREATE_PROSPECT ->
          Optional.of(submissionDetail);
    };
  }
}
