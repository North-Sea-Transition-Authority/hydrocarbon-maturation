package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class ResourceGroupSummaryServiceTest {

  private static final String ACTIVE = "(active)";
  private static final String RESOURCE_NAME = "resource name";
  private static final String OTHER_RESOURCE_NAME = "other resource";

  @Mock
  private ResourceGroupService resourceGroupService;
  @Mock
  private ResourceGroupQueryService resourceGroupQueryService;
  @Mock
  private SubmissionResourceService submissionResourceService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @InjectMocks
  private ResourceGroupSummaryService resourceGroupSummaryService;

  private final Resource resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
  private final ResourceGroupLink resourceGroupLink = new ResourceGroupLink();

  @Test
  void getResourceGroupLinkSummaryFromResource_assertSingleResourceReturned() {
    var resourceGroup = new ResourceGroup();
    resourceGroupLink.setResourceGroup(resourceGroup);
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(resourceGroupLink);
    resourceGroupLink.setResource(resource);
    var summaryView = resourceGroupSummaryService
        .getResourceGroupLinkSummaryFromResource(resource);

    assertThat(summaryView)
        .extracting(
            ResourceGroupSummaryView::resourceType,
            ResourceGroupSummaryView::isActive)
        .containsExactly(
            ResourceType.PROSPECT,
            ""
        );
  }

  @Test
  void getResourceGroupLinkSummaryFromResource_whenMultipleResourcesLinked_assertReturnAndOrder() {
    var resourceGroup = new ResourceGroup();
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(resourceGroupLink);
    resource.setType(ResourceType.DISCOVERY);
    resource.setCreatedTimestamp(Instant.now().plusSeconds(200));
    resource.setActive(false);
    resourceGroupLink.setResource(resource);
    resourceGroupLink.setResourceGroup(resourceGroup);
    var resource2 = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    resource2.setCreatedTimestamp(Instant.now());
    var resourceGroupLink2 = new ResourceGroupLink();
    resourceGroupLink2.setResource(resource2);
    resourceGroupLink2.setResourceGroup(resourceGroup);
    when(resourceGroupService.getResourceGroupLinksByResourceGroup(resourceGroup))
        .thenReturn(List.of(resourceGroupLink, resourceGroupLink2));
    when(resourceGroupQueryService.resourceIsInGroupWithOtherResources(resourceGroupLink)).thenReturn(true);
    mockLatestApprovedSubmission(
        resource,
        RESOURCE_NAME,
        List.of(
          SubmissionType.CREATE_DISCOVERY,
          SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
          SubmissionType.UPDATE_DISCOVERY,
          SubmissionType.RELINQUISHMENT_DISCOVERY
        )
    );
    mockLatestApprovedSubmission(
        resource2,
        OTHER_RESOURCE_NAME,
        List.of(
            SubmissionType.CREATE_DISCOVERY,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.UPDATE_DISCOVERY,
            SubmissionType.RELINQUISHMENT_DISCOVERY
        )
    );

    var summaryView = resourceGroupSummaryService
        .getResourceGroupLinkSummaryFromResource(resource);

    assertThat(summaryView)
        .extracting(
            ResourceGroupSummaryView::resourceType,
            ResourceGroupSummaryView::isActive)
        .containsExactly(
            resource.getType(),
            "No"
        );

    assertThat(summaryView.resourcesInGroup())
        .extracting(
            ResourceItemSummaryView::resourceName,
            ResourceItemSummaryView::disableLink,
            ResourceItemSummaryView::viewSorUrl,
            ResourceItemSummaryView::isActive)
        .containsExactly(
            tuple(
                OTHER_RESOURCE_NAME,
                false,
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource2.getId(), null)),
                ACTIVE
            ),
            tuple(
                RESOURCE_NAME,
                true,
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)),
                ""
            )
        );
  }

  @Test
  void getResourceGroupMembersSummaryWithoutCurrentResource_whenMultipleResourcesLinked_assertReturn() {
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(resourceGroupLink);
    resource.setType(ResourceType.DISCOVERY);
    resource.setCreatedTimestamp(Instant.now().plusSeconds(200));
    resourceGroupLink.setResource(resource);
    var resourceGroup = new ResourceGroup();
    resourceGroupLink.setResourceGroup(resourceGroup);
    var resource2 = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    resource2.setCreatedTimestamp(Instant.now());
    var resourceGroupLink2 = new ResourceGroupLink();
    resourceGroupLink2.setResource(resource2);
    resourceGroupLink2.setResourceGroup(resourceGroupLink.getResourceGroup());
    when(resourceGroupService.getResourceGroupLinksByResourceGroup(resourceGroupLink.getResourceGroup()))
        .thenReturn(List.of(resourceGroupLink, resourceGroupLink2));

    mockLatestApprovedSubmission(
        resource2,
        OTHER_RESOURCE_NAME,
        List.of(
            SubmissionType.CREATE_DISCOVERY,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.UPDATE_DISCOVERY,
            SubmissionType.RELINQUISHMENT_DISCOVERY
        )
    );

    var summaryView = resourceGroupSummaryService
        .getResourceGroupMembersSummaryWithoutCurrentResource(resource);

    assertThat(summaryView)
        .extracting(
            ResourceItemSummaryView::resourceName,
            ResourceItemSummaryView::isActive)
        .containsExactly(
            tuple(
                OTHER_RESOURCE_NAME,
                ACTIVE
            )
        );
  }

  private void mockLatestApprovedSubmission(Resource resource,
                                            String expectedResourceName,
                                            List<SubmissionType> submissionTypes) {
    var mockSubmissionDetail = mock(SubmissionDetail.class);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes(resource, submissionTypes))
          .thenReturn(Optional.of(mockSubmissionDetail));
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(mockSubmissionDetail))
        .thenReturn(expectedResourceName);
  }
}