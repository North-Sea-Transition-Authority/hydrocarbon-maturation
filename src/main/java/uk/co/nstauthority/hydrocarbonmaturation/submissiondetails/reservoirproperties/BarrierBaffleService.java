package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BarrierBaffleService {
  private final BarrierBaffleDetailRepository barrierBaffleDetailRepository;

  @Autowired
  public BarrierBaffleService(BarrierBaffleDetailRepository barrierBaffleDetailRepository) {
    this.barrierBaffleDetailRepository = barrierBaffleDetailRepository;
  }

  public Set<BarrierBaffleDetail> getVerticalBarrierBafflesForReservoirProperty(ReservoirProperty reservoirProperty) {
    return barrierBaffleDetailRepository
        .findAllByReservoirPropertyAndVerticalLateralType(reservoirProperty, VerticalLateralType.VERTICAL);
  }

  public Set<BarrierBaffleDetail> getLateralBarrierBafflesForReservoirProperty(ReservoirProperty reservoirProperty) {
    return barrierBaffleDetailRepository
        .findAllByReservoirPropertyAndVerticalLateralType(reservoirProperty, VerticalLateralType.LATERAL);
  }
}
