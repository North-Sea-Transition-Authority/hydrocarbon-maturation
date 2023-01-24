package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Service
public class SubmissionDetailService {

  public static final Integer FIRST_VERSION = 1;

  private final SubmissionDetailRepository submissionDetailRepository;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Autowired
  public SubmissionDetailService(SubmissionDetailRepository submissionDetailRepository,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
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

  public List<Resource> getResourcesByTypeAndSubmissionDetailsResourceName(ResourceType resourceType, String name) {
    return generalDetailsPersistenceService.getGeneralDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getResourceId())
        .distinct()
        .filter(resource -> resource.getType() == resourceType)
        .toList();
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

  public SubmissionDetail getSubmissionDetailForIdOrError(UUID id) {
    return getSubmissionDetail(id)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail with id %s not found".formatted(id)));
  }

  public List<SubmissionDetail> getSubmissionDetailsForResource(Resource resource) {
    return submissionDetailRepository.findSubmissionDetailsByResourceId(resource);
  }

  public Optional<String> getResourceName(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(GeneralDetail::getResourceName);
  }

  public String getResourceNameOrDefault(SubmissionDetail submissionDetail) {
    return getResourceName(submissionDetail)
        .orElseGet(() -> SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
  }

  public String getSubmissionTypeName(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType().getTypeName();
  }

  public SubmissionDetail getSubmissionDetailByGeneralDetailOrThrow(UUID generalDetailId) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(generalDetailId);
    return generalDetail.getSubmissionDetail();
  }
}
