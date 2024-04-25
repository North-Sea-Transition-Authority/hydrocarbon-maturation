package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Service
public class UndevelopedDiscoveryService {

  private final UndevelopedDiscoveryRepository undevelopedDiscoveryRepository;

  @Autowired
  public UndevelopedDiscoveryService(UndevelopedDiscoveryRepository undevelopedDiscoveryRepository) {
    this.undevelopedDiscoveryRepository = undevelopedDiscoveryRepository;
  }

  public Optional<UndevelopedDiscovery> getUndevelopedDiscoveryForResource(Resource resource) {
    return undevelopedDiscoveryRepository.findByResource(resource);
  }

  public UndevelopedDiscoveryTrackingDetailsForm discoveryToTrackingDetailsForm(UndevelopedDiscovery undevelopedDiscovery) {
    var form = new UndevelopedDiscoveryTrackingDetailsForm();
    form.setIsUndevelopedDiscovery(undevelopedDiscovery.getIsUndevelopedDiscovery());
    return form;
  }

  @Transactional
  public void saveUndevelopedDiscoveryTrackingDetails(Resource resource,
                                                      UndevelopedDiscoveryTrackingDetailsForm trackingDetailsForm) {
    var undevelopedDiscovery = getUndevelopedDiscoveryForResource(resource).orElseGet(UndevelopedDiscovery::new);
    undevelopedDiscovery.setIsUndevelopedDiscovery(trackingDetailsForm.getIsUndevelopedDiscovery());

    if (undevelopedDiscovery.getResource() == null) {
      undevelopedDiscovery.setResource(resource);
    }

    undevelopedDiscoveryRepository.save(undevelopedDiscovery);
  }
}
