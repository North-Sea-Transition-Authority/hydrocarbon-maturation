package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class RelatedResourcesFormValidatorTest {

  @Mock
  private ResourceGroupQueryService resourceGroupQueryService;

  @Mock
  private RelatedResourcesFormService relatedResourcesFormService;

  @InjectMocks
  private RelatedResourcesFormValidator relatedResourcesFormValidator;

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidRelatedProspectsForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var bindingResult = validateRelatedProspectsForm_fullValidation(form, submissionDetail);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new RelatedResourcesForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form, submissionDetail));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherResource", Set.of("relatedToAnotherResource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noRelatedToAnotherProspect_thenValidationErrors() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(null);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form, submissionDetail));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherResource", Set.of("relatedToAnotherResource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noRelatedSubmissionDetail_thenValidationErrors() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource(null);

    var bindingResult = validateRelatedProspectsForm_fullValidation(form, submissionDetail);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("submissionDetailIdOfRelatedResource.required"))
        );
    assertThat(extractedMessages)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("Select a prospect or prospect group"))
        );
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoValidationErrors() {
    var form = new RelatedResourcesForm();
    var bindingResult = validateRelatedProspectsForm_partialValidation(form, new SubmissionDetail());
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenActiveResourceExistsInGroupAndResourceNotArchived_thenReject() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("some-id");
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resource = submissionDetail.getSubmission().getResource();
    resource.setActive(true);
    var resourceGroup = new ResourceGroup();
    when(relatedResourcesFormService.getSelectedResourceGroupOrNull(form)).thenReturn(resourceGroup);
    when(resourceGroupQueryService.getActiveResourceInGroup(resourceGroup)).thenReturn(Optional.of(resource));
    var validator = new RelatedResourcesFormValidator(resourceGroupQueryService, relatedResourcesFormService);
    var errors = new org.springframework.validation.BeanPropertyBindingResult(form, "form");
    validator.validateFull(form, errors, submissionDetail);
    assertThat(errors.getFieldError("submissionDetailIdOfRelatedResource")).isNotNull();
    assertThat(Objects.requireNonNull(errors.getFieldError("submissionDetailIdOfRelatedResource")).getCode())
        .isEqualTo("submissionDetailIdOfRelatedResource.cannotHaveSeveralActiveResourcesInGroup");
  }

  @Test
  void validate_whenActiveResourceExistsInGroupAndResourceIsArchived_thenAllow() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("some-id");
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resource = submissionDetail.getSubmission().getResource();
    resource.setActive(false);
    var resourceGroup = new ResourceGroup();
    when(relatedResourcesFormService.getSelectedResourceGroupOrNull(form)).thenReturn(resourceGroup);
    when(resourceGroupQueryService.getActiveResourceInGroup(resourceGroup)).thenReturn(Optional.of(resource));
    var validator = new RelatedResourcesFormValidator(resourceGroupQueryService, relatedResourcesFormService);
    var errors = new org.springframework.validation.BeanPropertyBindingResult(form, "form");
    validator.validateFull(form, errors, submissionDetail);
    assertThat(errors.getFieldError("submissionDetailIdOfRelatedResource")).isNull();
  }

  @Test
  void validate_whenNoActiveResourceInGroup_thenAllow() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("some-id");
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var resource = submissionDetail.getSubmission().getResource();
    resource.setActive(true);
    var resourceGroup = new ResourceGroup();
    when(relatedResourcesFormService.getSelectedResourceGroupOrNull(form)).thenReturn(resourceGroup);
    when(resourceGroupQueryService.getActiveResourceInGroup(resourceGroup)).thenReturn(Optional.empty());
    var validator = new RelatedResourcesFormValidator(resourceGroupQueryService, relatedResourcesFormService);
    var errors = new org.springframework.validation.BeanPropertyBindingResult(form, "form");
    validator.validateFull(form, errors, submissionDetail);
    assertThat(errors.getFieldError("submissionDetailIdOfRelatedResource")).isNull();
  }

  private BindingResult validateRelatedProspectsForm_fullValidation(RelatedResourcesForm form, SubmissionDetail submissionDetail) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    relatedResourcesFormValidator.isValid(
        form, bindingResult, submissionDetail, ValidationType.FULL);

    return bindingResult;
  }

  private BindingResult validateRelatedProspectsForm_partialValidation(RelatedResourcesForm form, SubmissionDetail submissionDetail) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    relatedResourcesFormValidator.isValid(
        form, bindingResult, submissionDetail, ValidationType.PARTIAL);

    return bindingResult;
  }

  private RelatedResourcesForm getValidRelatedProspectsForm() {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    return form;
  }
}