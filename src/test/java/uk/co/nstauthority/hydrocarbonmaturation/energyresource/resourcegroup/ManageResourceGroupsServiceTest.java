package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
  private SubmissionDetailService submissionDetailService;
  @Mock
  private ResourceGroupsFormValidator resourceGroupsFormValidator;
  @InjectMocks
  private ManageResourceGroupsService underTest;

  @Nested
  class WhenValidatingRelatedResourceForm {
    private SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    @Test
    void validateForResource_whenResourceGroupLinkPresent_verifyMethodCalls() {
      var form = new RelatedResourcesForm();
      var bindingResult = new BeanPropertyBindingResult(form, "form");
      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setResourceGroupId(UUID.randomUUID());
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.of(resourceGroupLink));
      var expectedListOfResourceGroupLinks = List.of(
          new ResourceGroupLink(), new ResourceGroupLink(), resourceGroupLink);
      when(resourceGroupLinksRepository.findByResourceGroupId(resourceGroupLink.getResourceGroupId()))
          .thenReturn(expectedListOfResourceGroupLinks);

      var captor = ArgumentCaptor.forClass(ResourceGroupsValidatorHints.class);

      underTest.validateForResource(form, bindingResult, submissionDetail.getSubmission().getResource());

      verify(resourceGroupsFormValidator, times(1))
          .validate(eq(form), eq(bindingResult), captor.capture());
      assertThat(captor.getValue())
          .extracting(
              ResourceGroupsValidatorHints::resource,
              ResourceGroupsValidatorHints::currentResourceGroupLink,
              ResourceGroupsValidatorHints::linksWithResource)
          .containsExactly(
              submissionDetail.getSubmission().getResource(),
              resourceGroupLink,
              expectedListOfResourceGroupLinks);
    }

    @Test
    void validateForResource_whenResourceGroupLinkNotPresent_verifyMethodCalls() {
      var form = new RelatedResourcesForm();
      var bindingResult = new BeanPropertyBindingResult(form, "form");
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.empty());
      var captor = ArgumentCaptor.forClass(ResourceGroupsValidatorHints.class);

      underTest.validateForResource(form, bindingResult, submissionDetail.getSubmission().getResource());

      verify(resourceGroupsFormValidator, times(1))
          .validate(eq(form), eq(bindingResult), captor.capture());
      assertThat(captor.getValue())
          .extracting(
              ResourceGroupsValidatorHints::resource,
              ResourceGroupsValidatorHints::linksWithResource)
          .containsExactly(
              submissionDetail.getSubmission().getResource(),
              List.of());

      assertThat(captor.getValue().currentResourceGroupLink())
          .hasAllNullFieldsOrPropertiesExcept("preferredView");
    }

    @Test
    void validateForResource_whenResourceGroupSelected_verifyMethodCalls() {
      var form = new RelatedResourcesForm();
      form.setRelatedToAnotherResource(true);
      form.setSubmissionDetailIdOfRelatedResource(UUID.randomUUID().toString());
      var bindingResult = new BeanPropertyBindingResult(form, "form");
      var currentGroupLink = getResourceGroupLink(UUID.randomUUID(),
          submissionDetail.getSubmission().getResource());
      var selectedSubmissionDetail =
          SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.of(currentGroupLink));
      when(submissionDetailService.getSubmissionDetailForIdOrError(
          UUID.fromString(form.getSubmissionDetailIdOfRelatedResource())))
          .thenReturn(selectedSubmissionDetail);
      var selectedGroupId = UUID.randomUUID();
      var groupLinks = List.of(
          getResourceGroupLink(currentGroupLink.getResourceGroupId(),
              ResourceTestUtils.buildResource(ResourceType.PROSPECT)),
          getResourceGroupLink(currentGroupLink.getResourceGroupId(),
              ResourceTestUtils.buildResource(ResourceType.PROSPECT)));
      when(resourceGroupLinksRepository.findByResource(selectedSubmissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.of(getResourceGroupLink(
              selectedGroupId, selectedSubmissionDetail.getSubmission().getResource())));
      when(resourceGroupLinksRepository.findByResourceGroupId(currentGroupLink.getResourceGroupId()))
          .thenReturn(groupLinks);
      var captor = ArgumentCaptor.forClass(ResourceGroupsValidatorHints.class);

      underTest.validateForResource(form, bindingResult, submissionDetail.getSubmission().getResource());

      verify(resourceGroupsFormValidator, times(1))
          .validate(eq(form), eq(bindingResult), captor.capture());
      assertThat(captor.getValue())
          .extracting(
              ResourceGroupsValidatorHints::resource,
              ResourceGroupsValidatorHints::linksWithResource,
              ResourceGroupsValidatorHints::selectedGroupId,
              ResourceGroupsValidatorHints::currentResourceGroupLink)
          .containsExactly(
              submissionDetail.getSubmission().getResource(),
              groupLinks,
              selectedGroupId,
              currentGroupLink);
    }
  }

  @Nested
  class WhenGetRelatedResourcesFormFromResource {
    private SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    @Test
    void getRelatedResourcesFormFromResource_existingEntity_thenPopulatedForm() {
      var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var preferredViewResource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
      var groupId = UUID.randomUUID();
      var resourceGroupLink = getResourceGroupLink(resource, groupId);
      var preferredViewGroupLink = getResourceGroupLink(preferredViewResource, groupId);

      when(resourceGroupLinksRepository.findByResource(resource))
          .thenReturn(Optional.of(resourceGroupLink));
      when(resourceGroupLinksRepository.findByResourceGroupIdAndPreferredViewIsTrue(
          resourceGroupLink.getResourceGroupId()))
          .thenReturn(Optional.of(preferredViewGroupLink));
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(preferredViewResource))
          .thenReturn(Optional.of(submissionDetail));

      var result = underTest.getRelatedResourcesFormFromResource(resource);

      assertThat(result)
          .extracting(
              RelatedResourcesForm::getRelatedToAnotherResource,
              RelatedResourcesForm::getPreferredView,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(true,
              false,
              submissionDetail.getId().toString());
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
              RelatedResourcesForm::getPreferredView,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(false,
              false,
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
              RelatedResourcesForm::getPreferredView,
              RelatedResourcesForm::getSubmissionDetailIdOfRelatedResource)
          .containsExactly(false,
              null,
              null);
    }

    private ResourceGroupLink getResourceGroupLink(Resource resource, UUID resourceGroupId) {
      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setResource(resource);
      resourceGroupLink.setPreferredView(false);
      resourceGroupLink.setResourceGroupId(resourceGroupId);
      return resourceGroupLink;
    }
  }

  @Nested
  class WhenUpdatingResourceGroupLinkFromResource {
    @Mock
    private Resource resource;

    private RelatedResourcesForm form = new RelatedResourcesForm();

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
      void WhenCreatingNewGroup_assertSavedNewLinks_andOtherPreferredView() {
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
                ResourceGroupLink::getResource,
                ResourceGroupLink::getPreferredView)
            .containsExactlyInAnyOrder(
                tuple(
                    resourceGroupLink.getResource(),
                    false),
                tuple(
                    submissionDetail.getSubmission().getResource(),
                    true
                ));
        assertThat(captor.getAllValues().get(0).getResourceGroupId())
            .isEqualTo(captor.getAllValues().get(1).getResourceGroupId());
      }

      @Test
      void WhenCreatingNewGroup_assertSavedNewLinks_andPreferredView() {
        form.setPreferredView(true);
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
                ResourceGroupLink::getResource,
                ResourceGroupLink::getPreferredView)
            .containsExactlyInAnyOrder(
                tuple(
                    resourceGroupLink.getResource(),
                    true),
                tuple(
                    submissionDetail.getSubmission().getResource(),
                    false
                ));
        assertThat(captor.getAllValues().get(0).getResourceGroupId())
            .isEqualTo(captor.getAllValues().get(1).getResourceGroupId());
      }

      @Test
      void AndAddingToExistingGroup_whenPreferredView() {
        form.setPreferredView(true);
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
                ResourceGroupLink::getResourceGroupId,
                ResourceGroupLink::getPreferredView)
            .containsExactly(
                resource,
                groupId,
                true
            );
        verify(resourceGroupService, times(1))
            .setCurrentlyPreferredViewAsNotPreferred(existingGroupLink1, groupId);
      }

      @Test
      void AndAddingToExistingGroup_whenNotPreferredView() {
        form.setPreferredView(false);
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
                ResourceGroupLink::getResourceGroupId,
                ResourceGroupLink::getPreferredView)
            .containsExactly(
                resource,
                groupId,
                false
            );
        verify(resourceGroupService, never())
            .setCurrentlyPreferredViewAsNotPreferred(any(), any());
      }

      @Test
      void AndUpdatingCurrentResourceGroup_whenMoreThanTwoInGroupAndNotChangedPreferredView_doesNotChangeAnything() {
        form.setPreferredView(false);
        resourceGroupLink.setPreferredView(false);
        var existingGroupLink1 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);
        var existingGroupLink2 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);

        when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
            .thenReturn(submissionDetail);
        when(resourceGroupService.getResourceGroupLinkByResource(
            submissionDetail.getSubmission().getResource()))
            .thenReturn(Optional.of(existingGroupLink1));
        when(resourceGroupService.getResourceGroupLinksByResourceGroupId(existingGroupLink1.getResourceGroupId()))
            .thenReturn(List.of(existingGroupLink1, resourceGroupLink, existingGroupLink2));

        underTest.updateResourceGroupLinkFromResource(resource, form);

        verifyNoMoreInteractions(resourceGroupLinksRepository);
        verifyNoMoreInteractions(resourceGroupService);
      }

      @Test
      void AndUpdatingCurrentResourceGroup_whenMoreThanTwoInGroupAndChangedPreferredView_verifyMethodCalls() {
        form.setPreferredView(true);
        resourceGroupLink.setPreferredView(false);
        var existingGroupLink1 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);
        var existingGroupLink2 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);

        when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
            .thenReturn(submissionDetail);
        when(resourceGroupService.getResourceGroupLinkByResource(
            submissionDetail.getSubmission().getResource()))
            .thenReturn(Optional.of(existingGroupLink1));
        when(resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupLink.getResourceGroupId()))
            .thenReturn(List.of(existingGroupLink1, resourceGroupLink, existingGroupLink2));

        underTest.updateResourceGroupLinkFromResource(resource, form);

        verify(resourceGroupLinksRepository, times(1))
            .save(resourceGroupLink);
        verify(resourceGroupService, times(1))
            .setCurrentlyPreferredViewAsNotPreferred(resourceGroupLink, resourceGroupLink.getResourceGroupId());
      }

      @Test
      void AndUpdatingCurrentResourceGroup_whenTwoInGroupAndChangedPreferredView_verifyMethodCalls() {
        form.setPreferredView(false);
        resourceGroupLink.setPreferredView(true);
        var existingGroupLink1 = getResourceGroupLink(resourceGroupLink.getResourceGroupId(), resource);
        var otherResource = new Resource();
        otherResource.setId(UUID.randomUUID());
        existingGroupLink1.setResource(otherResource);
        when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
            .thenReturn(submissionDetail);
        when(resource.getId()).thenReturn(UUID.randomUUID());
        when(resourceGroupService.getResourceGroupLinkByResource(
            submissionDetail.getSubmission().getResource()))
            .thenReturn(Optional.of(existingGroupLink1));
        when(resourceGroupService.getResourceGroupLinksByResourceGroupId(any()))
            .thenReturn(List.of(existingGroupLink1, resourceGroupLink));

        underTest.updateResourceGroupLinkFromResource(resource, form);

        verify(resourceGroupLinksRepository, times(1))
            .saveAll(List.of(existingGroupLink1, resourceGroupLink));
      }
    }
  }

  private ResourceGroupLink getResourceGroupLink(UUID resourceGroupId, Resource resource) {
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setResourceGroupId(resourceGroupId);
    resourceGroupLink.setPreferredView(false);
    return resourceGroupLink;
  }
}