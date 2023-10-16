package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class GeneralFluidPropertyFormService {

  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @Autowired
  GeneralFluidPropertyFormService(GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService) {
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
  }

  GeneralFluidPropertyForm getGeneralFluidPropertyForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail)
        .map(this::generalFluidPropertyEntityToForm)
        .orElse(new GeneralFluidPropertyForm());
  }

  private GeneralFluidPropertyForm generalFluidPropertyEntityToForm(GeneralFluidProperty generalFluidProperty) {
    var form = new GeneralFluidPropertyForm();

    form.setDatumDepth(DecimalInput.createDecimalInput(generalFluidProperty.getDatumDepth()));

    if (generalFluidProperty.getPressureAtDatumDepth() != null) {
      form.setPressureAtDatumDepth(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getPressureAtDatumDepth())));
    }

    if (generalFluidProperty.getInitialPressure() != null) {
      form.setInitialPressure(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getInitialPressure())));
    }

    if (generalFluidProperty.getSaturationPressureAtDatumDepth() != null) {
      form.setSaturationPressureAtDatumDepth(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getSaturationPressureAtDatumDepth())));
    }

    if (generalFluidProperty.getTemperatureAtDatumDepth() != null) {
      form.setTemperatureAtDatumDepth(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getTemperatureAtDatumDepth())));
    }

    if (generalFluidProperty.getInitialTemperature() != null) {
      form.setInitialTemperature(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getInitialTemperature())));
    }

    if (generalFluidProperty.getWaterSalinity() != null) {
      form.setWaterSalinity(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getWaterSalinity())));
    }

    return form;
  }
}
