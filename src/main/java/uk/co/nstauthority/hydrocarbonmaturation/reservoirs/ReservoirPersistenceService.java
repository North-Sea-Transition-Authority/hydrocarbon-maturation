package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
