package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class SubmissionSummaryService {

  private final List<SummarySectionService<SubmissionDetail>> summarySectionServices;

  @Autowired
  public SubmissionSummaryService(List<SummarySectionService<SubmissionDetail>> summarySectionServices) {
    this.summarySectionServices = summarySectionServices;
  }

  public List<SummarySection> getSummarySections(SubmissionDetail submissionDetail) {
    return summarySectionServices.stream()
        .map(summarySectionService -> summarySectionService.getSummarySection(submissionDetail))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(SummarySection::displayOrder))
        .toList();
  }
}
