package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class OilFluidPropertyFormService {

  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @Autowired
  OilFluidPropertyFormService(OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService) {
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
  }

  OilFluidPropertyForm getOilFluidPropertyForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail)
        .map(this::oilFluidPropertyEntityToForm)
        .orElse(new OilFluidPropertyForm());
  }

  private OilFluidPropertyForm oilFluidPropertyEntityToForm(OilFluidProperty oilFluidProperty) {
    var form = new OilFluidPropertyForm();

    if (oilFluidProperty.getOilGravity() != null) {
      form.setOilGravity(DecimalInput.createDecimalInput(oilFluidProperty.getOilGravity()));
    }

    if (oilFluidProperty.getOilSulphur() != null) {
      form.setOilSulphur(DecimalInput.createDecimalInput(oilFluidProperty.getOilSulphur()));
    }

    form.setOilViscosity(DecimalInput.createDecimalInput(oilFluidProperty.getOilViscosity()));
    form.setOilWax(DecimalInput.createDecimalInput(oilFluidProperty.getOilWax()));
    if (oilFluidProperty.getOilOtherWaxAppearanceTemperature() != null) {
      form.setOilOtherWaxAppearanceTemperature(
          DecimalInput.createDecimalInput(oilFluidProperty.getOilOtherWaxAppearanceTemperature()));
    }
    return form;
  }
}
