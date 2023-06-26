package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType.DISCOVERY;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;

@ExtendWith(MockitoExtension.class)
class ResourceGroupQueryServiceTest {

  private final String SEARCH_TERM = "SEARCH_TERM";
  private final String RESOURCE_NAME = "RESOURCE_NAME";

  @Mock
  private ResourceGroupLinksRepository resourceGroupLinksRepository;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private SubmissionResourceService submissionResourceService;
  @InjectMocks
  private ResourceGroupQueryService resourceGroupQueryService;

  @Nested
  class WhenGettingProspectsAndGroupsByName {
    private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    @BeforeEach
    void setup() {
      var resource = ResourceTestUtils.buildResource(PROSPECT);
      submissionDetail.getSubmission().setResource(resource);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(PROSPECT, SEARCH_TERM))
          .thenReturn(List.of(resource));

      when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource))
          .thenReturn(Optional.of(submissionDetail));

      when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn(RESOURCE_NAME);
    }

    @Test
    void prospectIsInGroupAndPreferredView_ReturnToListWithGroupPrefix() {

      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setPreferredView(true);
      resourceGroupLink.setResource(submissionDetail.getSubmission().getResource());
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.of(resourceGroupLink));

      var resultList = resourceGroupQueryService.getProspectsAndGroupsByName(SEARCH_TERM, null);

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
      resourceGroupLink.setResource(submissionDetail.getSubmission().getResource());
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource())).thenReturn(Optional.of(resourceGroupLink));

      var resultList = resourceGroupQueryService.getProspectsAndGroupsByName(SEARCH_TERM, null);

      assertThat(resultList).isEmpty();
    }

    @Test
    void prospectIsNotInGroup_ReturnToList() {

      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.empty());

      var resultList = resourceGroupQueryService.getProspectsAndGroupsByName(SEARCH_TERM, null);

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

  @Nested
  class WhenFilteringOutCurrentProspect {
    private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    @BeforeEach
    void setUp() {
      var resource = ResourceTestUtils.buildResource(PROSPECT);
      submissionDetail.getSubmission().setResource(resource);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(PROSPECT, SEARCH_TERM))
          .thenReturn(List.of(resource));
    }

    @Test
    void onlyCurrentProspectFound_filterOut_assertNoReturn() {
      var resultList = resourceGroupQueryService.getProspectsAndGroupsByName(
          SEARCH_TERM, submissionDetail.getSubmission().getResource().getId());

      assertThat(resultList).isEmpty();
    }
  }

  @Nested
  class WhenGettingDiscoveriesAndGroupsByName {
    private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    @BeforeEach
    void setup() {
      var resource = ResourceTestUtils.buildResource(DISCOVERY);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(DISCOVERY, SEARCH_TERM))
          .thenReturn(List.of(resource));

      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
          .thenReturn(Optional.of(submissionDetail));

      when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn(RESOURCE_NAME);
    }

    @Test
    void discoveryIsInGroupAndPreferredView_ReturnToListWithGroupPrefix() {

      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setPreferredView(true);
      resourceGroupLink.setResource(submissionDetail.getSubmission().getResource());

      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.of(resourceGroupLink));

      var resultList = resourceGroupQueryService.getDiscoveriesAndGroupsByName(SEARCH_TERM, null);

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
    void discoveryIsInGroupButNotPreferredView_DontReturnToList() {

      var resourceGroupLink = new ResourceGroupLink();
      resourceGroupLink.setPreferredView(false);
      resourceGroupLink.setResource(submissionDetail.getSubmission().getResource());
      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.of(resourceGroupLink));

      var resultList = resourceGroupQueryService.getDiscoveriesAndGroupsByName(SEARCH_TERM, null);

      assertThat(resultList).isEmpty();
    }

    @Test
    void discoveryIsNotInGroup_ReturnToList() {

      when(resourceGroupLinksRepository.findByResource(submissionDetail.getSubmission().getResource()))
          .thenReturn(Optional.empty());

      var resultList = resourceGroupQueryService.getDiscoveriesAndGroupsByName(SEARCH_TERM, null);

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

  @Nested
  class WhenFilteringOutCurrentDiscovery {
    private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();

    @BeforeEach
    void setUp() {
      var resource = ResourceTestUtils.buildResource(DISCOVERY);
      submissionDetail.getSubmission().setResource(resource);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(DISCOVERY, SEARCH_TERM))
          .thenReturn(List.of(resource));
    }

    @Test
    void onlyCurrentProspectFound_filterOut_assertNoReturn() {
      var resultList = resourceGroupQueryService.getDiscoveriesAndGroupsByName(
          SEARCH_TERM, submissionDetail.getSubmission().getResource().getId());

      assertThat(resultList).isEmpty();
    }
  }
}