package uk.co.nstauthority.hydrocarbonmaturation.field;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
public class FieldFormValidator implements Validator {

  private final FieldLicenceService fieldLicenceService;

  public FieldFormValidator(FieldLicenceService fieldLicenceService) {
    this.fieldLicenceService = fieldLicenceService;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return FieldForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (FieldForm) target;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "legalFieldName", "legalFieldName.required", "Enter the legal field name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "correctedLegalFieldName", "correctedLegalFieldName.required", "Enter the corrected legal field name");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "commonNameForField", "commonNameForField.required", "Enter the common name for field");

    if (CollectionUtils.isEmpty(form.getAssociatedDiscoveries())) {
      errors.rejectValue("associatedDiscoverySelect", "associatedDiscoverySelect.notEmpty",
          "You must select at least one associated discovery");
    }

    validateLicences(form, errors);

    ValidationUtils.rejectIfEmpty(errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");

    if (form.getOnshoreOffshore() == OnshoreOffshore.OFFSHORE) {
      ValidationUtils.rejectIfEmpty(errors, "geographicalArea", "geographicalArea.required", "Select a geographical area");

      ValidationUtils.rejectIfEmpty(errors, "responsibleTeamType", "responsibleTeamType.required", "Select a responsible team");

      ValidationUtils.rejectIfEmpty(errors,
              "fieldLocatedInUkcs", "fieldLocatedInUkcs.required", "Select if this field is wholly located in the UKCS");
    } else {
      ValidationUtils.rejectIfEmpty(errors,
              "fieldLocatedInEngland", "fieldLocatedInEngland.required", "Select if this field is wholly located in England");
    }

    ValidationUtils.rejectIfEmpty(errors,
        "determinationStatus", "determinationStatus.required", "Select the determination status of field");

    if (form.getDeterminationStatus() == DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors,
          "parentDeterminedField", "parentDeterminedField.required", "Select the parent field");
    }

    ValidationUtils.rejectIfEmpty(errors,
        "infrastructureHost", "infrastructureHost.required", "Select yes if this field's infrastructure hosts other fields");

    ValidationUtils.rejectIfEmpty(errors,
        "hasConsultingAuthorities",
        "hasConsultingAuthorities.required",
        "Select if there are consulting authorities for the field");

    if (BooleanUtils.isTrue(form.getHasConsultingAuthorities())) {
      ValidationUtils.rejectIfEmpty(errors,
          "consultingAuthority",
          "consultingAuthority.required",
          "Select the consulting authorities"
      );
    }
  }

  private void validateLicences(FieldForm form, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "isOnLicence",
        "isOnLicence.required",
        "Select if this field is on a licence");


    var isOnLicence = BooleanUtils.isTrue(form.getIsOnLicence());
    if (isOnLicence && form.getLicences().isEmpty()) {
      errors.rejectValue(
          "licenceSelect",
          "licenceSelect.required",
          "You must select at least one extant licence"
      );

      return;
    }

    if (isOnLicence && !fieldLicenceService.areAllExtantLicences(form.getLicences())) {
      errors.rejectValue(
          "licenceSelect",
          "licenceSelect.notValid",
          "You must only select extant licences"
      );
    }
  }
}
