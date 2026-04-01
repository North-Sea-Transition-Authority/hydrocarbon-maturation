package uk.co.nstauthority.hydrocarbonmaturation.newresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ResourceDetailsFormValidatorTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private InternalTeamService internalTeamService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @InjectMocks
  private ResourceDetailsFormValidator resourceDetailsFormValidator;

  @Test
  void validate_whenFilledValidForm_asRegulator_noErrors() {
    var form = getValidForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    resourceDetailsFormValidator.validateFull(bindingResult, submissionDetail, USER);
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenFilledValidForm_partialValidation_asRegulator_noErrors() {
    var form = getValidForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    resourceDetailsFormValidator.validatePartial(bindingResult, submissionDetail);
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenFilledValidForm_asNotRegulator_noErrors() {
    var form = getValidForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    resourceDetailsFormValidator.validateFull(bindingResult, submissionDetail, USER);
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenCorrectionAndNoResponsibleOrganisation_partialValidation_asRegulator_noErrors() {
    var form = getValidForm();
    form.setResponsibleOrganisationId(null);
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    var newSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(submissionDetail);
    newSubmissionDetail.setCorrection(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(newSubmissionDetail)).thenReturn(Optional.of(submissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsFormValidator.validatePartial(bindingResult, newSubmissionDetail);
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validate_whenCorrectionAndNoResponsibleOrganisation_fullValidation_asRegulator_noErrors() {
    var form = getValidForm();
    form.setResponsibleOrganisationId(null);
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    var newSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(submissionDetail);
    newSubmissionDetail.setCorrection(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(newSubmissionDetail)).thenReturn(Optional.of(submissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsFormValidator.validateFull(bindingResult, newSubmissionDetail, USER);
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  void validateFull_whenEmptyForm_asRegulator_whenOrgOnAnEarlierSubmission_assertErrors() {
    var form = new ResourceDetailsForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(123);
    var newSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(newSubmissionDetail)).thenReturn(Optional.of(submissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsFormValidator.isValid(bindingResult, newSubmissionDetail, ValidationType.FULL, USER);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );
  }

  @Test
  void validateFull_whenEmptyForm_asNonRegulator_whenOrgOnAnEarlierSubmission_assertErrors() {
    var form = new ResourceDetailsForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(123);
    var newSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(newSubmissionDetail)).thenReturn(Optional.of(submissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsFormValidator.isValid(bindingResult, newSubmissionDetail, ValidationType.FULL, USER);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );

    assertThat(extractedErrorMessages)
        .contains(
            entry("resourceName", Set.of("Enter the prospect name")),
            entry("responsibleOrganisationId",
                Set.of("You cannot remove the responsible organisation" +
                    " from a resource that has a responsible organisation on an earlier accepted submission")),
            entry("onshoreOffshore", Set.of("Select onshore or offshore"))
        );
  }

  @Test
  void validatePartial_whenEmptyForm_asRegulator_whenOrgOnAnEarlierSubmission_assertErrors() {
    var form = new ResourceDetailsForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(123);
    var newSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(newSubmissionDetail)).thenReturn(Optional.of(submissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsFormValidator.isValid(bindingResult, newSubmissionDetail, ValidationType.PARTIAL, USER);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );
  }

  @Test
  void validatePartial_whenEmptyForm_asNonRegulator_whenOrgOnAnEarlierSubmission_assertErrors() {
    var form = new ResourceDetailsForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(123);
    var newSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(newSubmissionDetail)).thenReturn(Optional.of(submissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsFormValidator.isValid(bindingResult, newSubmissionDetail, ValidationType.PARTIAL, USER);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required"))
        );

    assertThat(extractedErrorMessages)
        .contains(
            entry("resourceName", Set.of("Enter the prospect name")),
            entry("responsibleOrganisationId",
                Set.of("You cannot remove the responsible organisation" +
                    " from a resource that has a responsible organisation on an earlier accepted submission")),
            entry("onshoreOffshore", Set.of("Select onshore or offshore"))
        );
  }

  private ResourceDetailsForm getValidForm() {
    var form = new ResourceDetailsForm();
    form.setResourceName("resource");
    form.setResponsibleOrganisationId("1");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    return form;
  }
}