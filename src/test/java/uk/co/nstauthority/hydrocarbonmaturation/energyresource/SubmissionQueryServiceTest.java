package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionQueryServiceTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionResourceService submissionResourceService;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private PermissionService permissionService;

  @InjectMocks
  private SubmissionQueryService submissionQueryService;

  @Test
  void getLatestProspectsByNameForSubmissionType_ProspectUpdate_whenExplorationTeam_twoProspects() {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var resourceDetail = createResourceDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var resourceDetail2 = createResourceDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var searchTerm = "prospect";
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(
            resourceDetail.getSubmissionDetail().getResourceId(),
            resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail2.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail.getSubmissionDetail()))
        .thenReturn(resourceDetail);
    when(submissionResourceService.getResourceDisplayName(resourceDetail)).thenReturn(prospectName1);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail2.getSubmissionDetail()))
        .thenReturn(resourceDetail2);
    when(submissionResourceService.getResourceDisplayName(resourceDetail2)).thenReturn("prospect2 (15/10)");

    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetail.getId().toString(), "prospect1"),
            tuple(resourceDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, mode = EnumSource.Mode.EXCLUDE,
      names = {"CREATE_PROSPECT", "UPDATE_DISCOVERY", "CREATE_DISCOVERY"})
  void getLatestProspectsByNameForSubmissionType_whenDryHoleSubmissionPresent_noProspects(SubmissionType submissionType) {
    var resourceDetail1 = createResourceDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var resourceDetail2 = createResourceDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));

    var postDrillDryWell = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();

    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
        resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail1.getSubmissionDetail()));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
        resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail2.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(postDrillDryWell, resourceDetail1.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(postDrillDryWell, resourceDetail2.getSubmissionDetail()));

    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, mode = EnumSource.Mode.EXCLUDE, names = {
      "PROSPECT_UPDATE", "CREATE_PROSPECT", "UPDATE_DISCOVERY", "RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void getLatestProspectsByNameForSubmissionType_whenDiscoverySubmissionPresent_whenExploration_twoProspects(
      SubmissionType submissionType) {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var resourceDetail1 = createResourceDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var resourceDetail2 = createResourceDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionDetailService.getSubmissionDetailsForResource(any()))
        .thenReturn(List.of(
            submissionDetail, resourceDetail2.getSubmissionDetail(), resourceDetail1.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail1.getSubmissionDetail()))
        .thenReturn(resourceDetail1);
    when(submissionResourceService.getResourceDisplayName(resourceDetail1)).thenReturn(prospectName1);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail2.getSubmissionDetail()))
        .thenReturn(resourceDetail2);
    when(submissionResourceService.getResourceDisplayName(resourceDetail2)).thenReturn(prospectName2);
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail1.getSubmissionDetail()));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail2.getSubmissionDetail()));
    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetail1.getId().toString(), "prospect1"),
            tuple(resourceDetail2.getId().toString(), "prospect2")
        );
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_updateProspect_whenDiscoverySubmissionPresent_noProspects() {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var resourceDetail1 = createResourceDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var resourceDetail2 = createResourceDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var searchTerm = "prospect";
    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    discoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService.getSubmissionDetailsForResource(
        any())).thenReturn(List.of(discoverySubmission, submissionDetail2));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(discoverySubmission));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of((submissionDetail2)));
    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_DryHole_twoProspects() {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var resourceDetail1 = createResourceDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var resourceDetail2 = createResourceDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail1.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail2.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail1.getSubmissionDetail()))
        .thenReturn(resourceDetail1);
    when(submissionResourceService.getResourceDisplayName(resourceDetail1)).thenReturn(prospectName1);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail2.getSubmissionDetail()))
        .thenReturn(resourceDetail2);
    var prospectName2AndCoI = "prospect2 (15/10)";
    when(submissionResourceService.getResourceDisplayName(resourceDetail2)).thenReturn(prospectName2AndCoI);
    when(resourceLinkService.getResourceLinksByProspect(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    when(resourceLinkService.getResourceLinksByProspect(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());

    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.POST_DRILL_DRY_HOLE);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetail1.getId().toString(), "prospect1"),
            tuple(resourceDetail2.getId().toString(), prospectName2AndCoI)
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getLatestProspectsByNameForSubmissionType_whenDeletedSubmission_whenDryWell_whenExploration_twoProspects(
      SubmissionType submissionType) {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var resourceDetail1 = createResourceDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var resourceDetail2 = createResourceDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";
    var deletedSubmission = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    deletedSubmission.setStatus(SubmissionDetailStatus.DELETED);
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail1.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail2.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail(), deletedSubmission));
    when(submissionDetailService.getSubmissionDetailsForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceDetail2.getSubmissionDetail(), deletedSubmission));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail1.getSubmissionDetail()))
        .thenReturn(resourceDetail1);
    when(submissionResourceService.getResourceDisplayName(resourceDetail1)).thenReturn(prospectName1);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail2.getSubmissionDetail()))
        .thenReturn(resourceDetail2);
    when(submissionResourceService.getResourceDisplayName(resourceDetail2)).thenReturn("prospect2 (15/10)");
    when(resourceLinkService.getResourceLinksByProspect(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    when(resourceLinkService.getResourceLinksByProspect(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.POST_DRILL_DRY_HOLE);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetail1.getId().toString(), "prospect1"),
            tuple(resourceDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  @Test
  void getLatestProspectsByName_linkedResource_updateProspect_whenExploration_noProspect() {
    var resourceDetail1 = createResourceDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var resourceDetail2 = createResourceDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var searchTerm = "prospect";
    var resourceLink = new ResourceLink();
    resourceLink.setProspectResource(resourceDetail1.getSubmissionDetail().getResourceId());
    resourceLink.setDiscoveryResource(resourceDetail2.getSubmissionDetail().getResourceId());
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail1.getSubmissionDetail()));
    when(resourceLinkService.getResourceLinksByProspect(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    when(submissionDetailService.getSubmissionDetailsForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceDetail2.getSubmissionDetail()));
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(
            searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestProspectsByName_linkedResource_createDiscovery_whenExploration_oneProspect() {
    var resourceDetail1 = createResourceDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var resourceDetail2 = createResourceDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var searchTerm = "prospect";
    var resourceLink = new ResourceLink();
    resourceLink.setProspectResource(resourceDetail1.getSubmissionDetail().getResourceId());
    resourceLink.setDiscoveryResource(resourceDetail2.getSubmissionDetail().getResourceId());
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail1.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail1.getSubmissionDetail()))
        .thenReturn(resourceDetail1);
    when(submissionResourceService.getResourceDisplayName(resourceDetail1)).thenReturn("prospect1");
    when(resourceLinkService.getResourceLinksByProspect(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetail1.getId().toString(), "prospect1")
        );
  }

  @Test
  void getLatestProspectsByName_onlyDraftProspects_prospectUpdate_noProspects() {
    var resourceDetail1 = createResourceDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.DRAFT, SubmissionType.PROSPECT_UPDATE);
    var resourceDetail2 = createResourceDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    var searchTerm = "prospect";
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetail1.getSubmissionDetail().getResourceId(), resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(resourceDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.empty());

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(
            searchTerm, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestDiscoveriesByName_whenUpdateDiscovery_whenIndustry_oneDiscoveryOneProspect() {
    var resourceDetail = createResourceDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var resourceDetail2 = createResourceDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var generalDetail = createGeneralDetail(resourceDetail2.getSubmissionDetail());
    var searchTerm = "prospect";
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(resourceDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(resourceDetail2.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(resourceDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceDetail2.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail2.getSubmissionDetail()))
        .thenReturn(resourceDetail2);
    when(submissionResourceService.getResourceDisplayName(resourceDetail2)).thenReturn("prospect2 (15/10)");
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        any(), eq(Set.of(IndustryTeamRole.SUBMITTER))))
        .thenReturn(List.of(1, 2));
    mockPassingExtantLicenceCheck(generalDetail);
    var result = submissionQueryService
        .getLatestDiscoveriesByNameForSubmissionType(searchTerm, SubmissionType.UPDATE_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionDetailStatus.class)
  void getLatestProspectsByName_forProspectRelinquishment_whenIndustry_whenOtherSubmissionStatus_assertReturnsOne(
      SubmissionDetailStatus submissionDetailStatus) {
    var prospect = new Resource();
    prospect.setType(ResourceType.PROSPECT);
    var resourceDetailForResourceForRelinquishment = createResourceDetail(
        "prospect3", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    resourceDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(prospect);
    var resourceDetailDryWellApproved = createResourceDetail(
        "prospect4", ResourceType.PROSPECT, submissionDetailStatus, SubmissionType.POST_DRILL_DRY_HOLE);
    resourceDetailDryWellApproved.getSubmissionDetail().setResourceId(prospect);
    var searchTerm = "prospect";
    var generalDetailForRelinquishment =
        createGeneralDetail(resourceDetailForResourceForRelinquishment.getSubmissionDetail());

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(prospect));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(
            List.of(resourceDetailDryWellApproved.getSubmissionDetail(),
                resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(resourceLinkService.getResourceLinksByProspect(prospect))
        .thenReturn(List.of());
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    if (submissionDetailStatus == SubmissionDetailStatus.APPROVED
        || submissionDetailStatus == SubmissionDetailStatus.DELETED) {
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
          resourceDetailForResourceForRelinquishment.getSubmissionDetail()))
          .thenReturn(resourceDetailForResourceForRelinquishment);
      when(submissionResourceService.getResourceDisplayName(resourceDetailForResourceForRelinquishment)).thenReturn("prospect3");
      when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
          any(), eq(Set.of(IndustryTeamRole.SUBMITTER))))
          .thenReturn(List.of(1,2));
    }

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.RELINQUISHMENT_PROSPECT);

    if (submissionDetailStatus == SubmissionDetailStatus.APPROVED
        || submissionDetailStatus == SubmissionDetailStatus.DELETED) {
      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(resourceDetailForResourceForRelinquishment.getId().toString(), "prospect3")
          );
    } else {
      assertThat(result).isEmpty();
    }
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getLatestProspectsByName_forProspectRelinquishment_whenOtherSubmissionsDraft_assertReturnsNothing(
      SubmissionType submissionType) {
    var prospect = new Resource();
    prospect.setType(ResourceType.PROSPECT);
    var resourceDetailForResourceForRelinquishment = createResourceDetail(
        "prospect3", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    resourceDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(prospect);
    var searchTerm = "prospect";

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(List.of(submissionDetail, submissionDetail2));
    when(resourceLinkService.getResourceLinksByProspect(prospect))
        .thenReturn(List.of());

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_PROSPECT);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestDiscoveryByName_forDiscoveryRelinquishment_assertReturnsOne() {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var oldResourceDetail = createResourceDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var resourceDetailForResourceForRelinquishment = createResourceDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    resourceDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldResourceDetail.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(submissionDetail, submissionDetail2));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
        resourceDetailForResourceForRelinquishment.getSubmissionDetail()))
        .thenReturn(resourceDetailForResourceForRelinquishment);
    when(submissionResourceService.getResourceDisplayName(resourceDetailForResourceForRelinquishment)).thenReturn("discovery");
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetailForResourceForRelinquishment.getId().toString(), "discovery")
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getLatestDiscoveryByName_forDiscoveryRelinquishment_whenIndustry_whenDeletedSubmission_assertReturnsOne(
      SubmissionType submissionType) {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var oldResourceDetail = createResourceDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var resourceDetailForResourceForRelinquishment = createResourceDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    var deletedSubmission = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    deletedSubmission.setStatus(SubmissionDetailStatus.DELETED);
    resourceDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldResourceDetail.getSubmissionDetail().setResourceId(discovery);
    var relinquishmentGeneralDetail = createGeneralDetail(
        resourceDetailForResourceForRelinquishment.getSubmissionDetail());
    var searchTerm = "discovery";

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(submissionDetail, submissionDetail2, deletedSubmission));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
        resourceDetailForResourceForRelinquishment.getSubmissionDetail()))
        .thenReturn(resourceDetailForResourceForRelinquishment);
    when(submissionResourceService.getResourceDisplayName(resourceDetailForResourceForRelinquishment)).thenReturn("discovery");
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        any(), eq(Set.of(IndustryTeamRole.SUBMITTER))))
        .thenReturn(List.of(1, 2));

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetailForResourceForRelinquishment.getId().toString(), "discovery")
        );
  }

  @Test
  void getLatestDiscoveryByName_forDiscoveryRelinquishment_whenDraftUpdatePresent_assertReturnsNothing() {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var oldResourceDetail = createResourceDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var resourceDetailForResourceForRelinquishment = createResourceDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    resourceDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldResourceDetail.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(submissionDetail, submissionDetail2));

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DELETED"}, mode = EnumSource.Mode.EXCLUDE)
  void getLatestDiscoveryByName_forDiscoveryRelinquishment_whenRelinquishmentPresentInStatus_assertReturnsNothing(
      SubmissionDetailStatus status) {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var oldResourceDetail = createResourceDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var resourceDetailForResourceForRelinquishment = createResourceDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    resourceDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldResourceDetail.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryRelinquishment = SubmissionDetailTestUtils.createNewDiscoveryRelinquishmentSubmissionDetail();
    discoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
    discoveryRelinquishment.setStatus(status);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(discoverySubmission, discoveryRelinquishment));

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT", "SUBMITTED"}, mode = EnumSource.Mode.INCLUDE)
  void getLatestDiscoveryByName_forDiscoveryUpdate_whenDiscoveryUpdatePresentInStatus_assertReturnsNothing(
      SubmissionDetailStatus status) {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var resourceDetails = createResourceDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    resourceDetails.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryUpdate = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    discoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
    discoveryUpdate.setStatus(status);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(discoverySubmission,
            discoveryUpdate,
            resourceDetails.getSubmissionDetail()));

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.UPDATE_DISCOVERY);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT", "APPROVED"}, mode = EnumSource.Mode.INCLUDE)
  void getLatestDiscoveryByName_forDiscoveryUpdate_whenRelinquishmentPresentInStatus_assertReturnsNothing(
      SubmissionDetailStatus status) {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var resourceDetails = createResourceDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    resourceDetails.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var relinquishment = SubmissionDetailTestUtils.createNewDiscoveryRelinquishmentSubmissionDetail();
    relinquishment.setStatus(status);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(discoverySubmission,
            relinquishment,
            resourceDetails.getSubmissionDetail()));

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.UPDATE_DISCOVERY);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_PROSPECT", "CREATE_DISCOVERY", "UPDATE_DISCOVERY", "RELINQUISHMENT_DISCOVERY"},
      mode = EnumSource.Mode.EXCLUDE)
  void getLatestProspectByName_forSubmission_whenRelinquishmentPresent_assertReturnsNothing(
      SubmissionType submissionType) {
    var prospect = new Resource();
    prospect.setType(ResourceType.PROSPECT);
    var resourceDetails = createResourceDetail("prospect", ResourceType.PROSPECT,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    resourceDetails.getSubmissionDetail().setResourceId(prospect);

    var searchTerm = "prospect";

    var relinquishment = SubmissionDetailTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT);
    relinquishment.setStatus(SubmissionDetailStatus.DRAFT);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(resourceDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(List.of(relinquishment, resourceDetails.getSubmissionDetail()));

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm,
            submissionType);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @MethodSource(value = ("provideTwoSubmissionTypesForProspect"))
  void getLatestProspectByName_forSubmission_whenDeletedSubmissionPresent_assertReturnsOne(
      SubmissionType submissionType, SubmissionType otherSubmissionType) {
    var prospect = new Resource();
    prospect.setType(ResourceType.PROSPECT);
    var prospectName = "prospect";
    var resourceDetails = createResourceDetail(prospectName, ResourceType.PROSPECT,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    resourceDetails.getSubmissionDetail().setResourceId(prospect);
    resourceDetails.setOrganisationId(1);
    var searchTerm = "prospect";

    var otherSubmission = SubmissionDetailTestUtils
        .createSubmissionDetail(otherSubmissionType);
    otherSubmission.setStatus(SubmissionDetailStatus.DELETED);
    var deletedDiscoverySubmission = SubmissionDetailTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    deletedDiscoverySubmission.setStatus(SubmissionDetailStatus.DELETED);
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(resourceDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(List.of(otherSubmission, resourceDetails.getSubmissionDetail()));
    var resourceLink = new ResourceLink();
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    resourceLink.setDiscoveryResource(discovery);
    when(resourceLinkService.getResourceLinksByProspect(resourceDetails.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(deletedDiscoverySubmission));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetails.getSubmissionDetail()))
        .thenReturn(resourceDetails);
    when(submissionResourceService.getResourceDisplayName(resourceDetails)).thenReturn(prospectName);
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm,
            submissionType);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetails.getId().toString(), "prospect")
        );
  }

  @Test
  void getLatestProspectByName_forRelinqishment_whenOrganisationResponsibleNull_assertReturnsNone() {
    var prospect = new Resource();
    prospect.setType(ResourceType.PROSPECT);
    var resourceDetails = createResourceDetail("prospect", ResourceType.PROSPECT,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    resourceDetails.getSubmissionDetail().setResourceId(prospect);
    resourceDetails.setOrganisationId(null);
    var searchTerm = "prospect";

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(resourceDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(resourceDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(List.of(resourceDetails.getSubmissionDetail()));
    when(resourceLinkService.getResourceLinksByProspect(resourceDetails.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetails.getSubmissionDetail()))
        .thenReturn(resourceDetails);

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_PROSPECT);

    assertThat(result)
        .isEmpty();
  }

  @Test
  void getLatestDiscoveryByName_forRelinqishment_whenUserNotSubmitterForOrganisationResponsible_assertReturnsNone() {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var resourceDetail = createResourceDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    resourceDetail.getSubmissionDetail().setResourceId(discovery);
    resourceDetail.setOrganisationId(1);
    var searchTerm = "discovery";
    var generalDetail = createGeneralDetail(resourceDetail.getSubmissionDetail());
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(false);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(resourceDetail.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetail.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(resourceDetail.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail.getSubmissionDetail()))
        .thenReturn(resourceDetail);
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        any(), eq(Set.of(IndustryTeamRole.SUBMITTER))))
        .thenReturn(List.of());

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result)
        .isEmpty();
  }

  @Test
  void getLatestDiscoveryByName_forRelinqishment_whenUserExploration_assertReturnsOne() {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var prospectName = "discovery";
    var resourceDetail = createResourceDetail(prospectName, ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    resourceDetail.getSubmissionDetail().setResourceId(discovery);
    resourceDetail.setOrganisationId(1);
    var searchTerm = "discovery";

    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(resourceDetail.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(resourceDetail.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(resourceDetail.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetail.getSubmissionDetail()))
        .thenReturn(resourceDetail);
    when(submissionResourceService.getResourceDisplayName(resourceDetail)).thenReturn(prospectName);

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetail.getId().toString(), "discovery")
        );
  }

  @Test
  void getLatestDiscoverySubmission_twoResources_whenUserInResponsibleTeam_oneOnExtantLicence_assertOneReturn() {
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(false);
    var responsibleOrganisationForUser = 1;
    var discoveryOnExtantLicence = new Resource();
    discoveryOnExtantLicence.setType(ResourceType.DISCOVERY);
    var discoveryNotOnExtantLicence = new Resource();
    discoveryNotOnExtantLicence.setType(ResourceType.DISCOVERY);
    var discoveryNameMatchingTerm = "discovery";
    var discoveryNameMatchingTerm2 = "discovery2";
    var searchTerm = "discovery";
    var resourceDetailOnExtantLicence = createResourceDetail(discoveryNameMatchingTerm, ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    var resourceDetailNotOnExtantLicence = createResourceDetail(discoveryNameMatchingTerm2, ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    resourceDetailOnExtantLicence.getSubmissionDetail().setResourceId(discoveryOnExtantLicence);
    resourceDetailNotOnExtantLicence.getSubmissionDetail().setResourceId(discoveryNotOnExtantLicence);
    resourceDetailOnExtantLicence.setOrganisationId(responsibleOrganisationForUser);
    resourceDetailNotOnExtantLicence.setOrganisationId(responsibleOrganisationForUser);
    var generalDetailOnExtantLicence = createGeneralDetail(resourceDetailOnExtantLicence.getSubmissionDetail());
    var generalDetailNotOnExtantLicence = createGeneralDetail(resourceDetailNotOnExtantLicence.getSubmissionDetail());

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(
            resourceDetailNotOnExtantLicence.getSubmissionDetail().getResourceId(),
            resourceDetailOnExtantLicence.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discoveryNotOnExtantLicence))
        .thenReturn(Optional.of(resourceDetailNotOnExtantLicence.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discoveryOnExtantLicence))
        .thenReturn(Optional.of(resourceDetailOnExtantLicence.getSubmissionDetail()));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetailOnExtantLicence.getSubmissionDetail()))
        .thenReturn(resourceDetailOnExtantLicence);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetailNotOnExtantLicence.getSubmissionDetail()))
        .thenReturn(resourceDetailNotOnExtantLicence);
    when(submissionResourceService.getResourceDisplayName(resourceDetailOnExtantLicence))
        .thenReturn(discoveryNameMatchingTerm);
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        any(), eq(Set.of(IndustryTeamRole.SUBMITTER))))
        .thenReturn(List.of(responsibleOrganisationForUser));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(
        resourceDetailOnExtantLicence.getSubmissionDetail()))
        .thenReturn(Optional.of(generalDetailOnExtantLicence));
    when(generalDetailsPersistenceService.isOnAnExtantLicence(generalDetailOnExtantLicence))
        .thenReturn(true);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(
        resourceDetailNotOnExtantLicence.getSubmissionDetail()))
        .thenReturn(Optional.of(generalDetailNotOnExtantLicence));
    when(generalDetailsPersistenceService.isOnAnExtantLicence(generalDetailNotOnExtantLicence))
        .thenReturn(false);

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.PROSPECT_UPDATE);
    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(resourceDetailOnExtantLicence.getId().toString(), "discovery")
        );
  }

  private static Stream<Arguments> provideTwoSubmissionTypesForProspect() {
    return Stream.of(
        Arguments.of(
            SubmissionType.RELINQUISHMENT_PROSPECT,
            SubmissionType.RELINQUISHMENT_PROSPECT),
        Arguments.of(
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.POST_DRILL_DRY_HOLE),
        Arguments.of(
            SubmissionType.PROSPECT_UPDATE,
            SubmissionType.RELINQUISHMENT_PROSPECT),
        Arguments.of(
            SubmissionType.POST_DRILL_DRY_HOLE,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT),
        Arguments.of(
            SubmissionType.PROSPECT_UPDATE,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT),
        Arguments.of(
            SubmissionType.POST_DRILL_DRY_HOLE,
            SubmissionType.PROSPECT_UPDATE)
    );
  }

  private void mockPassingExtantLicenceCheck(GeneralDetail generalDetail) {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(any()))
        .thenReturn(Optional.of(generalDetail));
    when(generalDetailsPersistenceService.isOnAnExtantLicence(generalDetail))
        .thenReturn(true);
  }

  private ResourceDetail createResourceDetail(String prospectName,
                                              ResourceType resourceType,
                                              SubmissionDetailStatus submissionDetailStatus,
                                              SubmissionType submissionType) {
    var detail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    detail.setStatus(submissionDetailStatus);
    var resource = new Resource();
    resource.setType(resourceType);
    detail.setResourceId(resource);

    var resourceDetail = new ResourceDetail();
    resourceDetail.setId(UUID.randomUUID());
    resourceDetail.setResourceName(prospectName);
    resourceDetail.setSubmissionDetail(detail);
    resourceDetail.setOrganisationId(1);
    return resourceDetail;
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    return generalDetail;
  }
}