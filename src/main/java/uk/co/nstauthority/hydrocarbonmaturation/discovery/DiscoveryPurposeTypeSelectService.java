package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class DiscoveryPurposeTypeSelectService {

  private final DiscoveryPurposeTypeSelectFormValidator discoveryPurposeTypeSelectFormValidator;

  @Autowired
  public DiscoveryPurposeTypeSelectService(
      DiscoveryPurposeTypeSelectFormValidator discoveryPurposeTypeSelectFormValidator) {
    this.discoveryPurposeTypeSelectFormValidator = discoveryPurposeTypeSelectFormValidator;
  }

  BindingResult validate(DiscoveryPurposeTypeSelectForm form, BindingResult bindingResult) {
    discoveryPurposeTypeSelectFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
