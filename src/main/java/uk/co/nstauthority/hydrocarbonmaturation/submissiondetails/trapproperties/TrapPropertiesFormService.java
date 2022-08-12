package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInput;

@Service
class TrapPropertiesFormService {

  private final TrapPropertiesFormValidator trapPropertiesFormValidator;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Autowired
  TrapPropertiesFormService(TrapPropertiesFormValidator trapPropertiesFormValidator,
                            TrapPropertiesPersistenceService trapPropertiesPersistenceService) {
    this.trapPropertiesFormValidator = trapPropertiesFormValidator;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
  }

  BindingResult validate(TrapPropertiesForm form, BindingResult bindingResult) {
    trapPropertiesFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  TrapPropertiesForm getTrapPropertiesForm(SubmissionDetail submissionDetail) {
    return trapPropertiesPersistenceService.getTrapProperty(submissionDetail)
        .map(this::trapPropertyEntityToForm)
        .orElse(new TrapPropertiesForm());
  }

  private TrapPropertiesForm trapPropertyEntityToForm(TrapProperty trapProperty) {
    var form = new TrapPropertiesForm();
    form.setTrapType(trapProperty.getTrapType());
    form.setTrapDescription(trapProperty.getTrapDescription());
    form.setOtherTrapType(trapProperty.getOtherTrapType());

    var latitudeCoordinates = new CoordinateInput();
    latitudeCoordinates.setDegrees(new DecimalInput(trapProperty.getLatitudeDegrees().toString()));
    latitudeCoordinates.setMinutes(new DecimalInput(trapProperty.getLatitudeMinutes().toString()));
    latitudeCoordinates.setSeconds(new DecimalInput(trapProperty.getLatitudeSeconds().toString()));

    var longitudeCoordinates = new CoordinateInput();
    longitudeCoordinates.setDegrees(new DecimalInput(trapProperty.getLongitudeDegrees().toString()));
    longitudeCoordinates.setMinutes(new DecimalInput(trapProperty.getLongitudeMinutes().toString()));
    longitudeCoordinates.setSeconds(new DecimalInput(trapProperty.getLongitudeSeconds().toString()));

    form.setLatitudeCoordinates(latitudeCoordinates);
    form.setLongitudeCoordinates(longitudeCoordinates);
    form.setLongitudeHemisphere(trapProperty.getLongitudeHemisphere());

    return form;
  }
}
