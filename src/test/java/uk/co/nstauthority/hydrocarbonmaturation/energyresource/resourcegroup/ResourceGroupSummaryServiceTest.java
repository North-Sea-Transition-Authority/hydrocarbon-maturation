package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class ResourceGroupSummaryServiceTest {

  private static final String PREFERRED_VIEW = "(preferred view)";
  private static final String RESOURCE_NAME = "resource name";
  private static final String OTHER_RESOURCE_NAME = "other resource";

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
    mockLatestApprovedSubmission(
        resource,
        expectedNameOnResource,
        List.of(
            SubmissionType.PROSPECT_UPDATE,
            SubmissionType.CREATE_PROSPECT,
            SubmissionType.RELINQUISHMENT_PROSPECT
        )
    );
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
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)),
            PREFERRED_VIEW
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
                RESOURCE_NAME,
                true,
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)),
                ""
            ),
            tuple(
                OTHER_RESOURCE_NAME,
                false,
                ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource2.getId(), null)),
                PREFERRED_VIEW
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
        .hasMessage("Could not find latest accepted submission for resource %s".formatted(resource.getId()));
  }

  @Test
  void getResourceGroupMembersSummaryWithoutCurrentResource_whenMultipleResourcesLinked_assertReturn() {
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
            ResourceItemSummaryView::preferredView)
        .containsExactly(
            tuple(
                OTHER_RESOURCE_NAME,
                PREFERRED_VIEW
            )
        );
  }

  @Test
  void getResourceGroupMembersSummaryWithoutCurrentResource_whenNoResourceLink_assertEmpty() {
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(Optional.empty());

    var summaryView = resourceGroupSummaryService
        .getResourceGroupMembersSummaryWithoutCurrentResource(resource);

    assertThat(summaryView).isEmpty();
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