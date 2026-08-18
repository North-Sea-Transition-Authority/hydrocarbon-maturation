package uk.co.nstauthority.hydrocarbonmaturation.field.bootstrap;

import org.springframework.validation.BindingResult;

class BootstrapFieldValidationException extends RuntimeException {

  private final BindingResult bindingResult;

  BootstrapFieldValidationException(BindingResult bindingResult) {
    super("Field validation failed");
    this.bindingResult = bindingResult;
  }

  BindingResult getBindingResult() {
    return bindingResult;
  }
}
