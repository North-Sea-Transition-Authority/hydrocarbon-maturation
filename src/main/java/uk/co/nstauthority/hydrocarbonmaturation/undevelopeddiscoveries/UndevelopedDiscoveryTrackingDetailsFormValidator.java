package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Service
public class UndevelopedDiscoveryTrackingDetailsFormValidator {
  public boolean isValidTrackingDetails(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "isUndevelopedDiscovery",
        "isUndevelopedDiscovery.required",
        "Select if this discovery should be included in the undeveloped discoveries tracker"
    );

    return !errors.hasErrors();
  }
}
