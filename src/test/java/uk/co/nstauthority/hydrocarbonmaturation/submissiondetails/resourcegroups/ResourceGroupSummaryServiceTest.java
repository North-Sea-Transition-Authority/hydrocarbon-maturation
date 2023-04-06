package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class ResourceGroupSummaryServiceTest {

  @Mock
  private ResourceGroupService resourceGroupService;
  @Mock
  private SubmissionResourceService submissionResourceService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @InjectMocks
  private ResourceGroupSummaryService resourceGroupSummaryService;

  private final Resource resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
  private ResourceGroupLink resourceGroupLink = new ResourceGroupLink();

  @Test
  void getResourceGroupLinkSummaryFromResource_assertSingleResourceReturned() {
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(Optional.of(resourceGroupLink));
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setPreferredView(true);
    when(resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupLink.getResourceGroupId()))
        .thenReturn(List.of(resourceGroupLink));
    var expectedNameOnResource = "resource 1";
    mockLatestApprovedSubmission(resource, expectedNameOnResource);
    var summaryView = resourceGroupSummaryService
        .getResourceGroupLinkSummaryFromResource(resource);

    assertThat(summaryView)
        .extracting(
            ResourceGroupSummaryView::resourceType,
            ResourceGroupSummaryView::isPreferredView)
        .containsExactly(
            ResourceType.PROSPECT,
            "Yes"
        );

    assertThat(summaryView.resourcesInGroup())
        .hasSize(1);

    assertThat(summaryView.resourcesInGroup().get(0))
        .extracting(
            ResourceItemSummaryView::resourceName,
            ResourceItemSummaryView::disableLink,
            ResourceItemSummaryView::viewSorUrl,
            ResourceItemSummaryView::preferredView)
        .containsExactly(expectedNameOnResource,
            true,
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId())),
            "(preferred view)"
        );
  }

  @Test
  void getResourceGroupLinkSummaryFromResource_whenMultipleResourcesLinked_assertReturnAndOrder() {
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(Optional.of(resourceGroupLink));
    resource.setType(ResourceType.DISCOVERY);
    resource.setCreatedTimestamp(Instant.now().plusSeconds(200));
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setResourceGroupId(UUID.randomUUID());
    resourceGroupLink.setPreferredView(false);
    var resource2 = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    resource2.setCreatedTimestamp(Instant.now());
    var resourceGroupLink2 = new ResourceGroupLink();
    resourceGroupLink2.setResource(resource2);
    resourceGroupLink2.setPreferredView(true);
    resourceGroupLink2.setResourceGroupId(resourceGroupLink.getId());
    when(resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupLink.getResourceGroupId()))
        .thenReturn(List.of(resourceGroupLink, resourceGroupLink2));
    var expectedNameOnResource = "resource 1";
    var expectedNameOnOtherResource = "the other resource";
    mockLatestApprovedSubmission(resource, expectedNameOnResource);
    mockLatestApprovedSubmission(resource2, expectedNameOnOtherResource);

    var summaryView = resourceGroupSummaryService
        .getResourceGroupLinkSummaryFromResource(resource);

    assertThat(summaryView)
        .extracting(
            ResourceGroupSummaryView::resourceType,
            ResourceGroupSummaryView::isPreferredView)
        .containsExactly(
            resource.getType(),
            "No"
        );

    assertThat(summaryView.resourcesInGroup())
        .extracting(
            ResourceItemSummaryView::resourceName,
            ResourceItemSummaryView::disableLink,
            ResourceItemSummaryView::viewSorUrl,
            ResourceItemSummaryView::preferredView)
        .containsExactly(
            tuple(
                expectedNameOnResource,
                true,
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId())),
                ""
            ),
            tuple(
                expectedNameOnOtherResource,
                false,
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource2.getId())),
                "(preferred view)"
            )
        );
  }

  @ParameterizedTest
  @EnumSource(ResourceType.class)
  void getResourceGroupLinkSummaryFromResource_whenNoLatestSubmissionFound_assertThrows(ResourceType resourceType) {
    resource.setType(resourceType);
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setResourceGroupId(UUID.randomUUID());
    resourceGroupLink.setPreferredView(false);
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(Optional.of(resourceGroupLink));
    when(resourceGroupService.getResourceGroupLinksByResourceGroupId(resourceGroupLink.getResourceGroupId()))
        .thenReturn(List.of(resourceGroupLink));

    assertThatThrownBy(() -> resourceGroupSummaryService
        .getResourceGroupLinkSummaryFromResource(resource))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Could not find latest approved submission for resource %s".formatted(resource.getId()));
  }

  private void mockLatestApprovedSubmission(Resource resource, String expectedResourceName) {
    var mockSubmissionDetail = mock(SubmissionDetail.class);
    if (resource.getType() == ResourceType.PROSPECT) {
      when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource))
          .thenReturn(Optional.of(mockSubmissionDetail));
    } else {
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
          .thenReturn(Optional.of(mockSubmissionDetail));
    }
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(mockSubmissionDetail))
        .thenReturn(expectedResourceName);
  }
}