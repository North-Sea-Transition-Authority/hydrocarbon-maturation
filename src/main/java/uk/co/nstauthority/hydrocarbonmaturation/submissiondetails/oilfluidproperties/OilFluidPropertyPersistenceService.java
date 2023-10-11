package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class OilFluidPropertyPersistenceService {

  private final OilFluidPropertyRepository oilFluidPropertyRepository;

  @Autowired
  OilFluidPropertyPersistenceService(OilFluidPropertyRepository oilFluidPropertyRepository) {
    this.oilFluidPropertyRepository = oilFluidPropertyRepository;
  }

  @Transactional
  public void saveOilFluidProperty(OilFluidPropertyForm oilFluidPropertyForm,
                                   ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var oilFluidProperty = getOilFluidProperty(reservoirSubmissionDetail).orElse(new OilFluidProperty());
    oilFluidProperty.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    oilFluidProperty.setOilGravity(oilFluidPropertyForm.getOilGravity().createIntegerOrNull());
    oilFluidProperty.setOilSulphur(oilFluidPropertyForm.getOilSulphur().createIntegerOrNull());
    oilFluidProperty.setOilViscosity(oilFluidPropertyForm.getOilViscosity().createBigDecimalOrNull());
    oilFluidProperty.setOilWax(oilFluidPropertyForm.getOilWax().createBigDecimalOrNull());
    oilFluidProperty.setOilOtherWaxAppearanceTemperature(
        oilFluidPropertyForm.getOilOtherWaxAppearanceTemperature().createIntegerOrNull());
    oilFluidPropertyRepository.save(oilFluidProperty);
  }

  Optional<OilFluidProperty> getOilFluidProperty(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilFluidPropertyRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public void clearAllOilFluidProperties(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    oilFluidPropertyRepository.deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}
