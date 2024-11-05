package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionQuerySearchRecord;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DateValidationUtil;

@Service
class CorrectionFormValidator {

  private static final String APPROVAL_DATA_CHANGE_FIELD_PREFIX = "approvalDateChange";
  private static final String APPROVAL_DATA_CHANGE_DISPLAY_PREFIX = "new accepted date of the submission";
  private static final String MISSING_UPDATE_FIELD_PREFIX = "missingUpdate";
  private static final String MISSING_UPDATE_DISPLAY_PREFIX = "missing update date of the submission";

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;

  @Autowired
  CorrectionFormValidator(SubmissionDetailService submissionDetailService,
                          SubmissionHistoryQueryService submissionHistoryQueryService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
  }

  boolean isValid(CorrectionForm form, Resource resource, Errors errors) {

    ValidationUtils.rejectIfEmpty(
        errors, "correctionType", "correctionType.required", "Select what you want to correct");

    if (form.getCorrectionType() == CorrectionType.SUBMISSION_CORRECTION) {
      validateSubmissionCorrection(errors);
    }

    if (form.getCorrectionType() == CorrectionType.ACCEPTED_DATE_CHANGE) {
      validateApprovalDateChange(form, resource, errors);
    }

    if (form.getCorrectionType() == CorrectionType.HISTORIC_UPDATE) {
      validateMissingUpdate(form, resource, errors);
    }

    if (form.getCorrectionType() == CorrectionType.FIELD_HISTORIC_UPDATE) {
      validateMissingFieldUpdate(form, resource, errors);
    }

    if (form.getCorrectionType() == CorrectionType.REMOVE_SUBMISSION) {
      validateSubmissionRemoval(errors);
    }

    return !errors.hasErrors();
  }

