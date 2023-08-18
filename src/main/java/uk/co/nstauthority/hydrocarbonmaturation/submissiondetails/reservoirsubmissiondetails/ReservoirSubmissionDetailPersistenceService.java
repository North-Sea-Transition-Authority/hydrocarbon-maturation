package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ReservoirSubmissionDetailPersistenceService {

  private final ReservoirSubmissionDetailRepository reservoirSubmissionDetailRepository;
  private final ReservoirPersistenceService reservoirPersistenceService;

  @Autowired
  public ReservoirSubmissionDetailPersistenceService(
      ReservoirSubmissionDetailRepository reservoirSubmissionDetailRepository,
      ReservoirPersistenceService reservoirPersistenceService) {
    this.reservoirSubmissionDetailRepository = reservoirSubmissionDetailRepository;
    this.reservoirPersistenceService = reservoirPersistenceService;
  }

  public List<ReservoirSubmissionDetail> getAllReservoirSubmissionDetailsForSubmissionDetail(SubmissionDetail submissionDetail) {
    return reservoirSubmissionDetailRepository.findBySubmissionDetailOrderByReservoir_Name(submissionDetail);
  }

  public boolean submissionHasReservoirs(SubmissionDetail submissionDetail) {
    return reservoirSubmissionDetailRepository.existsBySubmissionDetail(submissionDetail);
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

  @Transactional
  public void saveReservoirSubmissionDetail(Reservoir reservoir, SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail.setReservoir(reservoir);
    reservoirSubmissionDetail.setSubmissionDetail(submissionDetail);
    reservoirSubmissionDetailRepository.save(reservoirSubmissionDetail);
  }

  public Optional<ReservoirSubmissionDetail> getReservoirSubmissionDetail(UUID reservoirSubmissionDetailId) {
    return reservoirSubmissionDetailRepository.findById(reservoirSubmissionDetailId);
  }

  public ReservoirSubmissionDetail getReservoirSubmissionDetailOrThrow(UUID reservoirSubmissionDetailId) {
    return getReservoirSubmissionDetail(reservoirSubmissionDetailId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "Could not find reservoir submission detail for id %s".formatted(reservoirSubmissionDetailId)));
  }

  public ReservoirSubmissionDetail getReservoirSubmissionDetail(SubmissionDetail submissionDetail) {
    return getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail).stream().findFirst()
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "Could not find reservoir submission detail for submission detail %s".formatted(submissionDetail.getId())));
  }

  public List<ReservoirSubmissionDetail> getReservoirSubmissionDetails(Reservoir reservoir) {
    return reservoirSubmissionDetailRepository.findAllByReservoir(reservoir);
  }

  public List<ReservoirSubmissionDetail> getReservoirSubmissionDetailsForReservoirIds(Set<String> reservoirIds) {
    var reservoirs =  reservoirIds
        .stream()
        .map(reservoirId -> reservoirPersistenceService.getReservoirOrThrow(UUID.fromString(reservoirId)));

    return reservoirs
        .map(this::getReservoirSubmissionDetails)
        .flatMap(Collection::stream)
        .toList();
  }
}
