package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
class EnvironmentalActivityFormValidator {
  boolean isValid(EnvironmentalActivityForm form, Errors errors) {
    /*
     * Note that DateValidationUtil.validateDateIsTodayOrInThePast also validates if the date is well-formed.
     * Since all dates on this form are optional, we validate when any part of the date is provided, i.e.
     * when any part of the date is not null
     */
    if (ObjectUtils.anyNotNull(
        form.getSubmissionDateDay(),
        form.getSubmissionDateMonth(),
        form.getSubmissionDateYear())) {
      DateValidationUtil.validateDateIsTodayOrInThePast(
          "submissionDate",
          "submission date",
          form.getSubmissionDateDay(),
          form.getSubmissionDateMonth(),
          form.getSubmissionDateYear(),
          errors
      );
    }

    if (ObjectUtils.anyNotNull(
        form.getConsultationStartDateDay(),
        form.getConsultationStartDateMonth(),
        form.getConsultationStartDateYear())) {
      DateValidationUtil.validateDateIsTodayOrInThePast(
          "consultationStartDate",
          "consultation start date",
          form.getConsultationStartDateDay(),
          form.getConsultationStartDateMonth(),
          form.getConsultationStartDateYear(),
          errors
      );
    }

    if (ObjectUtils.anyNotNull(
        form.getConsultationClosingDateDay(),
        form.getConsultationClosingDateMonth(),
        form.getConsultationClosingDateYear())) {
      DateValidationUtil.validateDateIsTodayOrInThePast(
          "consultationClosingDate",
          "consultation closing date",
          form.getConsultationClosingDateDay(),
          form.getConsultationClosingDateMonth(),
          form.getConsultationClosingDateYear(),
          errors
      );
    }

    return !errors.hasErrors();
  }
}
