package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;

@ExtendWith(MockitoExtension.class)
class ManageResourceGroupsServiceTest {

  @Mock
  private ResourceGroupLinksRepository resourceGroupLinksRepository;
  @Mock
  private ResourceGroupService resourceGroupService;
  @Mock
  private ResourceGroupQueryService resourceGroupQueryService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private ResourceGroupsFormValidator resourceGroupsFormValidator;
  @InjectMocks
  private ManageResourceGroupsService underTest;

  @Nested
  class WhenValidatingRelatedResourceForm {
    private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    @Test
    void validateForResource_verifyMethodCalls() {
      var form = new RelatedResourcesForm();
      var bindingResult = new BeanPropertyBindingResult(form, "form");
      var captor = ArgumentCaptor.forClass(ResourceGroupsValidatorHints.class);

      underTest.validateForResource(form, bindingResult, submissionDetail.getSubmission().getResource());

      verify(resourceGroupsFormValidator, times(1))
          .validate(eq(form), eq(bindingResult), captor.capture());
      assertThat(captor.getValue().resource()).isEqualTo(submissionDetail.getSubmission().getResource());
    }

    @Test
    void validateForResource_whenResourceGroupSelected_verifyMethodCalls() {
      var form = new RelatedResourcesForm();
      form.setRelatedToAnotherResource(true);
      form.setSubmissionDetailIdOfRelatedResource(UUID.randomUUID().toString());
      var bindingResult = new BeanPropertyBindingResult(form, "form");
      var selectedSubmissionDetail =
          SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      when(submissionDetailService.getSubmissionDetailForIdOrError(
          UUID.fromString(form.getSubmissionDetailIdOfRelatedResource())))
          .thenReturn(selectedSubmissionDetail);
      var selectedGroupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(selectedGroupId, selectedSubmissionDetail.getSubmission().getResource());
      when(resourceGroupLinksRepository.findByResource(selectedSubmissionDetail.getSubmission().getResource()))
          .thenReturn(resourceGroupLink);
      var captor = ArgumentCaptor.forClass(ResourceGroupsValidatorHints.class);

      underTest.validateForResource(form, bindingResult, submissionDetail.getSubmission().getResource());

      verify(resourceGroupsFormValidator, times(1))
          .validate(eq(form), eq(bindingResult), captor.capture());
      assertThat(captor.getValue())
          .extracting(
              ResourceGroupsValidatorHints::resource,
              ResourceGroupsValidatorHints::selectedGroup)
          .containsExactly(
              submissionDetail.getSubmission().getResource(),
              resourceGroupLink.getResourceGroup());
    }
  }

  @Nested
  class WhenGetRelatedResourcesFormFromResource {

    @Test
    void getRelatedResourcesFormFromResource_existingEntity_thenPopulatedForm() {
      var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var otherResource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var groupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(groupId, resource);
      var resourcesInGroup = List.of(resource, otherResource);
      var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail.setId(UUID.randomUUID());

      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(resourceGroupLink);
      when(resourceGroupQueryService.resourceIsInGroupWithOtherResources(resourceGroupLink)).thenReturn(true);
      when(resourceGroupLinksRepository.findByResourceGroup(resourceGroupLink.getResourceGroup()))
          .thenReturn(List.of(resourceGroupLink));
      when(resourceGroupQueryService.getResourcesInGroup(resourceGroupLink.getResourceGroup()))
          .thenReturn(resourcesInGroup);
      when(resourceGroupQueryService.getActiveResourceForResourceList(resourcesInGroup))
          .thenReturn(Optional.of(otherResource));
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(otherResource))
          .thenReturn(Optional.of(submissionDetail));

      var result = underTest.getRelatedResourcesFormFromResource(resource);

      assertThat(result)
          .extracting(
              RelatedResourcesForm::getRelatedToAnotherResource,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(true, submissionDetail.getId().toString());
    }

