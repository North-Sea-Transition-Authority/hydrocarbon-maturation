package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

@Service
public class ReservoirPersistenceService {

  private final ReservoirRepository reservoirRepository;

  @Autowired
  public ReservoirPersistenceService(ReservoirRepository reservoirRepository) {
    this.reservoirRepository = reservoirRepository;
  }

  @Transactional
  public Reservoir createReservoir(String name) {
    var reservoir = new Reservoir();
    reservoir.setName(name);
    reservoirRepository.save(reservoir);
    return reservoir;
  }

  @Transactional
  public void saveReservoir(Reservoir reservoir, String name) {
    reservoir.setName(name);
    reservoirRepository.save(reservoir);
  }

  public Optional<Reservoir> getReservoir(UUID reservoirId) {
    return reservoirRepository.findById(reservoirId);
  }

  public Reservoir getReservoirOrThrow(UUID reservoirId) {
    return getReservoir(reservoirId)
        .orElseThrow(() -> new RemiEntityNotFoundException("No reservoir found for id %s".formatted(reservoirId)));
  }

  public List<Reservoir> getReservoirs(List<UUID> reservoirIds) {
    return reservoirRepository.findAllByIdIn(reservoirIds);
  }
}