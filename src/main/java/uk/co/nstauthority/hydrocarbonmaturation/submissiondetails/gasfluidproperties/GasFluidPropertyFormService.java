package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class GasFluidPropertyFormService implements SubmissionFormService {

  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;
  private final GasFluidPropertyFormValidator gasFluidPropertyFormValidator;

  @Autowired
  GasFluidPropertyFormService(GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService,
                              GasFluidPropertyFormValidator gasFluidPropertyFormValidator) {
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
    this.gasFluidPropertyFormValidator = gasFluidPropertyFormValidator;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    gasFluidPropertyFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  GasFluidPropertyForm getGasFluidPropertyForm(SubmissionDetail submissionDetail) {
    return gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail)
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
