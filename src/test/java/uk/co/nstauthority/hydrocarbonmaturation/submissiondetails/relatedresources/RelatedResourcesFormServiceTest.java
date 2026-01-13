package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLinksRepository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class RelatedResourcesFormServiceTest {
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
  void getRelatedResourcesFormEntityToForm_newEntity_thenEmptyForm() {
    when(relatedResourcesPersistenceService.getRelatedResource(submissionDetail)).thenReturn(Optional.empty());

    assertThat(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail))
        .extracting(
            RelatedResourcesForm::getRelatedToAnotherResource,
            RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource
        )
        .containsExactly(
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
            RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource
        )
        .containsExactly(
            relatedResource.getRelatedToAnotherResource(),
            relatedResource.getRelatedSubmissionDetail().getId().toString()
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
            RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource
        )
        .containsExactly(
            false,
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
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(submissionDetail))
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
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(submissionDetail))
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

  @Test
  void getSelectedResourceGroupIdOrNull_whenSubmissionDetailIdSetAndGroupExists_returnsGroup() {
    var form = new RelatedResourcesForm();
    var relatedSubmissionDetailId = UUID.randomUUID();
    form.setSubmissionDetailIdOfRelatedResource(relatedSubmissionDetailId.toString());
    var relatedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    relatedSubmissionDetail.setId(relatedSubmissionDetailId);
    var resource = new Resource();
    relatedSubmissionDetail.getSubmission().setResource(resource);
    var resourceGroup = new ResourceGroup();
    var link = new ResourceGroupLink();
    link.setResourceGroup(resourceGroup);
    when(submissionDetailService.getSubmissionDetailForIdOrError(relatedSubmissionDetailId)).thenReturn(relatedSubmissionDetail);
    when(resourceGroupLinksRepository.findByResource(resource)).thenReturn(link);
    assertThat(relatedResourcesFormService.getSelectedResourceGroupOrNull(form)).isEqualTo(resourceGroup);
  }

  @Test
  void getSelectedResourceGroupIdOrNull_whenSubmissionDetailIsNull_returnsNull() {
    var form = new RelatedResourcesForm();
    form.setSubmissionDetailIdOfRelatedResource(null);
    assertThat(relatedResourcesFormService.getSelectedResourceGroupOrNull(form)).isNull();
  }

  private RelatedResource getRelatedResource(SubmissionDetail submissionDetail) {
    var related = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var relatedResource = new RelatedResource();
    relatedResource.setSubmissionDetail(submissionDetail);
    relatedResource.setRelatedToAnotherResource(true);
    relatedResource.setRelatedSubmissionDetail(related);
    return relatedResource;
  }
}