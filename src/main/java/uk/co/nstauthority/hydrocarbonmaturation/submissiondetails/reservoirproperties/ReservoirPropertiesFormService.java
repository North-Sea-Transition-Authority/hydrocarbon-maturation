package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
class ReservoirPropertiesFormService {

  private final ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  @Autowired
  public ReservoirPropertiesFormService(ReservoirPropertiesFormValidator reservoirPropertiesFormValidator) {
    this.reservoirPropertiesFormValidator = reservoirPropertiesFormValidator;
  }

  BindingResult validate(ReservoirPropertiesForm form, BindingResult bindingResult) {
    reservoirPropertiesFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
