package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class ResourceDetailsFormValidator {

  private final InternalTeamService internalTeamService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Autowired
  public ResourceDetailsFormValidator(InternalTeamService internalTeamService,
                                      SubmissionDetailService submissionDetailService,
                                      ResourceDetailsPersistenceService resourceDetailsPersistenceService) {
    this.internalTeamService = internalTeamService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
  }

  public boolean isValid(Errors errors, SubmissionDetail submissionDetail, ValidationType validationType, User user) {
    switch (validationType) {
      case FULL -> validateFull(errors, submissionDetail, user);
      case PARTIAL -> validatePartial(errors, submissionDetail);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }

    return !errors.hasErrors();
  }

  public void validatePartial(Errors errors, SubmissionDetail submissionDetail) {
    validateResourceName(errors, submissionDetail);

    validateOrganisationCantBeRemoved(errors, submissionDetail);

    validateOnshoreOffshore(errors);

  }

  public void validateFull(Errors errors, SubmissionDetail submissionDetail, User user) {
    validateResourceName(errors, submissionDetail);

    var organisationErrorCountBefore = errors.getErrorCount();
    validateOrganisationCantBeRemoved(errors, submissionDetail);
    var organisationErrorCountAfter = errors.getErrorCount();

    if (!internalTeamService.isRegulatorOrAreaTeamUser(user) && organisationErrorCountBefore == organisationErrorCountAfter) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors,
          "responsibleOrganisationId",
          "responsibleOrganisationId.required",
          "Enter the responsible organisation");
    }

    validateOnshoreOffshore(errors);
  }

  private void validateResourceName(Errors errors, SubmissionDetail submissionDetail) {
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "resourceName",
        "resourceName.required",
        "Enter the %s name".formatted(submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase()));
  }

  /** Prevents removal of an organisation if there was one originally.
   * Corrections bypass this as the organisation is set automatically.
   * @param errors the current error result
   * @param submissionDetail the current submission detail
   */
  private void validateOrganisationCantBeRemoved(Errors errors, SubmissionDetail submissionDetail) {
    var latestApprovedResourceDetail = getLatestApprovedResourceDetail(submissionDetail);
    if (latestApprovedResourceDetail != null
        && latestApprovedResourceDetail.getOrganisationId() != null
        && BooleanUtils.isNotTrue(submissionDetail.isCorrection())
        && !latestApprovedResourceDetail.getSubmissionDetail().getId().equals(submissionDetail.getId())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "responsibleOrganisationId",
          "responsibleOrganisationId.required",
          "You cannot remove the responsible organisation from a resource that has " +
              "a responsible organisation on an earlier accepted submission");
    }
  }

  private void validateOnshoreOffshore(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors, "onshoreOffshore", "onshoreOffshore.required", "Select onshore or offshore");
  }

  private ResourceDetail getLatestApprovedResourceDetail(SubmissionDetail submissionDetail) {
    return submissionDetailService
        .getLatestApprovedSubmissionDetailForSubmission(submissionDetail)
        .flatMap(resourceDetailsPersistenceService::getResourceDetailBySubmissionDetail)
        .orElse(null);
  }
}
