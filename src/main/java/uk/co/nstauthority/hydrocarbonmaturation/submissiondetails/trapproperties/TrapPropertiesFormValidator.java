package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.CoordinateInputValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class TrapPropertiesFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return TrapPropertiesForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (TrapPropertiesForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var trapPropertiesForm = (TrapPropertiesForm) form;

    CoordinateInputValidationUtil.validateOptionalCoordinateInput(errors, trapPropertiesForm.getLatitudeCoordinates(),
        "latitudeCoordinates", "latitude");

    CoordinateInputValidationUtil.validateOptionalCoordinateInput(errors,
        trapPropertiesForm.getLongitudeCoordinates(), "longitudeCoordinates", "longitude");

    var depthToCrestForm = trapPropertiesForm.getDepthToCrest();
    ProbabilityDistributionValidationUtil.validate(
        depthToCrestForm,
        errors,
        "depthToCrest",
        "Depth to crest",
        ValidationType.PARTIAL);

    var depthToSpillPointForm = trapPropertiesForm.getDepthToSpillPoint();
    ProbabilityDistributionValidationUtil.validate(
        depthToSpillPointForm,
        errors,
        "depthToSpillPoint",
        "Depth to spill-point",
        ValidationType.PARTIAL);

    var productiveAreaForm = trapPropertiesForm.getProductiveArea();
    ProbabilityDistributionValidationUtil.validate(
        productiveAreaForm,
        errors,
        "productiveArea",
        "Productive area",
        ValidationType.PARTIAL
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "trapDescription", "trapDescription.required",
        "Enter a trap description");

    ValidationUtils.rejectIfEmpty(errors, "trapType", "trapType.required",
        "Select a trap type");

    var trapPropertiesForm = (TrapPropertiesForm) form;
    if (Objects.equals(trapPropertiesForm.getTrapType(), TrapType.OTHER)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherTrapType", "otherTrapType.required",
          "Enter an alternative trap type");
    }

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputNoHemisphere(errors,
        trapPropertiesForm.getLatitudeCoordinates(), "latitudeCoordinates", "latitude");

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputWithHemisphere(errors,
        trapPropertiesForm.getLongitudeCoordinates(), "longitudeCoordinates", "longitude",
        trapPropertiesForm.getLongitudeHemisphere(), "longitudeHemisphere");

    ValidationUtils.rejectIfEmpty(errors, "depthToCrest.probabilityDistributionType",
        "depthToCrest.probabilityDistributionType.required",
        "Select a depth to crest distribution used");

    var depthToCrestForm = trapPropertiesForm.getDepthToCrest();
    ProbabilityDistributionValidationUtil.validate(
        depthToCrestForm,
        errors,
        "depthToCrest",
        "Depth to crest",
        ValidationType.FULL
    );

    ValidationUtils.rejectIfEmpty(errors, "depthToSpillPoint.probabilityDistributionType",
        "depthToSpillPoint.probabilityDistributionType.required",
        "Select a depth to spill-point distribution used");

    var depthToSpillPointForm = trapPropertiesForm.getDepthToSpillPoint();
    ProbabilityDistributionValidationUtil.validate(
        depthToSpillPointForm,
        errors,
        "depthToSpillPoint",
        "Depth to spill-point",
        ValidationType.FULL
    );

    ValidationUtils.rejectIfEmpty(errors, "productiveArea.probabilityDistributionType",
        "productiveArea.probabilityDistributionType.required",
        "Select a productive area distribution used");

    var productiveAreaForm = trapPropertiesForm.getProductiveArea();
    ProbabilityDistributionValidationUtil.validate(
        productiveAreaForm,
        errors,
        "productiveArea",
        "Productive area",
        ValidationType.FULL
    );
  }
}
