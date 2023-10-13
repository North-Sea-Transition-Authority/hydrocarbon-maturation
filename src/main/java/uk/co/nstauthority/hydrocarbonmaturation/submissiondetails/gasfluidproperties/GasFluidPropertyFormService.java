package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class GasFluidPropertyFormService {

  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @Autowired
  GasFluidPropertyFormService(GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService) {
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
  }

  GasFluidPropertyForm getGasFluidPropertyForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail)
        .map(this::gasFluidPropertyEntityToForm)
        .orElse(new GasFluidPropertyForm());
  }

  private GasFluidPropertyForm gasFluidPropertyEntityToForm(GasFluidProperty gasFluidProperty) {
    var form = new GasFluidPropertyForm();

    form.setGasCo2(DecimalInput.createDecimalInput(gasFluidProperty.getGasCo2()));
    form.setGasH2s(DecimalInput.createDecimalInput(gasFluidProperty.getGasH2s()));
    form.setGasN2(DecimalInput.createDecimalInput(gasFluidProperty.getGasN2()));
    form.setCondensatePresent(gasFluidProperty.getCondensatePresent());
    form.setGasSpecificGravity(DecimalInput.createDecimalInput(gasFluidProperty.getGasSpecificGravity()));
    form.setAdviseOtherGasesConcentration(gasFluidProperty.getAdviseOtherGasesConcentration());

    if (BooleanUtils.isTrue(gasFluidProperty.getCondensatePresent())) {
      form.setCondensateGravity(DecimalInput.createDecimalInput(gasFluidProperty.getCondensateGravity()));
      form.setCondensateViscosity(DecimalInput.createDecimalInput(gasFluidProperty.getCondensateViscosity()));
      form.setCondensateSulphur(DecimalInput.createDecimalInput(gasFluidProperty.getCondensateSulphur()));
      form.setCondensateWax(DecimalInput.createDecimalInput(gasFluidProperty.getCondensateWax()));
      form.setCondensateOtherInclWax(DecimalInput.createDecimalInput(gasFluidProperty.getCondensateOtherInclWax()));
    }
    return form;

  }
}
