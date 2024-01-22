package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesSubmissionService;

@ExtendWith(MockitoExtension.class)
class ResourceGroupServiceTest {

  @Mock
  private RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  @Mock
  private ResourceGroupLinksRepository resourceGroupLinksRepository;
  @Mock
  private RelatedResourcesSubmissionService relatedResourcesSubmissionService;
  @InjectMocks
  private ResourceGroupService resourceGroupService;

  @Test
  void getResourceGroupLinkByResource_assertReturn() {
    var resource = mock(Resource.class);
    var resourceGroupLink = mock(ResourceGroupLink.class);
    when(resourceGroupLinksRepository.findByResource(resource))
        .thenReturn(Optional.of(resourceGroupLink));

    assertThat(resourceGroupService.getResourceGroupLinkByResource(resource))
        .contains(resourceGroupLink);
  }

  @Test
  void getResourceGroupLinksByResourceGroupId_assertReturn() {
    var resourceGroupId = UUID.randomUUID();
    var resourceGroupLink = mock(ResourceGroupLink.class);
    when(resourceGroupLinksRepository.findByResourceGroupId(resourceGroupId))
        .thenReturn(List.of(resourceGroupLink));

    assertThat(resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupId))
        .containsExactly(resourceGroupLink);
  }


  @Nested
  class WhenCreatingResourceGroupLink {
    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setUp() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

      when(relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    }

    @Nested
    class RelatedToAProspectGroup_AddToGroup {

      private RelatedResource relatedResourceTaskList;
      private ResourceGroupLink resourceGroupLinkForRelatedProspect;

      @BeforeEach
      void setup() {

        var relatedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
        relatedResourceTaskList = buildRelatedProspect(relatedSubmissionDetail);
        when(relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail))
            .thenReturn(Optional.of(relatedResourceTaskList));

        resourceGroupLinkForRelatedProspect = ResourceGroupService.buildResourceGroupLink(UUID.randomUUID(),
            relatedSubmissionDetail.getSubmission().getResource());
        when(resourceGroupLinksRepository.findByResource(relatedSubmissionDetail.getSubmission().getResource()))
            .thenReturn(Optional.of(resourceGroupLinkForRelatedProspect));
      }

      @Test
      void currentProspectIsToBePreferredView_RelatedProspectIsAlreadyPreferredView() {
        resourceGroupLinkForRelatedProspect.setPreferredView(true);

        var argumentCaptor = ArgumentCaptor.forClass(ResourceGroupLink.class);
        resourceGroupService.createResourceGroupLink(submissionDetail);

        verify(resourceGroupLinksRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue())
            .extracting(
                ResourceGroupLink::getPreferredView,
                ResourceGroupLink::getResourceGroupId,
                ResourceGroupLink::getResource
            )
            .containsExactly(
                true,
                resourceGroupLinkForRelatedProspect.getResourceGroupId(),
                submissionDetail.getSubmission().getResource()
            );

        assertThat(resourceGroupLinkForRelatedProspect.getPreferredView()).isFalse();
      }

      @Test
      void currentProspectIsToBePreferredView_OtherProspectIsPreferredView() {
        resourceGroupLinkForRelatedProspect.setPreferredView(false);

        var otherResourceGroupLink = ResourceGroupService.buildResourceGroupLink(UUID.randomUUID(), new Resource());
        when(resourceGroupLinksRepository.findByResourceGroupIdAndPreferredViewIsTrue(
            resourceGroupLinkForRelatedProspect.getResourceGroupId()))
            .thenReturn(Optional.of(otherResourceGroupLink));

        var argumentCaptor = ArgumentCaptor.forClass(ResourceGroupLink.class);
        resourceGroupService.createResourceGroupLink(submissionDetail);

        verify(resourceGroupLinksRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue())
            .extracting(
                ResourceGroupLink::getPreferredView,
                ResourceGroupLink::getResourceGroupId,
                ResourceGroupLink::getResource
            )
            .containsExactly(
                true,
                resourceGroupLinkForRelatedProspect.getResourceGroupId(),
                submissionDetail.getSubmission().getResource()
            );

        assertThat(resourceGroupLinkForRelatedProspect.getPreferredView()).isFalse();
        assertThat(otherResourceGroupLink.getPreferredView()).isFalse();
      }

      @Test
      void currentProspectIsNotToBePreferredView() {
        relatedResourceTaskList.setPreferredView(false);
        resourceGroupLinkForRelatedProspect.setPreferredView(true);

        var argumentCaptor = ArgumentCaptor.forClass(ResourceGroupLink.class);
        resourceGroupService.createResourceGroupLink(submissionDetail);

        verify(resourceGroupLinksRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue())
            .extracting(
                ResourceGroupLink::getPreferredView,
                ResourceGroupLink::getResourceGroupId,
                ResourceGroupLink::getResource
            )
            .containsExactly(
                false,
                resourceGroupLinkForRelatedProspect.getResourceGroupId(),
                submissionDetail.getSubmission().getResource()
            );

        assertThat(resourceGroupLinkForRelatedProspect.getPreferredView()).isTrue();
      }

    }

    @Nested
    class RelatedToAnotherProspect_CreateNewGroup {
      private RelatedResource relatedResourceTaskList;
      private SubmissionDetail relatedSubmissionDetail;

      @BeforeEach
      void setup() {

        relatedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

        relatedResourceTaskList = buildRelatedProspect(relatedSubmissionDetail);
        when(relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail))
            .thenReturn(Optional.of(relatedResourceTaskList));

        when(resourceGroupLinksRepository.findByResource(relatedSubmissionDetail.getSubmission().getResource()))
            .thenReturn(Optional.empty());
      }

      @Test
      void currentProspectIsToBePreferredView() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ResourceGroupLink>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        resourceGroupService.createResourceGroupLink(submissionDetail);

        verify(resourceGroupLinksRepository).saveAll(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue())
            .hasSize(2)
            .extracting(
                ResourceGroupLink::getPreferredView,
                ResourceGroupLink::getResource
            )
            .containsExactly(
                tuple(
                    true,
                    submissionDetail.getSubmission().getResource()
                ),
                tuple(
                    false,
                    relatedSubmissionDetail.getSubmission().getResource()
                )
            );

        assertThat(argumentCaptor.getValue().get(0).getResourceGroupId())
            .isEqualTo(argumentCaptor.getValue().get(1).getResourceGroupId());
      }

      @Test
      void relatedProspectIsToBePreferredView() {
        relatedResourceTaskList.setPreferredView(false);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ResourceGroupLink>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        resourceGroupService.createResourceGroupLink(submissionDetail);

        verify(resourceGroupLinksRepository).saveAll(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue())
            .hasSize(2)
            .extracting(
                ResourceGroupLink::getPreferredView,
                ResourceGroupLink::getResource
            )
            .containsExactly(
                tuple(
                    false,
                    submissionDetail.getSubmission().getResource()
                ),
                tuple(
                    true,
                    relatedSubmissionDetail.getSubmission().getResource()
                )
            );

        assertThat(argumentCaptor.getValue().get(0).getResourceGroupId())
            .isEqualTo(argumentCaptor.getValue().get(1).getResourceGroupId());
      }
    }

    @Nested
    class NotRelatedToAnotherProspect {
      @BeforeEach
      void setup() {
        when(relatedResourcesPersistenceService.getRelatedResourceIfRelatedToAnotherResource(submissionDetail))
            .thenReturn(Optional.empty());
      }

      @Test
      void dontCreateResourceGroupLink() {
        resourceGroupService.createResourceGroupLink(submissionDetail);

        verify(resourceGroupLinksRepository, never()).saveAll(anyList());
        verify(resourceGroupLinksRepository, never()).save(any(ResourceGroupLink.class));
      }
    }
  }

  @Test
  void isInGroupAndIsNotPreferredView_verifyMethodCall() {
    var resource = new Resource();
    resourceGroupService.isInGroupAndIsNotPreferredView(resource);

    verify(resourceGroupLinksRepository).existsResourceGroupLinkByResourceAndPreferredViewIs(resource, false);
  }

  @Test
  void isPreferredView_verifyMethodCall() {
    var resource = new Resource();
    resourceGroupService.isPreferredView(resource);

    verify(resourceGroupLinksRepository).existsResourceGroupLinkByResourceAndPreferredViewIs(resource, true);
  }

  @Test
  void removeResourceFromGroup_whenOnlyTwoInGroupToStart() {
    var resource1 = new Resource();
    var resourceGroupLinkFromResource1 = new ResourceGroupLink();
    var resource2 = new Resource();
    var resourceGroupLinkFromResource2 = new ResourceGroupLink();
    var resourceGroupId = UUID.randomUUID();
    resourceGroupLinkFromResource1.setResource(resource1);
    resourceGroupLinkFromResource1.setResourceGroupId(resourceGroupId);
    resourceGroupLinkFromResource2.setResource(resource2);
    resourceGroupLinkFromResource2.setResourceGroupId(resourceGroupId);
    var resourceGroupLinkList = List.of(resourceGroupLinkFromResource1, resourceGroupLinkFromResource2);
    when(resourceGroupLinksRepository.findByResource(resource1)).thenReturn(Optional.of(resourceGroupLinkFromResource1));
    when(resourceGroupLinksRepository.findByResourceGroupId(resourceGroupId))
        .thenReturn(resourceGroupLinkList);
    resourceGroupService.removeResourceFromGroup(resource1);
    verify(resourceGroupLinksRepository).deleteAll(resourceGroupLinkList);
  }

  @Test
  void removeResourceFromGroup_whenMoreThanTwoInGroupToStart() {
    var resource1 = new Resource();
    var resourceGroupLinkFromResource1 = new ResourceGroupLink();
    var resource2 = new Resource();
    var resourceGroupLinkFromResource2 = new ResourceGroupLink();
    var resource3 = new Resource();
    var resourceGroupLinkFromResource3 = new ResourceGroupLink();
    var resourceGroupId = UUID.randomUUID();
    resourceGroupLinkFromResource1.setResource(resource1);
    resourceGroupLinkFromResource1.setResourceGroupId(resourceGroupId);
    resourceGroupLinkFromResource2.setResource(resource2);
    resourceGroupLinkFromResource2.setResourceGroupId(resourceGroupId);
    resourceGroupLinkFromResource3.setResource(resource3);
    resourceGroupLinkFromResource3.setResourceGroupId(resourceGroupId);
    var resourceGroupLinkList = List.of(
        resourceGroupLinkFromResource1, resourceGroupLinkFromResource2, resourceGroupLinkFromResource3
    );
    when(resourceGroupLinksRepository.findByResource(resource1)).thenReturn(Optional.of(resourceGroupLinkFromResource1));
    when(resourceGroupLinksRepository.findByResourceGroupId(resourceGroupId))
        .thenReturn(resourceGroupLinkList);
    resourceGroupService.removeResourceFromGroup(resource1);
    verify(resourceGroupLinksRepository).delete(resourceGroupLinkFromResource1);
  }

  private RelatedResource buildRelatedProspect(SubmissionDetail submissionDetail) {
    var relatedProspect = new RelatedResource();
    relatedProspect.setRelatedToAnotherResource(true);
    relatedProspect.setRelatedSubmissionDetail(submissionDetail);
    relatedProspect.setPreferredView(true);
    return relatedProspect;
  }


}