package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class ResourceDetailsFormService implements SubmissionFormService {

  private final ResourceDetailsFormValidator resourceDetailsFormValidator;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final SubmissionDetailService submissionDetailService;
  private final TeamQueryService teamQueryService;

  @Autowired
  ResourceDetailsFormService(ResourceDetailsFormValidator resourceDetailsFormValidator,
                             ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                             OrganisationUnitQueryService organisationUnitQueryService,
                             SubmissionDetailService submissionDetailService,
                             TeamQueryService teamQueryService) {
    this.resourceDetailsFormValidator = resourceDetailsFormValidator;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.submissionDetailService = submissionDetailService;
    this.teamQueryService = teamQueryService;
  }

  ResourceDetailsForm getResourceDetailsForm(SubmissionDetail submissionDetail) {
    return resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)
        .map(this::resourceDetailEntityToForm)
        .orElse(new ResourceDetailsForm());
  }

  @Override
  public BindingResult validate(SubmissionForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                SubmissionDetail submissionDetail,
                                User user) {
    resourceDetailsFormValidator.validate(
        form,
        bindingResult,
        getValidationHints(submissionDetail, validationType, user));
    return bindingResult;
  }

  public Map<String, String> getPreSelectedOrganisationUnit(String responsibleOrganisationId) {
    if (StringUtils.isBlank(responsibleOrganisationId)) {
      return Map.of();
    }
    return organisationUnitQueryService.getPreSelectedOrganisationUnit(Integer.valueOf(responsibleOrganisationId));
  }

  private ResourceDetailsForm resourceDetailEntityToForm(ResourceDetail resourceDetail) {
    var form = new ResourceDetailsForm();
    form.setResourceName(resourceDetail.getResourceName());
    form.setOnshoreOffshore(resourceDetail.getOnshoreOffshore());
    var organisationId = resourceDetail.getOrganisationId();

    if (!Objects.isNull(organisationId)) {
      form.setResponsibleOrganisationId(organisationId.toString());
    }

    return form;
  }

  private ResourceDetailsFormValidatorHints getValidationHints(SubmissionDetail submissionDetail,
                                                               ValidationType validationType,
                                                               User user) {
    var isRegulator = teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION);
    if (submissionDetail.isFirstSubmission()) {
      return new ResourceDetailsFormValidatorHints(
          validationType,
          isRegulator,
          submissionDetail.getSubmission().getResource().getType(),
          null);
    }

    var latestApprovedResourceDetails = submissionDetailService
        .getLatestApprovedSubmissionForSubmission(submissionDetail)
        .flatMap(resourceDetailsPersistenceService::getResourceDetailBySubmissionDetail)
        .orElse(null);

    return new ResourceDetailsFormValidatorHints(
        validationType,
        isRegulator,
        submissionDetail.getSubmission().getResource().getType(),
        latestApprovedResourceDetails);
  }
}
