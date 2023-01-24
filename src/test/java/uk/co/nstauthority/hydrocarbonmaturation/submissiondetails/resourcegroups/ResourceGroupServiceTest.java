package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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
  private RelatedProspectsRepository relatedProspectsRepository;
  @Mock
  private ResourceGroupLinksRepository resourceGroupLinksRepository;
  @Mock
  private RelatedProspectsSubmissionService relatedProspectsSubmissionService;
  @InjectMocks
  private ResourceGroupService resourceGroupService;

  @Nested
  class WhenCreatingResourceGroupLink_AddToGroup {
    private SubmissionDetail submissionDetail;
    private RelatedProspect relatedProspectTaskList;
    private ResourceGroupLink resourceGroupLinkForRelatedProspect;

    @BeforeEach
    void setup() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

      when(relatedProspectsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

      var relatedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

      relatedProspectTaskList = buildRelatedProspect(relatedSubmissionDetail);
      when(relatedProspectsRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(relatedProspectTaskList));

      resourceGroupLinkForRelatedProspect = ResourceGroupService.buildResourceGroupLink(UUID.randomUUID(), relatedSubmissionDetail.getResourceId());
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
      when(resourceGroupLinksRepository.findByResourceGroupIdAndPreferredViewIsTrue(resourceGroupLinkForRelatedProspect.getResourceGroupId()))
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
      relatedProspectTaskList.setPreferredView(false);
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
  class WhenCreatingResourceGroupLink_CreateNewGroup {
    private SubmissionDetail submissionDetail;
    private RelatedProspect relatedProspectTaskList;
    private SubmissionDetail relatedSubmissionDetail;

    @BeforeEach
    void setup() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

      when(relatedProspectsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

      relatedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

      relatedProspectTaskList = buildRelatedProspect(relatedSubmissionDetail);
      when(relatedProspectsRepository.findBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(relatedProspectTaskList));

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
      relatedProspectTaskList.setPreferredView(false);

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
  private RelatedProspect buildRelatedProspect(SubmissionDetail submissionDetail) {
    var relatedProspect = new RelatedProspect();
    relatedProspect.setRelatedToAnotherProspect(true);
    relatedProspect.setRelatedSubmissionDetail(submissionDetail);
    relatedProspect.setPreferredView(true);
    return relatedProspect;
  }
}