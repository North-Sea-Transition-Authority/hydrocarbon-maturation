package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionQueryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionResourceService submissionResourceService;

  @Mock
  private SubmissionFilterService submissionFilterService;

  @Mock
  private SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory;

  @Mock
  private RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;

  @Mock
  private OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService;

  @Mock
  private ResourceGroupService resourceGroupService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private InternalTeamService internalTeamService;

  @InjectMocks
  private SubmissionQueryService submissionQueryService;

  private final Resource resourceWithoutLatestApproved = new Resource();

  @Nested
  class GetLatestDiscoveriesByNameForSubmissionType {

    @ParameterizedTest
    @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY", "RELINQUISHMENT_DISCOVERY"})
    void whenDiscoverySubmission_assertOneFound(SubmissionType submissionType) {
      var searchTerm = "discovery";

      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.DISCOVERY, SubmissionDetailStatus.ACCEPTED, SubmissionType.UPDATE_DISCOVERY);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
          .thenReturn(List.of(
              latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource(),
              resourceWithoutLatestApproved));
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(
          latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()))
          .thenReturn(Optional.of(latestApprovedResourceDetail.getSubmissionDetail()));
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(
          resourceWithoutLatestApproved))
          .thenReturn(Optional.empty());
      when(submissionFilterService.filterDiscoverySubmissionByOtherSubmissions(
          latestApprovedResourceDetail.getSubmissionDetail(), submissionType))
          .thenReturn(true);
      when(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy())
          .thenReturn(operatorSubmissionSearchSelectorService);

      ArgumentCaptor<Stream<SubmissionDetail>> lastestSubmissionStream = ArgumentCaptor.forClass(Stream.class);

      when(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissionsWithTeam(
          lastestSubmissionStream.capture(), eq(submissionType)))
          .thenReturn(Stream.of(latestApprovedResourceDetail));
      when(submissionResourceService.getResourceDisplayName(latestApprovedResourceDetail))
          .thenReturn(searchTerm);
      var result = submissionQueryService
          .getLatestDiscoveriesByNameForSubmissionType(searchTerm, submissionType);

      assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(latestApprovedResourceDetail.getId().toString(), searchTerm)
        );

      assertThat(lastestSubmissionStream.getValue().toList())
          .containsExactly(latestApprovedResourceDetail.getSubmissionDetail());
    }

    @ParameterizedTest
    @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY", "RELINQUISHMENT_DISCOVERY"})
    void whenDiscoverySubmission_whenSubmissionPreventsSubmission_assertEmpty(SubmissionType submissionType) {
      var searchTerm = "discovery";
      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.DISCOVERY, SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_DISCOVERY);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
          .thenReturn(List.of(latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()));
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(
          latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()))
          .thenReturn(Optional.of(latestApprovedResourceDetail.getSubmissionDetail()));
      when(submissionFilterService.filterDiscoverySubmissionByOtherSubmissions(
          latestApprovedResourceDetail.getSubmissionDetail(), submissionType))
          .thenReturn(false);
      when(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy())
          .thenReturn(operatorSubmissionSearchSelectorService);

      ArgumentCaptor<Stream<SubmissionDetail>> lastestSubmissionStream = ArgumentCaptor.forClass(Stream.class);

      when(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissionsWithTeam(
          lastestSubmissionStream.capture(), eq(submissionType)))
          .thenReturn(Stream.of());

      var result = submissionQueryService
          .getLatestDiscoveriesByNameForSubmissionType(searchTerm, submissionType);

      assertThat(result).isEmpty();
      assertThat(lastestSubmissionStream.getValue().toList()).isEmpty();
    }
  }

  @Nested
  class GetLatestProspectsByNameForSubmissionType {

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {
            "PROSPECT_UPDATE",
            "POST_DRILL_DRY_HOLE",
            "CREATE_DISCOVERY_FROM_PROSPECT",
            "RELINQUISHMENT_PROSPECT"})
    void whenProspectSubmission_passesFilter_assertOneFound(SubmissionType submissionType) {
      var searchTerm = "prospect";

      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.PROSPECT, SubmissionDetailStatus.ACCEPTED, submissionType);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
          .thenReturn(List.of(latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()));
      when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
          latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()))
          .thenReturn(Optional.of(latestApprovedResourceDetail.getSubmissionDetail()));
      when(submissionFilterService.filterProspectSubmissionByOtherSubmissions(
          latestApprovedResourceDetail.getSubmissionDetail(), submissionType))
          .thenReturn(true);
      when(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy())
          .thenReturn(operatorSubmissionSearchSelectorService);

      ArgumentCaptor<Stream<SubmissionDetail>> lastestSubmissionStream = ArgumentCaptor.forClass(Stream.class);

      when(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissionsWithTeam(
          lastestSubmissionStream.capture(), eq(submissionType)))
          .thenReturn(Stream.of(latestApprovedResourceDetail));
      when(submissionResourceService.getResourceDisplayName(latestApprovedResourceDetail))
          .thenReturn(searchTerm);
      var result = submissionQueryService
          .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(latestApprovedResourceDetail.getId().toString(), searchTerm)
          );

      assertThat(lastestSubmissionStream.getValue().toList())
          .containsExactly(latestApprovedResourceDetail.getSubmissionDetail());
    }

    @ParameterizedTest
    @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY", "RELINQUISHMENT_DISCOVERY"})
    void whenProspectSubmission_whenSubmissionPreventsSubmission_assertEmpty(SubmissionType submissionType) {
      var searchTerm = "prospect";
      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.PROSPECT, SubmissionDetailStatus.ACCEPTED, submissionType);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
          .thenReturn(List.of(latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()));
      when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
          latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()))
          .thenReturn(Optional.of(latestApprovedResourceDetail.getSubmissionDetail()));
      when(submissionFilterService.filterProspectSubmissionByOtherSubmissions(
          latestApprovedResourceDetail.getSubmissionDetail(), submissionType))
          .thenReturn(false);
      when(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy())
          .thenReturn(operatorSubmissionSearchSelectorService);

      ArgumentCaptor<Stream<SubmissionDetail>> lastestSubmissionStream = ArgumentCaptor.forClass(Stream.class);

      when(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissionsWithTeam(
          lastestSubmissionStream.capture(), eq(submissionType)))
          .thenReturn(Stream.of());

      var result = submissionQueryService
          .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

      assertThat(result).isEmpty();
      assertThat(lastestSubmissionStream.getValue().toList()).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(
        value = SubmissionType.class,
        names = {
            "PROSPECT_UPDATE",
            "POST_DRILL_DRY_HOLE",
            "CREATE_DISCOVERY_FROM_PROSPECT",
            "RELINQUISHMENT_PROSPECT"})
    void whenProspectSubmission_whenTwoResourcesMatchesSubmission_assertEmpty(SubmissionType submissionType) {
      var searchTerm = "prospect";
      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.PROSPECT, SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_PROSPECT);
      var latestApprovedResourceDetail2 = createResourceDetail(
          searchTerm, ResourceType.PROSPECT, SubmissionDetailStatus.ACCEPTED, SubmissionType.PROSPECT_UPDATE);
      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
          .thenReturn(List.of(
              latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource(),
              latestApprovedResourceDetail2.getSubmissionDetail().getSubmission().getResource()));
      when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
          latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource()))
          .thenReturn(Optional.of(latestApprovedResourceDetail.getSubmissionDetail()));
      when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
          latestApprovedResourceDetail2.getSubmissionDetail().getSubmission().getResource()))
          .thenReturn(Optional.of(latestApprovedResourceDetail2.getSubmissionDetail()));
      when(submissionFilterService.filterProspectSubmissionByOtherSubmissions(
          latestApprovedResourceDetail.getSubmissionDetail(), submissionType))
          .thenReturn(true);
      when(submissionFilterService.filterProspectSubmissionByOtherSubmissions(
          latestApprovedResourceDetail2.getSubmissionDetail(), submissionType))
          .thenReturn(true);
      when(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy())
          .thenReturn(operatorSubmissionSearchSelectorService);

      ArgumentCaptor<Stream<SubmissionDetail>> lastestSubmissionStream = ArgumentCaptor.forClass(Stream.class);

      when(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissionsWithTeam(
          lastestSubmissionStream.capture(), eq(submissionType)))
          .thenReturn(Stream.of(
              latestApprovedResourceDetail,
              latestApprovedResourceDetail2));

      when(submissionResourceService.getResourceDisplayName(latestApprovedResourceDetail))
          .thenReturn(searchTerm);
      when(submissionResourceService.getResourceDisplayName(latestApprovedResourceDetail2))
          .thenReturn(searchTerm);
      var result = submissionQueryService
          .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(latestApprovedResourceDetail.getId().toString(), searchTerm),
              tuple(latestApprovedResourceDetail2.getId().toString(), searchTerm)
          );

      assertThat(lastestSubmissionStream.getValue().toList())
          .containsExactly(
              latestApprovedResourceDetail.getSubmissionDetail(),
              latestApprovedResourceDetail2.getSubmissionDetail());
    }
  }

  @Nested
  class GetLatestResourceSubmissionsByName {

    @Test
    void whenTwoResources_assertTwoReturned() {
      var searchTerm = "resource";
      var latestApprovedProspectName = "prospect resource";
      var latestApprovedDiscoveryName = "discovery resource";
      var latestApprovedProspectResourceDetail = createResourceDetail(
          latestApprovedProspectName, ResourceType.PROSPECT,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_PROSPECT);
      var latestApprovedDiscoveryResourceDetail = createResourceDetail(
          latestApprovedDiscoveryName, ResourceType.DISCOVERY,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_DISCOVERY);

      var prospectResource = latestApprovedProspectResourceDetail.getSubmissionDetail().getSubmission().getResource();
      var discoveryResource = latestApprovedDiscoveryResourceDetail.getSubmissionDetail().getSubmission().getResource();
      when(submissionResourceService.getResourcesByName(searchTerm)).thenReturn(List.of(prospectResource, discoveryResource));
      when(resourceLinkService.prospectIsLinkedToDiscovery(prospectResource)).thenReturn(false);
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(discoveryResource))
          .thenReturn(Optional.of(latestApprovedDiscoveryResourceDetail.getSubmissionDetail()));
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(prospectResource))
          .thenReturn(Optional.of(latestApprovedProspectResourceDetail.getSubmissionDetail()));
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
          latestApprovedProspectResourceDetail.getSubmissionDetail()))
          .thenReturn(latestApprovedProspectResourceDetail);
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
          latestApprovedDiscoveryResourceDetail.getSubmissionDetail()))
          .thenReturn(latestApprovedDiscoveryResourceDetail);
      when(submissionResourceService.getResourceDisplayName(latestApprovedDiscoveryResourceDetail))
          .thenReturn(latestApprovedDiscoveryResourceDetail.getResourceName());
      when(submissionResourceService.getResourceDisplayName(latestApprovedProspectResourceDetail))
          .thenReturn(latestApprovedProspectResourceDetail.getResourceName());
      when(internalTeamService.isRegulatorOrAreaTeamUser())
          .thenReturn(false);
      when(industryTeamService.getOrganisationUnitIdsForUser(USER))
          .thenReturn(List.of(latestApprovedDiscoveryResourceDetail.getOrganisationId()));

      var result = submissionQueryService.getLatestResourceSubmissionsByName(searchTerm, USER);

      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(latestApprovedDiscoveryResourceDetail.getId().toString(), latestApprovedDiscoveryName),
              tuple(latestApprovedProspectResourceDetail.getId().toString(), latestApprovedProspectName)
          );
    }

    @Test
    void whenTwoResources_prospectIsLinkedToDiscovery_assertOneReturned() {
      var searchTerm = "resource";
      var latestApprovedProspectName = "prospect resource";
      var latestApprovedDiscoveryName = "discovery resource";
      var latestApprovedProspectResourceDetail = createResourceDetail(
          latestApprovedProspectName, ResourceType.PROSPECT,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_PROSPECT);
      var latestApprovedDiscoveryResourceDetail = createResourceDetail(
          latestApprovedDiscoveryName, ResourceType.DISCOVERY,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_DISCOVERY);

      var prospectResource = latestApprovedProspectResourceDetail.getSubmissionDetail().getSubmission().getResource();
      var discoveryResource = latestApprovedDiscoveryResourceDetail.getSubmissionDetail().getSubmission().getResource();
      when(submissionResourceService.getResourcesByName(searchTerm)).thenReturn(List.of(prospectResource, discoveryResource));
      when(resourceLinkService.prospectIsLinkedToDiscovery(prospectResource)).thenReturn(true);
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(discoveryResource))
          .thenReturn(Optional.of(latestApprovedDiscoveryResourceDetail.getSubmissionDetail()));
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
          latestApprovedDiscoveryResourceDetail.getSubmissionDetail()))
          .thenReturn(latestApprovedDiscoveryResourceDetail);
      when(submissionResourceService.getResourceDisplayName(latestApprovedDiscoveryResourceDetail))
          .thenReturn(latestApprovedDiscoveryResourceDetail.getResourceName());
      when(internalTeamService.isRegulatorOrAreaTeamUser())
          .thenReturn(false);
      when(industryTeamService.getOrganisationUnitIdsForUser(USER))
          .thenReturn(List.of(latestApprovedDiscoveryResourceDetail.getOrganisationId()));

      var result = submissionQueryService.getLatestResourceSubmissionsByName(searchTerm, USER);

      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(latestApprovedDiscoveryResourceDetail.getId().toString(), latestApprovedDiscoveryName)
          );
    }

    @Test
    void whenTwoResources_prospectIsLinkedToDiscovery_notInOrganisation_isRegulator_assertOneReturned() {
      var searchTerm = "resource";
      var latestApprovedProspectName = "prospect resource";
      var latestApprovedDiscoveryName = "discovery resource";
      var latestApprovedProspectResourceDetail = createResourceDetail(
          latestApprovedProspectName, ResourceType.PROSPECT,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_PROSPECT);
      var latestApprovedDiscoveryResourceDetail = createResourceDetail(
          latestApprovedDiscoveryName, ResourceType.DISCOVERY,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_DISCOVERY);

      var prospectResource = latestApprovedProspectResourceDetail.getSubmissionDetail().getSubmission().getResource();
      var discoveryResource = latestApprovedDiscoveryResourceDetail.getSubmissionDetail().getSubmission().getResource();
      when(submissionResourceService.getResourcesByName(searchTerm)).thenReturn(List.of(prospectResource, discoveryResource));
      when(resourceLinkService.prospectIsLinkedToDiscovery(prospectResource)).thenReturn(true);
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(discoveryResource))
          .thenReturn(Optional.of(latestApprovedDiscoveryResourceDetail.getSubmissionDetail()));
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
          latestApprovedDiscoveryResourceDetail.getSubmissionDetail()))
          .thenReturn(latestApprovedDiscoveryResourceDetail);
      when(submissionResourceService.getResourceDisplayName(latestApprovedDiscoveryResourceDetail))
          .thenReturn(latestApprovedDiscoveryResourceDetail.getResourceName());
      when(internalTeamService.isRegulatorOrAreaTeamUser())
          .thenReturn(true);

      var result = submissionQueryService.getLatestResourceSubmissionsByName(searchTerm, USER);

      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(latestApprovedDiscoveryResourceDetail.getId().toString(), latestApprovedDiscoveryName)
          );
    }

    @Test
    void whenTwoResources_prospectIsLinkedToDiscovery_notInOrganisation_assertNoneReturned() {
      var searchTerm = "resource";
      var latestApprovedProspectName = "prospect resource";
      var latestApprovedDiscoveryName = "discovery resource";
      var latestApprovedProspectResourceDetail = createResourceDetail(
          latestApprovedProspectName, ResourceType.PROSPECT,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_PROSPECT);
      var latestApprovedDiscoveryResourceDetail = createResourceDetail(
          latestApprovedDiscoveryName, ResourceType.DISCOVERY,
          SubmissionDetailStatus.ACCEPTED, SubmissionType.CREATE_DISCOVERY);

      var prospectResource = latestApprovedProspectResourceDetail.getSubmissionDetail().getSubmission().getResource();
      var discoveryResource = latestApprovedDiscoveryResourceDetail.getSubmissionDetail().getSubmission().getResource();
      when(submissionResourceService.getResourcesByName(searchTerm)).thenReturn(List.of(prospectResource, discoveryResource));
      when(resourceLinkService.prospectIsLinkedToDiscovery(prospectResource)).thenReturn(true);
      when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(discoveryResource))
          .thenReturn(Optional.of(latestApprovedDiscoveryResourceDetail.getSubmissionDetail()));
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
          latestApprovedDiscoveryResourceDetail.getSubmissionDetail()))
          .thenReturn(latestApprovedDiscoveryResourceDetail);
      when(internalTeamService.isRegulatorOrAreaTeamUser())
          .thenReturn(false);
      when(industryTeamService.getOrganisationUnitIdsForUser(USER))
          .thenReturn(List.of());

      var result = submissionQueryService.getLatestResourceSubmissionsByName(searchTerm, USER);

      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .isEmpty();
    }
  }

  @Nested
  class getLatestNotRelinquishedAndNotInGroupOrPreferredViewDiscoveriesByName {

    @Test
    void whenDiscoverySubmission_andNotInGroup_assertOneFound() {
      var searchTerm = "discovery";

      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.DISCOVERY, SubmissionDetailStatus.ACCEPTED, SubmissionType.UPDATE_DISCOVERY);
      var resource = latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource();

      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
          .thenReturn(List.of(resource, resourceWithoutLatestApproved));
      when(resourceGroupService.isInGroupAndIsNotPreferredView(resource)).thenReturn(false);
      when(resourceGroupService.isInGroupAndIsNotPreferredView(resourceWithoutLatestApproved)).thenReturn(false);
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
          .thenReturn(Optional.of(latestApprovedResourceDetail.getSubmissionDetail()));
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resourceWithoutLatestApproved))
          .thenReturn(Optional.empty());

      ArgumentCaptor<SubmissionDetail> lastestSubmission = ArgumentCaptor.forClass(SubmissionDetail.class);

      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(lastestSubmission.capture()))
          .thenReturn(latestApprovedResourceDetail);
      when(submissionResourceService.getResourceDisplayName(latestApprovedResourceDetail))
          .thenReturn(searchTerm);
      var result = submissionQueryService
          .getLatestNotRelinquishedAndNotInGroupOrPreferredViewDiscoveriesByName(searchTerm);

      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(latestApprovedResourceDetail.getId().toString(), searchTerm)
          );

      assertThat(lastestSubmission.getValue())
          .isEqualTo(latestApprovedResourceDetail.getSubmissionDetail());
    }

    @Test
    void whenDiscoverySubmission_andInGroupNotPreferredView_assertEmpty() {
      var searchTerm = "discovery";

      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.DISCOVERY, SubmissionDetailStatus.ACCEPTED, SubmissionType.UPDATE_DISCOVERY);
      var resource = latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource();

      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
          .thenReturn(List.of(resource, resourceWithoutLatestApproved));
      when(resourceGroupService.isInGroupAndIsNotPreferredView(resource)).thenReturn(true);
      when(resourceGroupService.isInGroupAndIsNotPreferredView(resourceWithoutLatestApproved)).thenReturn(true);

      var result = submissionQueryService
          .getLatestNotRelinquishedAndNotInGroupOrPreferredViewDiscoveriesByName(searchTerm);

      assertThat(result).isEmpty();
    }

    @Test
    void whenDiscoverySubmission_andRelinquishment_assertEmpty() {
      var searchTerm = "discovery";

      var latestApprovedResourceDetail = createResourceDetail(
          searchTerm, ResourceType.DISCOVERY, SubmissionDetailStatus.ACCEPTED, SubmissionType.RELINQUISHMENT_DISCOVERY);
      var resource = latestApprovedResourceDetail.getSubmissionDetail().getSubmission().getResource();

      when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
          .thenReturn(List.of(resource, resourceWithoutLatestApproved));
      when(resourceGroupService.isInGroupAndIsNotPreferredView(resource)).thenReturn(false);
      when(resourceGroupService.isInGroupAndIsNotPreferredView(resourceWithoutLatestApproved)).thenReturn(false);
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
          .thenReturn(Optional.of(latestApprovedResourceDetail.getSubmissionDetail()));
      when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resourceWithoutLatestApproved))
          .thenReturn(Optional.empty());

      var result = submissionQueryService
          .getLatestNotRelinquishedAndNotInGroupOrPreferredViewDiscoveriesByName(searchTerm);

      assertThat(result).isEmpty();
    }
  }

  private ResourceDetail createResourceDetail(String resourceName,
                                              ResourceType resourceType,
                                              SubmissionDetailStatus submissionDetailStatus,
                                              SubmissionType submissionType) {
    var detail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    detail.setStatus(submissionDetailStatus);
    var resource = new Resource();
    resource.setType(resourceType);
    detail.getSubmission().setResource(resource);

    var resourceDetail = new ResourceDetail();
    resourceDetail.setId(UUID.randomUUID());
    resourceDetail.setResourceName(resourceName);
    resourceDetail.setSubmissionDetail(detail);
    resourceDetail.setOrganisationId(1);
    return resourceDetail;
  }
}