package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class GeneralFluidPropertyPersistenceService {

  private final GeneralFluidPropertyRepository generalFluidPropertyRepository;

  @Autowired
  GeneralFluidPropertyPersistenceService(GeneralFluidPropertyRepository generalFluidPropertyRepository) {
    this.generalFluidPropertyRepository = generalFluidPropertyRepository;
  }

  @Transactional
  public void saveGeneralFluidProperty(GeneralFluidPropertyForm generalFluidPropertyForm,
                                       ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var generalFluidProperty = getGeneralFluidProperty(reservoirSubmissionDetail)
        .orElse(new GeneralFluidProperty());

    generalFluidProperty.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    generalFluidProperty.setDatumDepth(generalFluidPropertyForm.getDatumDepth().createBigDecimalOrNull());
    generalFluidProperty.setPressureAtDatumDepth(
        generalFluidPropertyForm.getPressureAtDatumDepth().createIntegerOrNull());
    generalFluidProperty.setInitialPressure(
        generalFluidPropertyForm.getInitialPressure().createIntegerOrNull());
    generalFluidProperty.setSaturationPressureAtDatumDepth(
        generalFluidPropertyForm.getSaturationPressureAtDatumDepth().createIntegerOrNull());
    generalFluidProperty.setWaterSalinity(
        generalFluidPropertyForm.getWaterSalinity().createIntegerOrNull());
    generalFluidProperty.setTemperatureAtDatumDepth(
        generalFluidPropertyForm.getTemperatureAtDatumDepth().createIntegerOrNull());
    generalFluidProperty.setInitialTemperature(
        generalFluidPropertyForm.getInitialTemperature().createIntegerOrNull());

    generalFluidPropertyRepository.save(generalFluidProperty);
  }

  Optional<GeneralFluidProperty> getGeneralFluidProperty(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return generalFluidPropertyRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}
