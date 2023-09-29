package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class ReservoirSummaryService {

  private final List<SummarySectionService<ReservoirSubmissionDetail>> summarySectionServices;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Autowired
  public ReservoirSummaryService(List<SummarySectionService<ReservoirSubmissionDetail>> summarySectionServices,
                                 ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.summarySectionServices = summarySectionServices;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  public List<ReservoirSummary> getReservoirSummaries(SubmissionDetail submissionDetail) {
    var reservoirs = reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail
    );
    return reservoirs.stream()
        .map(reservoirSubmissionDetail -> new ReservoirSummary(
            reservoirSubmissionDetail.getReservoir().getName(),
            getSummarySections(reservoirSubmissionDetail)))
        .sorted(Comparator.comparing(ReservoirSummary::reservoirName))
        .toList();
  }

  private List<SummarySection> getSummarySections(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return summarySectionServices.stream()
        .map(summarySectionService -> summarySectionService.getSummarySection(reservoirSubmissionDetail))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(SummarySection::displayOrder))
        .toList();
  }
}
