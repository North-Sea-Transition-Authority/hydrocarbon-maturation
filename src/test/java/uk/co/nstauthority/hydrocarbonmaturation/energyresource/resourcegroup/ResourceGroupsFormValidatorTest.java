package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
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
        getResourceGroupLink(ResourceType.PROSPECT, false),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.PROSPECT, false),
            getResourceGroupLink(ResourceType.PROSPECT, true)));
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new RelatedResourcesForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.PROSPECT, false),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.PROSPECT, false),
            getResourceGroupLink(ResourceType.PROSPECT, true))));

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
        getResourceGroupLink(ResourceType.PROSPECT, false),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.PROSPECT, false),
            getResourceGroupLink(ResourceType.PROSPECT, true))));

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
            resource, new ResourceGroupLink(),
            List.of(
                getResourceGroupLink(ResourceType.PROSPECT, false),
                getResourceGroupLink(ResourceType.PROSPECT, true)),
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
            new ResourceGroupLink(),
            List.of(),
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
    form.setPreferredView(false);

    var bindingResult = validateRelatedProspectsForm_fullValidation(
        form,
        getResourceGroupLink(ResourceType.PROSPECT, false),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.PROSPECT, false),
            getResourceGroupLink(ResourceType.PROSPECT, true)));
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
  void validate_whenInValidForm_noPreferredView_thenValidationErrors() {
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("1a3");
    form.setPreferredView(null);

    var bindingResult = validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.PROSPECT, false),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.PROSPECT, false),
            getResourceGroupLink(ResourceType.PROSPECT, true)));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("preferredView", Set.of("preferredView.required"))
        );

    assertThat(extractedMessages)
        .containsExactly(
            entry("preferredView", Set.of("Select whether this is the regulator preferred view of the prospect?"))
        );
  }

  @Test
  void validate_whenDiscovery_noPreferredView_thenValidationErrors_assertResourceType() {
    submissionDetail.getSubmission().getResource().setType(ResourceType.DISCOVERY);
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource(null);
    form.setPreferredView(null);
    var bindingResult = validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.DISCOVERY, false),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.DISCOVERY, false),
            getResourceGroupLink(ResourceType.DISCOVERY, true)));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("submissionDetailIdOfRelatedResource.required")),
            entry("preferredView", Set.of("preferredView.required"))
        );

    assertThat(extractedMessages)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("Select a discovery or discovery group")),
            entry("preferredView", Set.of("Select whether this is the regulator preferred view of the discovery?"))
        );
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void validate_whenDiscovery_removedFromGroupWhenPreferredViewAndMoreThanTwo(boolean preferredView) {
    submissionDetail.getSubmission().getResource().setType(ResourceType.DISCOVERY);
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    form.setSubmissionDetailIdOfRelatedResource(null);
    form.setPreferredView(null);
    var bindingResult = validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.DISCOVERY, preferredView),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.DISCOVERY, preferredView),
            getResourceGroupLink(ResourceType.DISCOVERY, false),
            getResourceGroupLink(ResourceType.DISCOVERY, false)));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    if (preferredView) {
      assertThat(extractedErrors)
          .containsExactly(
              entry("relatedToAnotherResource", Set.of("relatedToAnotherResource.mustHavePreferredView"))
          );

      assertThat(extractedMessages)
          .containsExactly(
              entry("relatedToAnotherResource",
                  Set.of("To remove this discovery you must first set another resource as the preferred view"))
          );
    } else {
      assertThat(extractedErrors).isEmpty();
      assertThat(extractedMessages).isEmpty();
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void validate_whenDiscovery_removedFromGroupWhenPreferredViewAndTwoLinks(boolean preferredView) {
    submissionDetail.getSubmission().getResource().setType(ResourceType.DISCOVERY);
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    form.setSubmissionDetailIdOfRelatedResource(null);
    form.setPreferredView(null);
    var bindingResult = validateRelatedProspectsForm_fullValidation(form,
        getResourceGroupLink(ResourceType.DISCOVERY, preferredView),
        UUID.randomUUID(),
        List.of(
            getResourceGroupLink(ResourceType.DISCOVERY, preferredView),
            getResourceGroupLink(ResourceType.DISCOVERY, false)));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors).isEmpty();
    assertThat(extractedMessages).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void validate_whenProspect_removedFromGroupWhenPreferredViewAndMoreThanTwo(boolean preferredView) {
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    var selectedGroupId = UUID.randomUUID();
    form.setSubmissionDetailIdOfRelatedResource(selectedGroupId.toString());
    form.setPreferredView(false);
    var existingLink = getResourceGroupLink(ResourceType.PROSPECT, preferredView);
    existingLink.setResourceGroupId(selectedGroupId);
    var bindingResult = validateRelatedProspectsForm_fullValidation(form,
        existingLink,
        selectedGroupId,
        List.of(
            getResourceGroupLink(ResourceType.PROSPECT, preferredView),
            getResourceGroupLink(ResourceType.PROSPECT, false),
            getResourceGroupLink(ResourceType.PROSPECT, false)));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    if (preferredView) {
      assertThat(extractedErrors)
          .containsExactly(
              entry("preferredView", Set.of("preferredView.mustHavePreferredView"))
          );

      assertThat(extractedMessages)
          .containsExactly(
              entry("preferredView",
                  Set.of("To change whether this prospect is the preferred view" +
                      " you must set another resource as the preferred view"))
          );
    } else {
      assertThat(extractedErrors).isEmpty();
      assertThat(extractedMessages).isEmpty();
    }
  }

  @Test
  void validate_whenProspect_changedGroupAndMoreThanTwoWhenPreferredView() {
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);
    var form = getValidRelatedResourcesForm();
    form.setRelatedToAnotherResource(true);
    var selectedGroupId = UUID.randomUUID();
    form.setSubmissionDetailIdOfRelatedResource(selectedGroupId.toString());
    form.setPreferredView(true);
    var existingLink = getResourceGroupLink(ResourceType.PROSPECT, true);
    existingLink.setResourceGroupId(selectedGroupId);
    var bindingResult = validateRelatedProspectsForm_fullValidation(form,
        existingLink,
        UUID.randomUUID(),
        List.of(
            existingLink,
            getResourceGroupLink(ResourceType.PROSPECT, false),
            getResourceGroupLink(ResourceType.PROSPECT, false)));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
      assertThat(extractedErrors)
          .containsExactly(
              entry("submissionDetailIdOfRelatedResource",
                  Set.of("submissionDetailIdOfRelatedResource.mustHavePreferredView"))
          );

      assertThat(extractedMessages)
          .containsExactly(
              entry("submissionDetailIdOfRelatedResource",
                  Set.of("You cannot change the group of this prospect if it is the preferred view."))
          );
  }

  private BindingResult validateRelatedProspectsForm_fullValidation(RelatedResourcesForm form,
                                                                    ResourceGroupLink currentResourceLink,
                                                                    UUID selectedResourceGroupId,
                                                                    List<ResourceGroupLink> resourceGroupLinks) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    resourceGroupsFormValidator.validate(
        form, bindingResult, new ResourceGroupsValidatorHints(
            currentResourceLink.getResource(), currentResourceLink, resourceGroupLinks, selectedResourceGroupId));

    return bindingResult;
  }

  private ResourceGroupLink getResourceGroupLink(ResourceType resourceType, boolean preferredView) {
    var resource = ResourceTestUtils.buildResource(resourceType);
    var link = new ResourceGroupLink();
    link.setResource(resource);
    link.setResourceGroupId(UUID.randomUUID());
    link.setPreferredView(preferredView);
    return link;
  }

  private RelatedResourcesForm getValidRelatedResourcesForm() {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    return form;
  }
}