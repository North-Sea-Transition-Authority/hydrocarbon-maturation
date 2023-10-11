package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface BarrierBaffleDetailRepository extends CrudRepository<BarrierBaffleDetail, UUID> {
  Set<BarrierBaffleDetail> findAllByReservoirPropertyAndVerticalLateralType(ReservoirProperty reservoirProperty,
                                                                            VerticalLateralType verticalLateralType);

  void deleteAllByReservoirPropertyAndVerticalLateralType(ReservoirProperty reservoirProperty,
                                                          VerticalLateralType type);
}
