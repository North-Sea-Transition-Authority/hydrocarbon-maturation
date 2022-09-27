package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

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

  public Optional<SubmissionDetail> getSubmissionDetailForId(UUID id) {
    return getSubmissionDetail(id);
  }

  public SubmissionDetail getSubmissionDetailForIdOrError(UUID id) {
    return getSubmissionDetail(id)
        .orElseThrow(() -> new EntityNotFoundException("Submission detail with id %s not found".formatted(id)));
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
}
