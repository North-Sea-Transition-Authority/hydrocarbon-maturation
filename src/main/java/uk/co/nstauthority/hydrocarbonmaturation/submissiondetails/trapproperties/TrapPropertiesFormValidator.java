package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.CoordinateInputValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class TrapPropertiesFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return TrapPropertiesForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var trapPropertiesForm = (TrapPropertiesForm) form;

    CoordinateInputValidationUtil.validateOptionalCoordinateInputForm(
        errors,
        trapPropertiesForm.getCoordinateInputForm(),
        "coordinateInputForm.latitudeCoordinates",
        "coordinateInputForm.longitudeCoordinates",
        null,
        null
    );

    var depthToCrestForm = trapPropertiesForm.getDepthToCrest();
    ProbabilityDistributionValidationUtil.validatePartial(
        depthToCrestForm,
        errors,
        "depthToCrest",
        "Depth to crest");

    var depthToSpillPointForm = trapPropertiesForm.getDepthToSpillPoint();
    ProbabilityDistributionValidationUtil.validatePartial(
        depthToSpillPointForm,
        errors,
        "depthToSpillPoint",
        "Depth to spill-point");

    var productiveAreaForm = trapPropertiesForm.getProductiveArea();
    ProbabilityDistributionValidationUtil.validatePartial(
        productiveAreaForm,
        errors,
        "productiveArea",
        "Productive area"
    );

    var prospectDepthMap = trapPropertiesForm.getProspectDepthMap();

    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one prospect depth map")
        .isPartiallyValidated()
        .validate(errors, prospectDepthMap, "prospectDepthMap");
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

    CoordinateInputValidationUtil.validateMandatoryCoordinateInputForm(
        errors,
        trapPropertiesForm.getCoordinateInputForm(),
        "coordinateInputForm.latitudeCoordinates",
        "coordinateInputForm.longitudeCoordinates",
        null,
        null,
        "coordinateInputForm.longitudeHemisphere"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "datum",
        "datum.required",
        "Select the datum"
    );

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "centreOfInterestQuadrantBlock",
        "centreOfInterestQuadrantBlock.required",
        "Enter the quadrant and block the centre of interest is within"
    );

    ValidationUtils.rejectIfEmpty(errors, "depthToCrest.probabilityDistributionType",
        "depthToCrest.probabilityDistributionType.required",
        "Select a depth to crest distribution used");

    var depthToCrestForm = trapPropertiesForm.getDepthToCrest();
    ProbabilityDistributionValidationUtil.validateFull(
        depthToCrestForm,
        errors,
        "depthToCrest",
        "Depth to crest"
    );

    ValidationUtils.rejectIfEmpty(errors, "depthToSpillPoint.probabilityDistributionType",
        "depthToSpillPoint.probabilityDistributionType.required",
        "Select a depth to spill-point distribution used");

    var depthToSpillPointForm = trapPropertiesForm.getDepthToSpillPoint();
    ProbabilityDistributionValidationUtil.validateFull(
        depthToSpillPointForm,
        errors,
        "depthToSpillPoint",
        "Depth to spill-point"
    );

    ValidationUtils.rejectIfEmpty(errors, "productiveArea.probabilityDistributionType",
        "productiveArea.probabilityDistributionType.required",
        "Select a productive area distribution used");

    var productiveAreaForm = trapPropertiesForm.getProductiveArea();
    ProbabilityDistributionValidationUtil.validateFull(
        productiveAreaForm,
        errors,
        "productiveArea",
        "Productive area"
    );

    var prospectDepthMap = trapPropertiesForm.getProspectDepthMap();

    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one prospect depth map")
        .withMinimumNumberOfFiles(1, "Upload a prospect depth map")
        .validate(errors, prospectDepthMap, "prospectDepthMap");
  }
}
