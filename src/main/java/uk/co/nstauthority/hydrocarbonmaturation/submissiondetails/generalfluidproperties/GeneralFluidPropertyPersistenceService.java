package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class GeneralFluidPropertyPersistenceService {

  private final GeneralFluidPropertyRepository generalFluidPropertyRepository;

  @Autowired
  GeneralFluidPropertyPersistenceService(GeneralFluidPropertyRepository generalFluidPropertyRepository) {
    this.generalFluidPropertyRepository = generalFluidPropertyRepository;
  }

  @Transactional
  public void saveGeneralFluidProperty(GeneralFluidPropertyForm generalFluidPropertyForm, SubmissionDetail submissionDetail) {
    var generalFluidProperty = getGeneralFluidProperty(submissionDetail)
        .orElse(new GeneralFluidProperty());

    generalFluidProperty.setSubmissionDetail(submissionDetail);
    generalFluidProperty.setDatumDepth(generalFluidPropertyForm.getDatumDepth().createBigDecimalOrNull());
    generalFluidProperty.setPressureAtDatumDepth(
        generalFluidPropertyForm.getPressureAtDatumDepth().createIntegerOrNull());
    generalFluidProperty.setSaturationPressureAtDatumDepth(
        generalFluidPropertyForm.getSaturationPressureAtDatumDepth().createIntegerOrNull());
    generalFluidProperty.setWaterSalinity(
        generalFluidPropertyForm.getWaterSalinity().createIntegerOrNull());
    generalFluidProperty.setTemperatureAtDatumDepth(
        generalFluidPropertyForm.getTemperatureAtDatumDepth().createIntegerOrNull());

    generalFluidPropertyRepository.save(generalFluidProperty);
  }

  Optional<GeneralFluidProperty> getGeneralFluidProperty(SubmissionDetail submissionDetail) {
    return generalFluidPropertyRepository.findBySubmissionDetail(submissionDetail);
  }
}
