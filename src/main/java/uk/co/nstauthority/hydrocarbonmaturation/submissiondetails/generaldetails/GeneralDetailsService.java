package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class GeneralDetailsService {

  private final GeneralDetailsFormValidator generalDetailsFormValidator;

  @Autowired
  public GeneralDetailsService(GeneralDetailsFormValidator generalDetailsFormValidator) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
  }

  BindingResult validate(GeneralDetailsForm form, BindingResult bindingResult) {
    generalDetailsFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
