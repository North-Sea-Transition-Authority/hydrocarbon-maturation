package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class ReservoirSubmissionDetailPersistenceService {

  private final ReservoirSubmissionDetailRepository reservoirSubmissionDetailRepository;
  private final ReservoirPersistenceService reservoirPersistenceService;

  @Autowired
  public ReservoirSubmissionDetailPersistenceService(
      ReservoirSubmissionDetailRepository reservoirSubmissionDetailRepository,
      ReservoirPersistenceService reservoirPersistenceService) {
    this.reservoirSubmissionDetailRepository = reservoirSubmissionDetailRepository;
    this.reservoirPersistenceService = reservoirPersistenceService;
  }

  List<ReservoirSubmissionDetail> getAllReservoirsForSubmissionDetail(SubmissionDetail submissionDetail) {
    return reservoirSubmissionDetailRepository.findBySubmissionDetailOrderByReservoir_Name(submissionDetail);
  }

  @Transactional
  void saveReservoirSubmissionDetails(ReservoirListForm form, SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetails = new ArrayList<ReservoirSubmissionDetail>();
    form.getReservoirs().stream().filter(Objects::nonNull).forEach(reservoir -> {
      var newReservoir = reservoirPersistenceService.createReservoir(reservoir);
      var newReservoirSubmissionDetail = new ReservoirSubmissionDetail();
      newReservoirSubmissionDetail.setReservoir(newReservoir);
      newReservoirSubmissionDetail.setSubmissionDetail(submissionDetail);
      reservoirSubmissionDetails.add(newReservoirSubmissionDetail);
    });
    reservoirSubmissionDetailRepository.saveAll(reservoirSubmissionDetails);
  }
}
