package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

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
            relatedSubmissionDetail.getResourceId());
        when(resourceGroupLinksRepository.findByResource(relatedSubmissionDetail.getResourceId()))
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
                submissionDetail.getResourceId()
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
                submissionDetail.getResourceId()
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
                submissionDetail.getResourceId()
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

        when(resourceGroupLinksRepository.findByResource(relatedSubmissionDetail.getResourceId()))
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
                    submissionDetail.getResourceId()
                ),
                tuple(
                    false,
                    relatedSubmissionDetail.getResourceId()
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
                    submissionDetail.getResourceId()
                ),
                tuple(
                    true,
                    relatedSubmissionDetail.getResourceId()
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

  private RelatedResource buildRelatedProspect(SubmissionDetail submissionDetail) {
    var relatedProspect = new RelatedResource();
    relatedProspect.setRelatedToAnotherResource(true);
    relatedProspect.setRelatedSubmissionDetail(submissionDetail);
    relatedProspect.setPreferredView(true);
    return relatedProspect;
  }


}