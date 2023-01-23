package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class GeneralFluidPropertyFormService implements SubmissionFormService {

  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;
  private final GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator;

  @Autowired
  GeneralFluidPropertyFormService(GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService,
                                  GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator) {
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
    this.generalFluidPropertyFormValidator = generalFluidPropertyFormValidator;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    generalFluidPropertyFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  GeneralFluidPropertyForm getGeneralFluidPropertyForm(SubmissionDetail submissionDetail) {
    return generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail)
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

    if (generalFluidProperty.getSaturationPressureAtDatumDepth() != null) {
      form.setSaturationPressureAtDatumDepth(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getSaturationPressureAtDatumDepth())));
    }

    if (generalFluidProperty.getTemperatureAtDatumDepth() != null) {
      form.setTemperatureAtDatumDepth(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getTemperatureAtDatumDepth())));
    }

    if (generalFluidProperty.getWaterSalinity() != null) {
      form.setWaterSalinity(DecimalInput.createDecimalInput(
          BigDecimal.valueOf(generalFluidProperty.getWaterSalinity())));
    }

    return form;
  }
}
