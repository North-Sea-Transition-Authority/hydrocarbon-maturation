package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType.PROSPECT;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class ResourceGroupQueryServiceTest {
  @Mock
  private ResourceGroupLinksRepository resourceGroupLinksRepository;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @InjectMocks
  private ResourceGroupQueryService resourceGroupQueryService;

  @Nested
  class WhenGettingProspectsAndGroupsByName {
    private final String SEARCH_TERM = "SEARCH_TERM";
    private final String RESOURCE_NAME = "RESOURCE_NAME";
    private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    @BeforeEach
    void setup() {
      var resource = mock(Resource.class);
      when(submissionDetailService.getResourcesByTypeAndSubmissionDetailsResourceName(PROSPECT, SEARCH_TERM))
          .thenReturn(List.of(resource));

      when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource))
          .thenReturn(Optional.of(submissionDetail));

      when(submissionDetailService.getResourceNameOrDefault(submissionDetail)).thenReturn(RESOURCE_NAME);
    }

    @Test
    void prospectIsInGroupAndPreferredView_ReturnToListWithGroupPrefix() {

      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setPreferredView(true);
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getResourceId())).thenReturn(Optional.of(resourceGroupLink));

      var resultList = resourceGroupQueryService.getProspectsAndGroupsByName(SEARCH_TERM);

      assertThat(resultList)
          .extracting(
              ResourceGroupView::getSelectionId,
              ResourceGroupView::getSelectionText
          )
          .containsExactly(
              tuple(
                  submissionDetail.getId().toString(),
                  ResourceGroupQueryService.GROUP_PREFIX + RESOURCE_NAME
              )
          );
    }

    @Test
    void prospectIsInGroupButNotPreferredView_DontReturnToList() {

      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setPreferredView(false);
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getResourceId())).thenReturn(Optional.of(resourceGroupLink));

      var resultList = resourceGroupQueryService.getProspectsAndGroupsByName(SEARCH_TERM);

      assertThat(resultList).isEmpty();
    }

    @Test
    void prospectIsNotInGroup_ReturnToList() {

      when(resourceGroupLinksRepository.findByResource(submissionDetail.getResourceId())).thenReturn(Optional.empty());

      var resultList = resourceGroupQueryService.getProspectsAndGroupsByName(SEARCH_TERM);

      assertThat(resultList)
          .extracting(
              ResourceGroupView::getSelectionId,
              ResourceGroupView::getSelectionText
          )
          .containsExactly(
              tuple(
                  submissionDetail.getId().toString(),
                  RESOURCE_NAME
              )
          );
    }
  }
}