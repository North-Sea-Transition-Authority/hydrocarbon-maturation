package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OilFluidPropertyFormService implements SubmissionFormService {

  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  private final OilFluidPropertyFormValidator oilFluidPropertyFormValidator;

  @Autowired
  OilFluidPropertyFormService(OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService,
                              OilFluidPropertyFormValidator oilFluidPropertyFormValidator) {
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
    this.oilFluidPropertyFormValidator = oilFluidPropertyFormValidator;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    oilFluidPropertyFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  OilFluidPropertyForm getOilFluidPropertyForm(SubmissionDetail submissionDetail) {
    return oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail)
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
