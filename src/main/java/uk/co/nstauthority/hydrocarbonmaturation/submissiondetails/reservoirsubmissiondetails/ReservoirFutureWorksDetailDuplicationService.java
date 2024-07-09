package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;

@Service
public class ReservoirFutureWorksDetailDuplicationService implements ReservoirSectionDuplicationService {

  private final ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  public ReservoirFutureWorksDetailDuplicationService(
      ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository,
      ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.reservoirFutureWorksDetailRepository = reservoirFutureWorksDetailRepository;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(oldReservoirSubmissionDetail)
        .ifPresent(reservoirFutureWorksDetail -> {
              var developedUnderFuturePhase = reservoirFutureWorksDetail.getIsReservoirBeingDevelopedUnderFuturePhase();
              if (BooleanUtils.isTrue(developedUnderFuturePhase)) {
                reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
                    newReservoirSubmissionDetail,
                    reservoirFutureWorksDetail,
                    ReservoirFutureWorksDetail.class
                );
              }
            }
        );
  }
}
