package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class ReservoirSummaryService {

  private final List<SummarySectionService<ReservoirSubmissionDetail>> summarySectionServices;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirSubmissionSectionService reservoirSubmissionSectionService;

  @Autowired
  public ReservoirSummaryService(List<SummarySectionService<ReservoirSubmissionDetail>> summarySectionServices,
                                 ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                 ReservoirSubmissionSectionService reservoirSubmissionSectionService) {
    this.summarySectionServices = summarySectionServices;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirSubmissionSectionService = reservoirSubmissionSectionService;
  }

  public List<ReservoirSummary> getReservoirSummaries(SubmissionDetail submissionDetail, User user) {
    var reservoirs = reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail
    );
    return reservoirs.stream()
        .map(reservoirSubmissionDetail -> new ReservoirSummary(
            reservoirSubmissionDetail.getReservoir().getName(),
            getSummarySections(reservoirSubmissionDetail, user)))
        .sorted(Comparator.comparing(ReservoirSummary::reservoirName))
        .toList();
  }

  public ReservoirSummary getReservoirSummaryForIndividualReservoir(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                                    User user) {
    return new ReservoirSummary(
        reservoirSubmissionDetail.getReservoir().getName(),
        getSummarySections(reservoirSubmissionDetail, user));
  }

  private List<SummarySection> getSummarySections(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    // get a list of the summary section classes we are not interested in
    var classesOfSectionsToRemove =
        reservoirSubmissionSectionService.getSummarySectionClassesNotForSelection(reservoirSubmissionDetail);

    // remove the sections we are not interested in and get the summary section
    return summarySectionServices.stream()
        // if the service class is not in the sections to be removed then keep
        .filter(service -> !classesOfSectionsToRemove.contains(service.getClass()))
        .map(summarySectionService -> summarySectionService.getSummarySection(reservoirSubmissionDetail, user))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(SummarySection::displayOrder))
        .toList();
  }
}
