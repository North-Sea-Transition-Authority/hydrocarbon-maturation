package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@Service
public class QuadrantAndBlockService {
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Autowired
  public QuadrantAndBlockService(QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService) {
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
  }

  public Map<String, String> getQuadrantAndBlocks(SubmissionDetail submissionDetail) {
    return quadrantAndBlockPersistenceService.getQuadrantBlocksBySubmissionDetail(submissionDetail)
      .stream()
      .sorted(new ReferenceBlockComparator())
      .collect(StreamUtil.toLinkedHashMap(
          QuadrantAndBlock::getQuadrantBlockReference,
          QuadrantAndBlock::getQuadrantBlockReference));
  }
}
