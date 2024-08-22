package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class FieldSummaryService {

  private final ReservoirSummaryService reservoirSummaryService;

  @Autowired
  public FieldSummaryService(ReservoirSummaryService reservoirSummaryService) {
    this.reservoirSummaryService = reservoirSummaryService;
  }

  public List<ReservoirSummary> getReservoirSummariesFromSubmissionDetail(SubmissionDetail submissionDetail, User user) {
    return reservoirSummaryService.getReservoirSummaries(submissionDetail, user);
  }
}
