package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
      when(resourceGroupLinksRepository.findByResource(selectedSubmissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.of(getResourceGroupLink(
              selectedGroupId, selectedSubmissionDetail.getSubmission().getResource())));
      var captor = ArgumentCaptor.forClass(ResourceGroupsValidatorHints.class);

      underTest.validateForResource(form, bindingResult, submissionDetail.getSubmission().getResource());

      verify(resourceGroupsFormValidator, times(1))
          .validate(eq(form), eq(bindingResult), captor.capture());
      assertThat(captor.getValue())
          .extracting(
              ResourceGroupsValidatorHints::resource,
              ResourceGroupsValidatorHints::selectedGroupId)
          .containsExactly(
              submissionDetail.getSubmission().getResource(),
              selectedGroupId);
    }
  }

  @Nested
  class WhenGetRelatedResourcesFormFromResource {

    @Test
    void getRelatedResourcesFormFromResource_existingEntity_thenPopulatedForm() {
      var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var otherResource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var groupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(resource, groupId);
      var resourcesInGroup = List.of(resource, otherResource);
      var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail.setId(UUID.randomUUID());

      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(Optional.of(resourceGroupLink));
      when(resourceGroupLinksRepository.findByResourceGroupId(groupId))
          .thenReturn(List.of(resourceGroupLink));
      when(resourceGroupQueryService.getResourcesInGroupForGroupId(groupId))
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
      var resourceGroupLink = getResourceGroupLink(resource, groupId);
      resourceGroupLink.setResourceGroupId(null);

      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(Optional.of(resourceGroupLink));

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

      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(Optional.empty());

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
      var resourceGroupLink = getResourceGroupLink(resource, groupId);
      var resourcesInGroup = List.of(resource, otherResource);
      var submissionDetail1 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail1.setId(UUID.randomUUID());
      submissionDetail1.setAcceptedTimestamp(Instant.parse("2024-01-01T10:00:00Z"));
      var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionDetail2.setId(UUID.randomUUID());
      submissionDetail2.setAcceptedTimestamp(Instant.parse("2024-02-01T10:00:00Z"));
      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(Optional.of(resourceGroupLink));
      when(resourceGroupLinksRepository.findByResourceGroupId(groupId))
          .thenReturn(List.of(resourceGroupLink));
      when(resourceGroupQueryService.getResourcesInGroupForGroupId(groupId))
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
      var resourceGroupLink = getResourceGroupLink(resource, groupId);
      var resourcesInGroup = List.of(resource, otherResource);
      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(Optional.of(resourceGroupLink));
      when(resourceGroupLinksRepository.findByResourceGroupId(groupId))
          .thenReturn(List.of(resourceGroupLink));
      when(resourceGroupQueryService.getResourcesInGroupForGroupId(groupId))
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

    private ResourceGroupLink getResourceGroupLink(Resource resource, UUID resourceGroupId) {
      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setResource(resource);
      resourceGroupLink.setResourceGroupId(resourceGroupId);
      return resourceGroupLink;
    }
  }

  @Nested
  class WhenUpdatingResourceGroupLinkFromResource {
    @Mock
    private Resource resource;

    private final RelatedResourcesForm form = new RelatedResourcesForm();

    private ResourceGroupLink resourceGroupLink;

    @Nested
    class AndRemovingGroupLink {
      @BeforeEach
      void setUp() {
        form.setRelatedToAnotherResource(false);
      }

      @Test
      void WhenTwoGroupLinksPresent_verifyGroupDeleted() {
        resourceGroupLink = getResourceGroupLink(UUID.randomUUID(), resource);
        var resourceGroupLink2 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);
        when(resourceGroupService.getResourceGroupLinkByResource(resource))
            .thenReturn(Optional.of(resourceGroupLink));
        when(resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupLink.getResourceGroupId()))
            .thenReturn(List.of(resourceGroupLink, resourceGroupLink2));
        underTest.updateResourceGroupLinkFromResource(resource, form);

        verify(resourceGroupLinksRepository, times(1))
            .deleteAll(List.of(resourceGroupLink, resourceGroupLink2));
      }

      @Test
      void WhenThreeGroupLinksPresent_verifyOneDeleted() {
        resourceGroupLink = getResourceGroupLink(UUID.randomUUID(), resource);
        var resourceGroupLink2 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);
        var resourceGroupLink3 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);

        when(resourceGroupService.getResourceGroupLinkByResource(resource))
            .thenReturn(Optional.of(resourceGroupLink));
        when(resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupLink.getResourceGroupId()))
            .thenReturn(List.of(resourceGroupLink, resourceGroupLink2, resourceGroupLink3));
        underTest.updateResourceGroupLinkFromResource(resource, form);

        verify(resourceGroupLinksRepository, times(1))
            .delete(resourceGroupLink);
      }

      @Test
      void WhenNoGroupLinkPresent_verifyNothingHappened() {
        when(resourceGroupService.getResourceGroupLinkByResource(resource))
            .thenReturn(Optional.empty());

        underTest.updateResourceGroupLinkFromResource(resource, form);

        verifyNoInteractions(resourceGroupLinksRepository);
        verifyNoMoreInteractions(resourceGroupService);
      }
    }

    @Nested
    class AndUpdatingGroup {
      private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
      private SubmissionDetail submissionDetail;
      private ResourceGroupLink resourceGroupLink;

      @BeforeEach
      void setUp() {
        submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
        resourceGroupLink = getResourceGroupLink(UUID.randomUUID(), resource);
        form.setRelatedToAnotherResource(true);
        form.setSubmissionDetailIdOfRelatedResource(SUBMISSION_DETAIL_ID.toString());
        when(resourceGroupService.getResourceGroupLinkByResource(resource))
            .thenReturn(Optional.of(resourceGroupLink));
      }

      @Test
      void WhenCreatingNewGroup_assertSavedNewLinks() {
        when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
            .thenReturn(submissionDetail);

        when(resourceGroupService.getResourceGroupLinkByResource(
            submissionDetail.getSubmission().getResource()))
            .thenReturn(Optional.empty());
        var captor = ArgumentCaptor.forClass(ResourceGroupLink.class);
        underTest.updateResourceGroupLinkFromResource(resource, form);

        verify(resourceGroupLinksRepository, times(2)).save(captor.capture());

        assertThat(captor.getAllValues())
            .extracting(
                ResourceGroupLink::getResource)
            .containsExactlyInAnyOrder(resourceGroupLink.getResource(), submissionDetail.getSubmission().getResource());
        assertThat(captor.getAllValues().get(0).getResourceGroupId())
            .isEqualTo(captor.getAllValues().get(1).getResourceGroupId());
      }

      @Test
      void AndAddingToExistingGroup() {
        var groupId = UUID.randomUUID();
        var existingGroupLink1 = getResourceGroupLink(groupId, resource);
        var captor = ArgumentCaptor.forClass(ResourceGroupLink.class);

        when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
            .thenReturn(submissionDetail);
        when(resourceGroupService.getResourceGroupLinkByResource(
            submissionDetail.getSubmission().getResource()))
            .thenReturn(Optional.of(existingGroupLink1));
        underTest.updateResourceGroupLinkFromResource(resource, form);

        verify(resourceGroupLinksRepository, times(1)).save(captor.capture());

        assertThat(captor.getValue())
            .extracting(
                ResourceGroupLink::getResource,
                ResourceGroupLink::getResourceGroupId)
            .containsExactly(
                resource,
                groupId
            );
      }
    }
  }

  private ResourceGroupLink getResourceGroupLink(UUID resourceGroupId, Resource resource) {
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setResourceGroupId(resourceGroupId);
    return resourceGroupLink;
  }
}