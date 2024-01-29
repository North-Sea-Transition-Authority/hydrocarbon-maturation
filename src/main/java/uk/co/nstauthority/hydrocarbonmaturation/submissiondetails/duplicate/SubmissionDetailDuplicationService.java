package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class SubmissionDetailDuplicationService {
  private final List<SubmissionSectionDuplicationService> submissionTaskDuplicationServiceList;
  private final List<ReservoirSectionDuplicationService> reservoirSectionDuplicationServiceList;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Autowired
  SubmissionDetailDuplicationService(List<SubmissionSectionDuplicationService> submissionTaskDuplicationServiceList,
                                     List<ReservoirSectionDuplicationService> reservoirSectionDuplicationServiceList,
                                     ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.submissionTaskDuplicationServiceList = submissionTaskDuplicationServiceList;
    this.reservoirSectionDuplicationServiceList = reservoirSectionDuplicationServiceList;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @Transactional
  public void duplicateSubmissionDetailSections(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    submissionTaskDuplicationServiceList.forEach(submissionTaskDuplicationService ->
        submissionTaskDuplicationService.duplicateSubmissionSection(
            oldSubmissionDetail,
            newSubmissionDetail
        )
    );

    var newReservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(newSubmissionDetail);

    if (!newReservoirSubmissionDetails.isEmpty()) {
      var oldReservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
          .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail);

      var newReservoirSubmissionDetailsMap = newReservoirSubmissionDetails
          .stream()
          .collect(Collectors.toMap(ReservoirSubmissionDetail::getReservoir, Function.identity()));

      oldReservoirSubmissionDetails.forEach(oldReservoirSubmissionDetail ->
          reservoirSectionDuplicationServiceList.forEach(reservoirSectionDuplicationService -> {
                var newReservoirSubmissionDetail = newReservoirSubmissionDetailsMap
                    .get(oldReservoirSubmissionDetail.getReservoir());
                if (newReservoirSubmissionDetail != null) {
                  reservoirSectionDuplicationService.duplicateReservoirSection(
                      oldReservoirSubmissionDetail,
                      newReservoirSubmissionDetail
                  );
                }
              }
          )
      );
    }
  }
}
