package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupsFormValidator.SELECTED_SUBMISSION_DETAIL_ID;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class ResourceGroupsFormValidatorTest {

  @Mock
  private ResourceGroupQueryService resourceGroupQueryService;

  @InjectMocks
  private ResourceGroupsFormValidator resourceGroupsFormValidator;

  static private SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenRelatedProspectsFormClass_thenTrue() {
    var supportedClass = RelatedResourcesForm.class;
    assertTrue(resourceGroupsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotRelatedProspectsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(resourceGroupsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidRelatedResourcesForm();

    var bindingResult = validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.PROSPECT),
        UUID.randomUUID());
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new RelatedResourcesForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.PROSPECT),
        UUID.randomUUID()));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherResource", Set.of("relatedToAnotherResource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noRelatedToAnotherProspect_thenValidationErrors() {
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.PROSPECT),
        UUID.randomUUID()));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherResource", Set.of("relatedToAnotherResource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noExistingGroupId_thenValidationErrors() {
    var form = getValidRelatedResourcesForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resourceGroupsFormValidator.validate(
        form, bindingResult, new ResourceGroupsValidatorHints(
            resource,
            null
           ));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .isEmpty();
  }

  @Test
  void validate_whenInValidForm_whenNoHintsProvided_assertThrows() {
    var form = getValidRelatedResourcesForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var wrongHints = new Object();
    assertThatThrownBy(() -> resourceGroupsFormValidator.validate(
        form, bindingResult, wrongHints))
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot get ResourceGroupsValidatorHints");
  }

  @Test
  void validate_whenValidForm_notRelatedToAnotherProspect_noExistingLink_assertNoErrors() {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    resourceGroupsFormValidator.validate(
        form,
        bindingResult,
        new ResourceGroupsValidatorHints(
            resource,
            null));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validate_whenInValidForm_noRelatedSubmissionDetail_thenValidationErrors() {
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource(null);

    var bindingResult = validateRelatedProspectsForm_fullValidation(
        form,
        getResourceGroupLink(ResourceType.PROSPECT),
        UUID.randomUUID());
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry(SELECTED_SUBMISSION_DETAIL_ID, Set.of(SELECTED_SUBMISSION_DETAIL_ID + ".required"))
        );
    assertThat(extractedMessages)
        .containsExactly(
            entry(SELECTED_SUBMISSION_DETAIL_ID, Set.of("Select a prospect or prospect group"))
        );
  }

  @Test
  void validate_whenNonArchivedResourceAndActiveResourceExistsInGroup_thenReject() {
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("some-id");
    var selectedGroupId = UUID.randomUUID();
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resource.setActive(true);
    var existingLink = getResourceGroupLink(ResourceType.PROSPECT);
    existingLink.setResourceGroupId(selectedGroupId);
    when(resourceGroupQueryService.getActiveResourceInGroupForGroupId(selectedGroupId))
      .thenReturn(Optional.of(ResourceTestUtils.buildResource(ResourceType.PROSPECT)));
    var validator = new ResourceGroupsFormValidator(resourceGroupQueryService);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    validator.validate(form, bindingResult, new ResourceGroupsValidatorHints(
      resource, selectedGroupId));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
      .containsKey(SELECTED_SUBMISSION_DETAIL_ID);
  }

  @Test
  void validate_whenArchivedResourceAndActiveResourceExistsInGroup_thenAllow() {
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("some-id");
    var selectedGroupId = UUID.randomUUID();
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resource.setActive(false);
    var existingLink = getResourceGroupLink(ResourceType.PROSPECT);
    existingLink.setResourceGroupId(selectedGroupId);
    when(resourceGroupQueryService.getActiveResourceInGroupForGroupId(selectedGroupId))
      .thenReturn(Optional.of(ResourceTestUtils.buildResource(ResourceType.PROSPECT)));
    var validator = new ResourceGroupsFormValidator(resourceGroupQueryService);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    validator.validate(form, bindingResult, new ResourceGroupsValidatorHints(
      resource, selectedGroupId));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
      .doesNotContainKey(SELECTED_SUBMISSION_DETAIL_ID);
  }

  @Test
  void validate_whenNonArchivedResourceAndNoActiveResourceInGroup_thenAllow() {
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("some-id");
    var selectedGroupId = UUID.randomUUID();
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resource.setActive(false);
    var existingLink = getResourceGroupLink(ResourceType.PROSPECT);
    existingLink.setResourceGroupId(selectedGroupId);
    when(resourceGroupQueryService.getActiveResourceInGroupForGroupId(selectedGroupId))
      .thenReturn(Optional.empty());
    var validator = new ResourceGroupsFormValidator(resourceGroupQueryService);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    validator.validate(form, bindingResult, new ResourceGroupsValidatorHints(
      resource, selectedGroupId));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
      .doesNotContainKey(SELECTED_SUBMISSION_DETAIL_ID);
  }

  private BindingResult validateRelatedProspectsForm_fullValidation(RelatedResourcesForm form,
                                                                    ResourceGroupLink currentResourceLink,
                                                                    UUID selectedResourceGroupId) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    resourceGroupsFormValidator.validate(
        form, bindingResult, new ResourceGroupsValidatorHints(
            currentResourceLink.getResource(), selectedResourceGroupId));

    return bindingResult;
  }

  private ResourceGroupLink getResourceGroupLink(ResourceType resourceType) {
    var resource = ResourceTestUtils.buildResource(resourceType);
    var link = new ResourceGroupLink();
    link.setResource(resource);
    link.setResourceGroupId(UUID.randomUUID());
    return link;
  }

  private RelatedResourcesForm getValidRelatedResourcesForm() {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    return form;
  }
}