package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class ProspectPurposeTypeSelectService {

  private final ProspectPurposeTypeSelectFormValidator prospectPurposeTypeSelectFormValidator;

  @Autowired
  public ProspectPurposeTypeSelectService(ProspectPurposeTypeSelectFormValidator prospectPurposeTypeSelectFormValidator) {
    this.prospectPurposeTypeSelectFormValidator = prospectPurposeTypeSelectFormValidator;
  }

  BindingResult validate(ProspectPurposeTypeSelectForm form, BindingResult bindingResult) {
    prospectPurposeTypeSelectFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
