package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLinksRepository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class RelatedResourcesFormServiceTest {
  @Mock
  private RelatedResourcesFormValidator relatedResourcesFormValidator;
  @Mock
  private RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  @Mock
  private ResourceGroupQueryService resourceGroupQueryService;
  @Mock
  private ResourceGroupLinksRepository resourceGroupLinksRepository;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private SearchSelectorService searchSelectorService;
  @InjectMocks
  private RelatedResourcesFormService relatedResourcesFormService;
  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(UUID.randomUUID());
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new RelatedResourcesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);
    relatedResourcesFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(relatedResourcesFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());
    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void getRelatedResourcesFormEntityToForm_newEntity_thenEmptyForm() {
    when(relatedResourcesPersistenceService.getRelatedResource(submissionDetail)).thenReturn(Optional.empty());

    assertThat(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail))
        .extracting(
            RelatedResourcesForm::getRelatedToAnotherResource,
            RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource,
            RelatedResourcesForm::getPreferredView
        )
        .containsExactly(
            null,
            null,
            null
        );
  }

  @Test
  void relatedResourceEntityToForm_existingEntity_thenPopulatedForm() {
    var relatedResource = getRelatedResource(submissionDetail);
    when(relatedResourcesPersistenceService.getRelatedResource(submissionDetail)).thenReturn(Optional.of(relatedResource));
    var resourceGroupView = new ResourceGroupView(UUID.randomUUID(), "", null);
    when(resourceGroupQueryService.getResourceGroupViewForSubmissionDetail(
        relatedResource.getRelatedSubmissionDetail(), null))
        .thenReturn(Optional.of(resourceGroupView));

    assertThat(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail))
        .extracting(
            RelatedResourcesForm::getRelatedToAnotherResource,
            RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource,
            RelatedResourcesForm::getPreferredView
        )
        .containsExactly(
            relatedResource.getRelatedToAnotherResource(),
            relatedResource.getRelatedSubmissionDetail().getId().toString(),
            relatedResource.getPreferredView()
        );
  }

  @Test
  void relatedResourceEntityToForm_existingEntityButRelatedSubmissionDetailNoLongerPreferredView_thenPopulatedForm() {
    var relatedResource = getRelatedResource(submissionDetail);
    when(relatedResourcesPersistenceService.getRelatedResource(submissionDetail)).thenReturn(Optional.of(relatedResource));
    when(resourceGroupQueryService.getResourceGroupViewForSubmissionDetail(
        relatedResource.getRelatedSubmissionDetail(),
        null))
        .thenReturn(Optional.empty());
    var resourceGroupLink = new ResourceGroupLink();
    var resourceGroupId = UUID.randomUUID();
    resourceGroupLink.setResourceGroupId(resourceGroupId);
    when(resourceGroupLinksRepository.findByResource(
        relatedResource.getRelatedSubmissionDetail().getSubmission().getResource()))
        .thenReturn(Optional.of(resourceGroupLink));
    var resourceGroupLink2 = new ResourceGroupLink();
    var resource = new Resource();
    resourceGroupLink2.setResource(resource);
    when(resourceGroupLinksRepository.findByResourceGroupIdAndPreferredViewIsTrue(resourceGroupId))
        .thenReturn(Optional.of(resourceGroupLink2));
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(submissionDetail2));

    assertThat(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail))
        .extracting(
            RelatedResourcesForm::getRelatedToAnotherResource,
            RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource,
            RelatedResourcesForm::getPreferredView
        )
        .containsExactly(
            relatedResource.getRelatedToAnotherResource(),
            submissionDetail2.getId().toString(),
            relatedResource.getPreferredView()
        );
  }

  @Test
  void relatedResourceEntityToForm_existingEntityButNotRelatedToAnotherResource_thenPartiallyPopulatedForm() {
    var relatedResource = getRelatedResource(submissionDetail);
    relatedResource.setRelatedToAnotherResource(false);
    when(relatedResourcesPersistenceService.getRelatedResource(submissionDetail)).thenReturn(Optional.of(relatedResource));

    assertThat(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail))
        .extracting(
            RelatedResourcesForm::getRelatedToAnotherResource,
            RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource,
            RelatedResourcesForm::getPreferredView
        )
        .containsExactly(
            false,
            null,
            null
        );
  }

  @Test
  void getPreSelectedSubmissionDetail_whenPreselected() {
    var relatedResource = getRelatedResource(submissionDetail);
    relatedResource.setRelatedToAnotherResource(false);
    var relatedResourceForm = new RelatedResourcesForm();
    relatedResourceForm.setSubmissionDetailIdOfRelatedResource(
        relatedResource.getSubmissionDetail().getId().toString());
    var resourceGroup =
        new ResourceGroupView(submissionDetail.getId(), "returned resource", UUID.randomUUID());
    var expectedMap = Map.of(submissionDetail.getId().toString(), resourceGroup.resourceName());
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
        .thenReturn(Optional.of(submissionDetail));
    when(resourceGroupQueryService.getResourceGroupViewForSubmissionDetail(
        submissionDetail,
        null))
        .thenReturn(Optional.of(resourceGroup));
    when(searchSelectorService.buildPrePopulatedSelections(
        List.of(submissionDetail.getId().toString()),
        Map.of(submissionDetail.getId().toString(), resourceGroup.resourceName())))
        .thenReturn(expectedMap);
    var result = relatedResourcesFormService.getPreSelectedSubmissionDetail(relatedResourceForm);
    assertThat(result)
        .containsExactly(
            entry(submissionDetail.getId().toString(), resourceGroup.resourceName()));
  }

  @Test
  void getPreSelectedSubmissionDetail_whenPreselectedAndNoResourceNameForSubmission() {
    var relatedResource = getRelatedResource(submissionDetail);
    relatedResource.setRelatedToAnotherResource(false);
    var relatedResourceForm = new RelatedResourcesForm();
    relatedResourceForm.setSubmissionDetailIdOfRelatedResource(
        relatedResource.getSubmissionDetail().getId().toString());
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    submissionDetail.getSubmission().setResource(resource);
    var resourceGroup =
        new ResourceGroupView(submissionDetail.getId(), "returned resource", UUID.randomUUID());
    var expectedMap = Map.of(submissionDetail.getId().toString(), resourceGroup.resourceName());
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() -> relatedResourcesFormService.getPreSelectedSubmissionDetail(relatedResourceForm))
        .isExactlyInstanceOf(IllegalStateException.class)
        .hasMessage("Could not get resource name for the latest accepted submission of resource with ID %s"
            .formatted(resource.getId().toString()));
  }

  @Test
  void getPreSelectedSubmissionDetail_whenNotPreselected() {
    var relatedResource = getRelatedResource(submissionDetail);
    relatedResource.setRelatedToAnotherResource(false);
    var relatedResourceForm = new RelatedResourcesForm();
    relatedResourceForm.setSubmissionDetailIdOfRelatedResource(null);

    var result = relatedResourcesFormService.getPreSelectedSubmissionDetail(relatedResourceForm);
    assertThat(result)
        .isEmpty();

  }

  private RelatedResource getRelatedResource(SubmissionDetail submissionDetail) {
    var related = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var relatedResource = new RelatedResource();
    relatedResource.setSubmissionDetail(submissionDetail);
    relatedResource.setRelatedToAnotherResource(true);
    relatedResource.setRelatedSubmissionDetail(related);
    relatedResource.setPreferredView(false);
    return relatedResource;
  }
}