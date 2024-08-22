package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class ResourceTypeSelectService {
  private final ResourceTypeSelectFormValidator resourceTypeSelectFormValidator;

  @Autowired
  ResourceTypeSelectService(ResourceTypeSelectFormValidator resourceTypeSelectFormValidator) {
    this.resourceTypeSelectFormValidator = resourceTypeSelectFormValidator;
  }

  BindingResult validate(ResourceTypeSelectForm form, BindingResult bindingResult) {
    resourceTypeSelectFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
