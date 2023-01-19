package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Service
public class SubmissionDetailService {

  public static final Integer FIRST_VERSION = 1;

  private final SubmissionDetailRepository submissionDetailRepository;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final Clock clock;
  private final UserDetailService userDetailService;

  @Autowired
  public SubmissionDetailService(SubmissionDetailRepository submissionDetailRepository,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                 Clock clock,
                                 UserDetailService userDetailService) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.clock = clock;
    this.userDetailService = userDetailService;
  }

  public Optional<SubmissionDetail> getSubmissionDetail(UUID id) {
    return submissionDetailRepository.findById(id);
  }

  public Optional<SubmissionDetail> getLatestApprovedProspectSubmissionForResource(Resource resource) {
    // Finds the latest prospect update or create update submission that is approved.
    return submissionDetailRepository
        .findFirstByResourceIdAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(
            resource,
            List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT),
            SubmissionDetailStatus.APPROVED);
  }

  Optional<SubmissionDetail> getLatestSubmissionDetailByResource(Resource resource) {
    return submissionDetailRepository.findFirstByResourceIdOrderByCreatedTimestampDesc(resource);
  }

  public SubmissionDetail getLatestSubmissionDetailForResourceOrThrow(Resource resource) {
    return getLatestSubmissionDetailByResource(resource)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail for resource with ID %s not found"
            .formatted(resource.getId())));
  }

  public SubmissionDetail getSubmissionDetailForIdOrError(UUID id) {
    return getSubmissionDetail(id)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail with id %s not found".formatted(id)));
  }

  SubmissionDetail createInitialSubmissionDetail(Resource resource, SubmissionType submissionType) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setResourceId(resource);
    submissionDetail.setCreatedByWuaId(userDetailService.getUserWuaId());
    submissionDetail.setCreatedTimestamp(clock.instant());
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setVersionNumber(FIRST_VERSION);
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  @Transactional
  public void approveSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail.setApprovedTimestamp(clock.instant());
    submissionDetail.setApprovedByWuaId(userDetailService.getUserWuaId());
    submissionDetailRepository.save(submissionDetail);
  }

  @Transactional
  public void completeSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmittedTimestamp(clock.instant());
    submissionDetail.setSubmittedByWuaId(userDetailService.getUserWuaId());
    submissionDetailRepository.save(submissionDetail);
    approveSubmission(submissionDetail);
  }

  public List<SubmissionDetail> getSubmissionDetailsForResource(Resource resource) {
    return submissionDetailRepository.findSubmissionDetailsByResourceId(resource);
  }

  @Transactional
  public void deleteSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    submissionDetailRepository.save(submissionDetail);
  }

  public Optional<String> getResourceName(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(GeneralDetail::getResourceName);
  }

  public String getResourceNameOrEmpty(SubmissionDetail submissionDetail) {
    return getResourceName(submissionDetail).orElse("");
  }

  public String getSubmissionTypeName(SubmissionDetail submissionDetail) {
    return submissionDetail.getSubmissionType().getTypeName();
  }
}
