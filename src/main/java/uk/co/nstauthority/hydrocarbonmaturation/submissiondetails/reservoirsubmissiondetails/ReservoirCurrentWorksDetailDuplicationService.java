package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;

@Service
public class ReservoirCurrentWorksDetailDuplicationService implements ReservoirSectionDuplicationService {

  private final ReservoirCurrentWorksDetailRepository reservoirCurrentWorksDetailRepository;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  public ReservoirCurrentWorksDetailDuplicationService(
      ReservoirCurrentWorksDetailRepository reservoirCurrentWorksDetailRepository,
      ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.reservoirCurrentWorksDetailRepository = reservoirCurrentWorksDetailRepository;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    reservoirCurrentWorksDetailRepository.findByReservoirSubmissionDetail(oldReservoirSubmissionDetail)
        .ifPresent(reservoirCurrentWorksDetail -> {
              var developedUnderFuturePhase = reservoirCurrentWorksDetail.getIsReservoirBeingDevelopedUnderThisPhase();
              if (BooleanUtils.isTrue(developedUnderFuturePhase)) {
                reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
                    newReservoirSubmissionDetail,
                    reservoirCurrentWorksDetail,
                    ReservoirCurrentWorksDetail.class
                );
              }
            }
        );
  }
}
