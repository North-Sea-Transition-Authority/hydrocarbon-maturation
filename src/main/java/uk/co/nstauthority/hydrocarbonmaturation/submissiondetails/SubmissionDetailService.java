package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

@Service
public class SubmissionDetailService {

  public static final Integer FIRST_VERSION = 1;

  private final SubmissionDetailRepository submissionDetailRepository;
  private final Clock clock;

  @Autowired
  public SubmissionDetailService(SubmissionDetailRepository submissionDetailRepository, Clock clock) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.clock = clock;
  }

  Optional<SubmissionDetail> getSubmissionDetail(UUID id) {
    return submissionDetailRepository.findById(id);
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
    //TODO change created by wua id to be the actual user's id
    submissionDetail.setCreatedByWuaId(1);
    submissionDetail.setCreatedTimestamp(clock.instant());
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setVersionNumber(FIRST_VERSION);
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  @Transactional
  public void completeSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetailRepository.save(submissionDetail);
  }

  public List<SubmissionDetail> getSubmissionDetailsForResource(Resource resource) {
    return submissionDetailRepository.findSubmissionDetailsByResourceId(resource);
  }
}