  private void validateSubmissionCorrection(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "submissionDetailIdForCorrectingInformation",
        "submissionDetailIdForCorrectingInformation.required",
        "Select the submission you want to correct");
  }

  private void validateApprovalDateChange(CorrectionForm form, Resource resource, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "submissionDetailIdForChangingApprovedDate",
        "submissionDetailIdForChangingApprovedDate.required",
        "Select the submission you want to change the accepted date for");

    if (form.getSubmissionDetailIdForChangingApprovedDate() != null) {
      var formSubmissionDetailId = UUID.fromString(form.getSubmissionDetailIdForChangingApprovedDate());
      var selectedSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(formSubmissionDetailId);

      // basic date validation - check it isn't in the future
      var basicDateValidationPassed = DateValidationUtil.validateDateIsTodayOrInThePast(
          APPROVAL_DATA_CHANGE_FIELD_PREFIX,
          APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
          form.getApprovalDateChangeDay(),
          form.getApprovalDateChangeMonth(),
          form.getApprovalDateChangeYear(),
          errors);

      if (basicDateValidationPassed) {
        var approvedSubmissions =
            submissionHistoryQueryService.getAcceptedSubmissionsOnResourceWithLinkedResources(resource.getId());

        checkDateAvailable(
            approvedSubmissions,
            form.getApprovalDateChangeDay(),
            form.getApprovalDateChangeMonth(),
            form.getApprovalDateChangeYear(),
            APPROVAL_DATA_CHANGE_FIELD_PREFIX,
            "New accepted date cannot be the same as another accepted date",
            errors
        );

        if (selectedSubmissionDetail.getSubmission().getResource().getType() == ResourceType.PROSPECT) {
          // if it is a prospect submission, and not the create submission, check it is after the create accepted date
          if (selectedSubmissionDetail.getSubmissionType() != SubmissionType.CREATE_PROSPECT) {
            checkApprovalAfterDateForSubmissionTypes(
                approvedSubmissions,
                List.of(SubmissionType.CREATE_PROSPECT),
                form.getApprovalDateChangeDay(),
                form.getApprovalDateChangeMonth(),
                form.getApprovalDateChangeYear(),
                APPROVAL_DATA_CHANGE_FIELD_PREFIX,
                APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
                "after the creation date of the prospect - %s",
                errors
            );
          }

          // if the selected submission isn't a relinquishment check it is before that relinquishment date
          if (selectedSubmissionDetail.getSubmissionType() != SubmissionType.RELINQUISHMENT_PROSPECT) {
            checkApprovalBeforeDateForSubmissionTypes(
                approvedSubmissions,
                List.of(SubmissionType.RELINQUISHMENT_PROSPECT),
                form.getApprovalDateChangeDay(),
                form.getApprovalDateChangeMonth(),
                form.getApprovalDateChangeYear(),
                APPROVAL_DATA_CHANGE_FIELD_PREFIX,
                APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
                "before the relinquishment date of the prospect - %s",
                errors
            );
          }

          // if it isn't a dry well submission, check it is before any dry well submission
          if (!selectedSubmissionDetail.isDryWellSubmission()) {
            checkApprovalBeforeDateForSubmissionTypes(
                approvedSubmissions,
                List.of(SubmissionType.POST_DRILL_DRY_HOLE),
                form.getApprovalDateChangeDay(),
                form.getApprovalDateChangeMonth(),
                form.getApprovalDateChangeYear(),
                APPROVAL_DATA_CHANGE_FIELD_PREFIX,
                APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
                "before the dry well date of the resource - %s",
                errors
            );
          }

          checkApprovalBeforeDateForSubmissionTypes(
              approvedSubmissions,
              List.of(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT),
              form.getApprovalDateChangeDay(),
              form.getApprovalDateChangeMonth(),
              form.getApprovalDateChangeYear(),
              APPROVAL_DATA_CHANGE_FIELD_PREFIX,
              APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
              "before the initial discovery date of the resource - %s",
              errors
          );
        }

        if (selectedSubmissionDetail.getSubmission().getResource().getType() == ResourceType.DISCOVERY) {
          // if it is a discovery submission, and not the create submission, check it is after the create approved date
          if (selectedSubmissionDetail.getSubmissionType() != SubmissionType.CREATE_DISCOVERY
              && selectedSubmissionDetail.getSubmissionType() != SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT) {
            checkApprovalAfterDateForSubmissionTypes(
                approvedSubmissions,
                List.of(SubmissionType.CREATE_DISCOVERY, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT),
                form.getApprovalDateChangeDay(),
                form.getApprovalDateChangeMonth(),
                form.getApprovalDateChangeYear(),
                APPROVAL_DATA_CHANGE_FIELD_PREFIX,
                APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
                "after the creation date of the discovery - %s",
                errors
            );
          }

          // if the selected submission isn't a relinquishment check it is before that relinquishment date
          if (selectedSubmissionDetail.getSubmissionType() != SubmissionType.RELINQUISHMENT_DISCOVERY) {
            checkApprovalBeforeDateForSubmissionTypes(
                approvedSubmissions,
                List.of(SubmissionType.RELINQUISHMENT_DISCOVERY),
                form.getApprovalDateChangeDay(),
                form.getApprovalDateChangeMonth(),
                form.getApprovalDateChangeYear(),
                APPROVAL_DATA_CHANGE_FIELD_PREFIX,
                APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
                "before the relinquishment date of the discovery - %s",
                errors
            );
          }

          // if it is a discovery submission, check it is after the latest prospect submission
          checkDiscoveryAfterLatestProspect(
              approvedSubmissions,
              form.getApprovalDateChangeDay(),
              form.getApprovalDateChangeMonth(),
              form.getApprovalDateChangeYear(),
              APPROVAL_DATA_CHANGE_FIELD_PREFIX,
              APPROVAL_DATA_CHANGE_DISPLAY_PREFIX,
              errors
          );
        }
      }
    }
  }

  private void validateMissingUpdate(CorrectionForm form, Resource resource, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "submissionDetailIdForMissingUpdate",
        "submissionDetailIdForMissingUpdate.required",
        "Select the submission you want to create the update after");

    if (form.getSubmissionDetailIdForMissingUpdate() != null) {
      var formSubmissionDetailId = UUID.fromString(form.getSubmissionDetailIdForMissingUpdate());
      var selectedSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(formSubmissionDetailId);

      // basic date validation - check it isn't in the future
      var basicDateValidationPassed = DateValidationUtil.validateDateIsTodayOrInThePast(
          MISSING_UPDATE_FIELD_PREFIX,
          MISSING_UPDATE_DISPLAY_PREFIX,
          form.getMissingUpdateDay(),
          form.getMissingUpdateMonth(),
          form.getMissingUpdateYear(),
          errors);

      if (basicDateValidationPassed) {
        var approvedSubmissions =
            submissionHistoryQueryService.getAcceptedSubmissionsOnResourceWithLinkedResources(resource.getId());

        checkDateAvailable(
            approvedSubmissions,
            form.getMissingUpdateDay(),
            form.getMissingUpdateMonth(),
            form.getMissingUpdateYear(),
            MISSING_UPDATE_FIELD_PREFIX,
            "Missing update date cannot be the same as another accepted date",
            errors
        );

        if (selectedSubmissionDetail.getSubmission().getResource().getType() == ResourceType.PROSPECT) {
          // check it is after the create approved date
          checkApprovalAfterDateForSubmissionTypes(
              approvedSubmissions,
              List.of(SubmissionType.CREATE_PROSPECT),
              form.getMissingUpdateDay(),
              form.getMissingUpdateMonth(),
              form.getMissingUpdateYear(),
              MISSING_UPDATE_FIELD_PREFIX,
              MISSING_UPDATE_DISPLAY_PREFIX,
              "after the creation date of the prospect - %s",
              errors
          );

          // check it is before any dry well submission
          checkApprovalBeforeDateForSubmissionTypes(
              approvedSubmissions,
              List.of(SubmissionType.POST_DRILL_DRY_HOLE),
              form.getMissingUpdateDay(),
              form.getMissingUpdateMonth(),
              form.getMissingUpdateYear(),
              MISSING_UPDATE_FIELD_PREFIX,
              MISSING_UPDATE_DISPLAY_PREFIX,
              "before the dry well date of the resource - %s",
              errors
          );

          // check it is before any relinquishment date
          checkApprovalBeforeDateForSubmissionTypes(
              approvedSubmissions,
              List.of(SubmissionType.RELINQUISHMENT_PROSPECT),
              form.getMissingUpdateDay(),
              form.getMissingUpdateMonth(),
              form.getMissingUpdateYear(),
              MISSING_UPDATE_FIELD_PREFIX,
              MISSING_UPDATE_DISPLAY_PREFIX,
              "before the relinquishment date of the prospect - %s",
              errors
          );

          checkApprovalBeforeDateForSubmissionTypes(
              approvedSubmissions,
              List.of(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT),
              form.getMissingUpdateDay(),
              form.getMissingUpdateMonth(),
              form.getMissingUpdateYear(),
              MISSING_UPDATE_FIELD_PREFIX,
              MISSING_UPDATE_DISPLAY_PREFIX,
              "before the initial discovery date of the resource - %s",
              errors
          );
        }

        if (selectedSubmissionDetail.getSubmission().getResource().getType() == ResourceType.DISCOVERY) {
          // check it is after the create approved date
          checkApprovalAfterDateForSubmissionTypes(
              approvedSubmissions,
              List.of(SubmissionType.CREATE_DISCOVERY, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT),
              form.getMissingUpdateDay(),
              form.getMissingUpdateMonth(),
              form.getMissingUpdateYear(),
              MISSING_UPDATE_FIELD_PREFIX,
              MISSING_UPDATE_DISPLAY_PREFIX,
              "after the creation date of the discovery - %s",
              errors
          );

          // check it is before a relinquishment date
          checkApprovalBeforeDateForSubmissionTypes(
              approvedSubmissions,
              List.of(SubmissionType.RELINQUISHMENT_DISCOVERY),
              form.getMissingUpdateDay(),
              form.getMissingUpdateMonth(),
              form.getMissingUpdateYear(),
              MISSING_UPDATE_FIELD_PREFIX,
              MISSING_UPDATE_DISPLAY_PREFIX,
              "before the relinquishment date of the discovery - %s",
              errors
          );

          // if it is a discovery submission, check it is after the latest prospect submission
          checkDiscoveryAfterLatestProspect(
              approvedSubmissions,
              form.getMissingUpdateDay(),
              form.getMissingUpdateMonth(),
              form.getMissingUpdateYear(),
              MISSING_UPDATE_FIELD_PREFIX,
              MISSING_UPDATE_DISPLAY_PREFIX,
              errors
          );
        }
      }

    }
  }

  private void validateMissingFieldUpdate(CorrectionForm form, Resource resource, Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "submissionTypeForMissingUpdate",
        "submissionTypeForMissingUpdate.required",
        "Select the submission type for the update you want to create");

    var basicDateValidationPassed = DateValidationUtil.validateDateIsTodayOrInThePast(
        MISSING_UPDATE_FIELD_PREFIX,
        MISSING_UPDATE_DISPLAY_PREFIX,
        form.getMissingUpdateDay(),
        form.getMissingUpdateMonth(),
        form.getMissingUpdateYear(),
        errors);

    if (form.getSubmissionTypeForMissingUpdate() != null && basicDateValidationPassed) {
      validateFieldSubmissionTypeAndDates(form, resource, errors);
    }
  }

  private void validateFieldSubmissionTypeAndDates(CorrectionForm form, Resource resource, Errors errors) {
    var existingSubmissions = submissionDetailService.getExtantSubmissionDetailsForResource(resource);

    var selectedSubmissionType = form.getSubmissionTypeForMissingUpdate();

    var existingSubmissionTypes = existingSubmissions.stream()
        .map(SubmissionDetail::getSubmissionType)
        .filter(submissionType -> !submissionType.equals(SubmissionType.CREATE_FDPA))
        .toList();

    if (existingSubmissionTypes.contains(selectedSubmissionType)) {
      errors.rejectValue(
          "submissionTypeForMissingUpdate",
          "submissionTypeForMissingUpdate.invalid",
          "Update of type %s cannot be created as a submission already exists of that type"
              .formatted(selectedSubmissionType.getAcronym())
      );
    } else {
      var approvedSubmissions =
          submissionHistoryQueryService.getAcceptedSubmissionsOnResourceWithLinkedResources(resource.getId());

      checkDateAvailable(
          approvedSubmissions,
          form.getMissingUpdateDay(),
          form.getMissingUpdateMonth(),
          form.getMissingUpdateYear(),
          MISSING_UPDATE_FIELD_PREFIX,
          "Missing update date cannot be the same as another accepted date",
          errors
      );

      validateFieldSubmissionTypeOrder(form, existingSubmissions, errors);
    }
  }

  private void validateFieldSubmissionTypeOrder(CorrectionForm form,
                                                List<SubmissionDetail> existingSubmissionDetails,
                                                Errors errors) {
    var selectedSubmissionType = form.getSubmissionTypeForMissingUpdate();

    if (!selectedSubmissionType.equals(SubmissionType.CREATE_CSR)) {
      checkPreviousType(form, existingSubmissionDetails, errors);
    }

    if (!selectedSubmissionType.equals(SubmissionType.CREATE_COP)) {
      checkFollowingType(form, existingSubmissionDetails, errors);
    }
  }

  private void checkPreviousType(CorrectionForm form,
                                 List<SubmissionDetail> existingSubmissionDetails,
                                 Errors errors) {

    var latestPreviousSubmissionDetail = existingSubmissionDetails.stream()
        .filter(submissionDetail ->
            getFieldTypeOrder(submissionDetail.getSubmissionType()) < getFieldTypeOrder(form.getSubmissionTypeForMissingUpdate()))
        .max(Comparator.comparing(SubmissionDetail::getAcceptedTimestamp));

    if (latestPreviousSubmissionDetail.isEmpty()) {
      return;
    }

    DateValidationUtil.validateDateIsAfterComparisonDate(
        MISSING_UPDATE_FIELD_PREFIX,
        MISSING_UPDATE_DISPLAY_PREFIX,
        form.getMissingUpdateDay(),
        form.getMissingUpdateMonth(),
        form.getMissingUpdateYear(),
        LocalDate.ofInstant(latestPreviousSubmissionDetail.get().getAcceptedTimestamp(), ZoneId.systemDefault()),
        "after date of previous submission type",
        errors
    );
  }

  private void checkFollowingType(CorrectionForm form,
                                  List<SubmissionDetail> existingSubmissionDetails,
                                  Errors errors) {

    var earliestFollowingSubmissionDetail = existingSubmissionDetails.stream()
        .filter(submissionDetail ->
            getFieldTypeOrder(submissionDetail.getSubmissionType()) > getFieldTypeOrder(form.getSubmissionTypeForMissingUpdate()))
        .min(Comparator.comparing(SubmissionDetail::getAcceptedTimestamp));

    if (earliestFollowingSubmissionDetail.isEmpty()) {
      return;
    }

    DateValidationUtil.validateDateIsBeforeComparisonDate(
        MISSING_UPDATE_FIELD_PREFIX,
        MISSING_UPDATE_DISPLAY_PREFIX,
        form.getMissingUpdateDay(),
        form.getMissingUpdateMonth(),
        form.getMissingUpdateYear(),
        LocalDate.ofInstant(earliestFollowingSubmissionDetail.get().getAcceptedTimestamp(), ZoneId.systemDefault()),
        "before date of following submission type",
        errors
    );
  }

  private int getFieldTypeOrder(SubmissionType submissionType) {
    return switch (submissionType) {
      case CREATE_CSR -> 1;
      case CREATE_FDP -> 2;
      case CREATE_FDPA -> 3;
      case CREATE_COP -> 4;
      default -> throw new IllegalStateException("Unexpected value: " + submissionType);
    };
  }

  private void validateSubmissionRemoval(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "submissionDetailIdForRemoval",
        "submissionDetailIdForRemoval.required",
        "Select the submission you want to remove");
  }

  // check the new approved date isn't the same as any other approved date
  private void checkDateAvailable(List<SubmissionQuerySearchRecord> approvedSubmissions,
                                  String day,
                                  String month,
                                  String year,
                                  String fieldPrefix,
                                  String errorMessage,
                                  Errors errors) {
    var approvedDates = approvedSubmissions.stream()
        .map(a -> a.acceptedTimestamp().toLocalDateTime().toLocalDate())
        .toList();
    var newApprovedDate = LocalDate.of(
        Integer.parseInt(year),
        Integer.parseInt(month),
        Integer.parseInt(day)
    );

    if (approvedDates.contains(newApprovedDate)) {
      errors.rejectValue(
          fieldPrefix + "Day",
          fieldPrefix + "Day.invalid",
          errorMessage
      );
      errors.rejectValue(
          fieldPrefix + "Month",
          fieldPrefix + "Month.invalid",
          ""
      );
      errors.rejectValue(
          fieldPrefix + "Year",
          fieldPrefix + "Year.invalid",
          ""
      );
    }
  }

  private void checkApprovalBeforeDateForSubmissionTypes(List<SubmissionQuerySearchRecord> approvedSubmissions,
                                                         List<SubmissionType> submissionTypes,
                                                         String day,
                                                         String month,
                                                         String year,
                                                         String fieldPrefix,
                                                         String displayPrefix,
                                                         String errorSuffix,
                                                         Errors errors
  ) {
    var approvedLocalDate = getApprovedLocalDateForSubmissionTypes(
        approvedSubmissions,
        submissionTypes
    );

    approvedLocalDate.ifPresent(localDate -> DateValidationUtil.validateDateIsBeforeComparisonDate(
        fieldPrefix,
        displayPrefix,
        day,
        month,
        year,
        localDate,
        String.format(errorSuffix, DateUtil.formatDate(localDate)),
        errors));
  }

  private void checkApprovalAfterDateForSubmissionTypes(List<SubmissionQuerySearchRecord> approvedSubmissions,
                                                        List<SubmissionType> submissionTypes,
                                                        String day,
                                                        String month,
                                                        String year,
                                                        String fieldPrefix,
                                                        String displayPrefix,
                                                        String errorSuffix,
                                                        Errors errors
  ) {
    var createApprovedLocalDate = getApprovedLocalDateForSubmissionTypes(
        approvedSubmissions,
        submissionTypes
    );

    createApprovedLocalDate.ifPresent(localDate -> DateValidationUtil.validateDateIsAfterComparisonDate(
        fieldPrefix,
        displayPrefix,
        day,
        month,
        year,
        localDate,
        String.format(errorSuffix, DateUtil.formatDate(localDate)),
        errors));
  }

  private Optional<LocalDate> getApprovedLocalDateForSubmissionTypes(List<SubmissionQuerySearchRecord> approvedSubmissions,
                                                                     List<SubmissionType> submissionTypes) {
    var acceptedTimestamp = approvedSubmissions.stream()
        .filter(a -> submissionTypes.contains(a.submissionType()))
        .map(SubmissionQuerySearchRecord::acceptedTimestamp)
        .findFirst();

    if (acceptedTimestamp.isPresent()) {
      var approvedInstant = acceptedTimestamp.get().toInstant();
      var approvedLocalDate = LocalDate.ofInstant(
          approvedInstant,
          ZoneId.systemDefault()
      );
      return Optional.of(approvedLocalDate);
    }
    return Optional.empty();
  }

  private void checkDiscoveryAfterLatestProspect(List<SubmissionQuerySearchRecord> approvedSubmissions,
                                                 String day,
                                                 String month,
                                                 String year,
                                                 String fieldPrefix,
                                                 String displayPrefix,
                                                 Errors errors) {
    var latestProspectTimestamp = approvedSubmissions.stream()
        .filter(a -> a.submissionType() == SubmissionType.CREATE_PROSPECT
            || a.submissionType() == SubmissionType.PROSPECT_UPDATE
            || a.submissionType() == SubmissionType.RELINQUISHMENT_PROSPECT
        )
        .sorted(Comparator.comparing(SubmissionQuerySearchRecord::acceptedTimestamp).reversed())
        .map(SubmissionQuerySearchRecord::acceptedTimestamp)
        .findFirst();

    if (latestProspectTimestamp.isPresent()) {
      var latestProspectApprovedInstant = latestProspectTimestamp.get().toInstant();
      var latestProspectLocalDate = LocalDate.ofInstant(
          latestProspectApprovedInstant,
          ZoneId.systemDefault()
      );
      DateValidationUtil.validateDateIsAfterComparisonDate(
          fieldPrefix,
          displayPrefix,
          day,
          month,
          year,
          latestProspectLocalDate,
          String.format(
              "after the latest prospect submission date of the resource - %s",
              DateUtil.formatDate(latestProspectLocalDate)
          ),
          errors
      );
    }
  }
}
