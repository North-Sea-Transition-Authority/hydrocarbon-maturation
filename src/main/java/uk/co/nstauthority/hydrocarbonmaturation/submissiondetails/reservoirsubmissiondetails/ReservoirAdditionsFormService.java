package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ReservoirAdditionsFormService {
  private final ReservoirAdditionsRepository reservoirAdditionsRepository;

  @Autowired
  public ReservoirAdditionsFormService(ReservoirAdditionsRepository reservoirAdditionsRepository) {
    this.reservoirAdditionsRepository = reservoirAdditionsRepository;
  }

  public void updateReservoirAdditionStatus(SubmissionDetail submissionDetail, ReservoirAdditionStatus reservoirAdditionStatus) {
    var reservoirAdditions = reservoirAdditionsRepository.findBySubmissionDetail(submissionDetail)
            .orElse(new ReservoirAdditions());
    reservoirAdditions.setSubmissionDetail(submissionDetail);
    reservoirAdditions.setReservoirAdditionStatus(reservoirAdditionStatus);
    reservoirAdditionsRepository.save(reservoirAdditions);
  }

  public ReservoirAdditionForm getReservoirAdditionsToForm(SubmissionDetail submissionDetail) {
    var reservoirAdditionStatus = reservoirAdditionsRepository.findBySubmissionDetail(submissionDetail)
        .map(ReservoirAdditions::getReservoirAdditionStatus)
        .orElse(null);
    return new ReservoirAdditionForm(reservoirAdditionStatus);
  }
}