    @Test
    void getRelatedResourcesFormFromResource_existingEntityNotSetGroup_thenPopulatedForm() {
      var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var groupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(groupId, resource);
      resourceGroupLink.setResourceGroup(null);

      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(resourceGroupLink);

      var result = underTest.getRelatedResourcesFormFromResource(resource);

      assertThat(result)
          .extracting(
              RelatedResourcesForm::getRelatedToAnotherResource,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(false,
              null);
    }

    @Test
    void getRelatedResourcesFormFromResource_noEntity_thenNoRelatedResource() {
      var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var groupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(groupId, resource);

      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(resourceGroupLink);
      when(resourceGroupQueryService.resourceIsInGroupWithOtherResources(resourceGroupLink)).thenReturn(false);

      var result = underTest.getRelatedResourcesFormFromResource(resource);

      assertThat(result)
          .extracting(
              RelatedResourcesForm::getRelatedToAnotherResource,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(false, null);
    }

    @Test
    void getRelatedResourcesFormFromResource_existingEntity_noActiveResource_thenUsesLatestSubmissionDetail() {
      var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var otherResource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var groupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(groupId, resource);
      var resourcesInGroup = List.of(resource, otherResource);
      var submissionDetail1 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail1.setId(UUID.randomUUID());
      submissionDetail1.setAcceptedTimestamp(Instant.parse("2024-01-01T10:00:00Z"));
      var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail2.setId(UUID.randomUUID());
      submissionDetail2.setAcceptedTimestamp(Instant.parse("2024-02-01T10:00:00Z"));
      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(resourceGroupLink);
      when(resourceGroupQueryService.resourceIsInGroupWithOtherResources(resourceGroupLink)).thenReturn(true);
      when(resourceGroupLinksRepository.findByResourceGroup(resourceGroupLink.getResourceGroup()))
          .thenReturn(List.of(resourceGroupLink));
      when(resourceGroupQueryService.getResourcesInGroup(resourceGroupLink.getResourceGroup()))
          .thenReturn(resourcesInGroup);
      when(resourceGroupQueryService.getActiveResourceForResourceList(resourcesInGroup))
          .thenReturn(Optional.empty());
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
          .thenReturn(Optional.of(submissionDetail1));
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(otherResource))
          .thenReturn(Optional.of(submissionDetail2));
      var result = underTest.getRelatedResourcesFormFromResource(resource);
      assertThat(result)
          .extracting(
              RelatedResourcesForm::getRelatedToAnotherResource,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(true, submissionDetail2.getId().toString());
    }

    @Test
    void getRelatedResourcesFormFromResource_existingEntity_noSubmissionDetails_thenSubmissionDetailIdIsNull() {
      var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var otherResource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var groupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(groupId, resource);
      var resourcesInGroup = List.of(resource, otherResource);
      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(resourceGroupLink);
      when(resourceGroupQueryService.resourceIsInGroupWithOtherResources(resourceGroupLink)).thenReturn(true);
      when(resourceGroupLinksRepository.findByResourceGroup(resourceGroupLink.getResourceGroup()))
          .thenReturn(List.of(resourceGroupLink));
      when(resourceGroupQueryService.getResourcesInGroup(resourceGroupLink.getResourceGroup()))
          .thenReturn(resourcesInGroup);
      when(resourceGroupQueryService.getActiveResourceForResourceList(resourcesInGroup))
          .thenReturn(Optional.empty());
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
          .thenReturn(Optional.empty());
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(otherResource))
          .thenReturn(Optional.empty());
      var result = underTest.getRelatedResourcesFormFromResource(resource);
      assertThat(result)
          .extracting(
              RelatedResourcesForm::getRelatedToAnotherResource,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(true, null);
    }
  }

  @Nested
  class WhenUpdatingResourceGroupLinkFromResource {
    @Mock
    private Resource resource;

    private final RelatedResourcesForm form = new RelatedResourcesForm();

    @Nested
    class AndUpdatingGroup {
      private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
      private SubmissionDetail submissionDetail;

      @BeforeEach
      void setUp() {
        submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
        ResourceGroupLink resourceGroupLink = getResourceGroupLink(UUID.randomUUID(), resource);
        form.setRelatedToAnotherResource(true);
        form.setSubmissionDetailIdOfRelatedResource(SUBMISSION_DETAIL_ID.toString());
        when(resourceGroupService.getResourceGroupLinkByResource(resource))
            .thenReturn(resourceGroupLink);
      }

      @Test
      void AndAddingToExistingGroup() {
        var groupId = UUID.randomUUID();
        var existingGroupLink1 = getResourceGroupLink(groupId, resource);
        var captor = ArgumentCaptor.forClass(ResourceGroupLink.class);

        when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
            .thenReturn(submissionDetail);
        when(resourceGroupService.getResourceGroupLinkByResource(submissionDetail.getSubmission().getResource()))
            .thenReturn(existingGroupLink1);
        underTest.updateResourceGroupLinkFromResource(resource, form);

        verify(resourceGroupLinksRepository, times(1)).save(captor.capture());

        assertThat(captor.getValue())
            .extracting(
                ResourceGroupLink::getResource,
                ResourceGroupLink::getResourceGroup)
            .containsExactly(
                resource,
                existingGroupLink1.getResourceGroup()
            );
      }

      @Test
      void updateResourceGroupLinkFromResource_currentResourceActive_updatesOtherResourceGroupLinks() {
        // arrange - override setup to mark current resource active and return a known current group link
        var currentGroupId = UUID.randomUUID();
        var currentGroupLink = getResourceGroupLink(currentGroupId, resource);
        when(resourceGroupService.getResourceGroupLinkByResource(resource)).thenReturn(currentGroupLink);
        when(resource.getActive()).thenReturn(true);

        // ensure the form and submission detail mapping is prepared by parent setup (form has submission id)
        var selectedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
        when(submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(form.getSubmissionDetailIdOfRelatedResource())))
            .thenReturn(selectedSubmissionDetail);

        // prepare selected group links (these should be moved into the current group)
        var otherResource1 = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
        var otherResource2 = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
        var selectedGroupLink1 = new ResourceGroupLink();
        selectedGroupLink1.setResource(otherResource1);
        selectedGroupLink1.setResourceGroup(new ResourceGroup());
        var selectedGroupLink2 = new ResourceGroupLink();
        selectedGroupLink2.setResource(otherResource2);
        selectedGroupLink2.setResourceGroup(new ResourceGroup());

        // the selected resource group's link returned for the selected submission detail
        var selectedResource = selectedSubmissionDetail.getSubmission().getResource();
        var selectedResourceGroupLink = getResourceGroupLink(UUID.randomUUID(), selectedResource);
        when(resourceGroupService.getResourceGroupLinkByResource(selectedSubmissionDetail.getSubmission().getResource()))
            .thenReturn(selectedResourceGroupLink);
        when(resourceGroupService.getResourceGroupLinksByResourceGroup(selectedResourceGroupLink.getResourceGroup()))
            .thenReturn(List.of(selectedGroupLink1, selectedGroupLink2));

        // act
        underTest.updateResourceGroupLinkFromResource(resource, form);

        // assert - verify saveAll called and that each saved link now points to the current group's ResourceGroup
        @SuppressWarnings("rawtypes")
        var captor = ArgumentCaptor.forClass(List.class);
        verify(resourceGroupLinksRepository, times(1)).saveAll(captor.capture());
        @SuppressWarnings("unchecked")
        var saved = (List<ResourceGroupLink>) captor.getValue();
        assertThat(saved).hasSize(2);
        for (var link : saved) {
          assertThat(link.getResourceGroup()).isEqualTo(currentGroupLink.getResourceGroup());
        }
      }

      @Test
      void updateResourceGroupLinkFromResource_notRelated_createsNewGroupWhenInGroupWithOthers() {
        // arrange - form defaults to not related; simulate resource is in group with other resources
        var formLocal = new RelatedResourcesForm();
        var currentGroupLink = getResourceGroupLink(UUID.randomUUID(), resource);
        when(resourceGroupService.getResourceGroupLinkByResource(resource)).thenReturn(currentGroupLink);
        when(resourceGroupQueryService.resourceIsInGroupWithOtherResources(currentGroupLink)).thenReturn(true);

        // act
        underTest.updateResourceGroupLinkFromResource(resource, formLocal);

        // assert
        verify(resourceGroupService, times(1)).removeResourceFromGroup(resource);
        verify(resourceGroupService, times(1)).createNewGroupForResource(resource);
      }

      @Test
      void updateResourceGroupLinkFromResource_notRelated_noActionWhenNotInGroupWithOthers() {
        // arrange - form defaults to not related; simulate resource is not in group with other resources
        var formLocal = new RelatedResourcesForm();
        var currentGroupLink = getResourceGroupLink(UUID.randomUUID(), resource);
        when(resourceGroupService.getResourceGroupLinkByResource(resource)).thenReturn(currentGroupLink);
        when(resourceGroupQueryService.resourceIsInGroupWithOtherResources(currentGroupLink)).thenReturn(false);

        // act
        underTest.updateResourceGroupLinkFromResource(resource, formLocal);

        // assert - no remove/create calls
        verify(resourceGroupService, times(0)).removeResourceFromGroup(resource);
        verify(resourceGroupService, times(0)).createNewGroupForResource(resource);
      }
    }
  }

  private ResourceGroupLink getResourceGroupLink(UUID resourceGroupId, Resource resource) {
    var resourceGroup = new ResourceGroup();
    resourceGroup.setId(resourceGroupId);
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setResourceGroup(resourceGroup);
    return resourceGroupLink;
  }
}