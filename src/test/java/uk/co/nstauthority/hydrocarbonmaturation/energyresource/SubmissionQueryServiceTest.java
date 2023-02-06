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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionQueryServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionResourceService submissionResourceService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private PermissionService permissionService;

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @InjectMocks
  private SubmissionQueryService submissionQueryService;

  @Test
  void getLatestProspectsByNameForSubmissionType_ProspectUpdate_twoProspects() {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var generalDetail1 = createGeneralDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var generalDetail2 = createGeneralDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var searchTerm = "prospect";
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(submissionResourceService.getResourceDisplayName(generalDetail1)).thenReturn(prospectName1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(submissionResourceService.getResourceDisplayName(generalDetail2)).thenReturn("prospect2 (15/10)");

    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1"),
            tuple(generalDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, mode = EnumSource.Mode.EXCLUDE,
      names = {"CREATE_PROSPECT", "UPDATE_DISCOVERY", "CREATE_DISCOVERY"})
  void getLatestProspectsByNameForSubmissionType_whenDryHoleSubmissionPresent_noProspects(SubmissionType submissionType) {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));

    var postDrillDryWell = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();

    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
        generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(
        generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(postDrillDryWell, generalDetail1.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(postDrillDryWell, generalDetail2.getSubmissionDetail()));

    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, mode = EnumSource.Mode.EXCLUDE, names = {
      "PROSPECT_UPDATE", "CREATE_PROSPECT", "UPDATE_DISCOVERY", "RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void getLatestProspectsByNameForSubmissionType_whenDiscoverySubmissionPresent_twoProspects(SubmissionType submissionType) {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var generalDetail1 = createGeneralDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionDetailService.getSubmissionDetailsForResource(any()))
        .thenReturn(List.of(
            submissionDetail, generalDetail2.getSubmissionDetail(), generalDetail1.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(submissionResourceService.getResourceDisplayName(generalDetail1)).thenReturn(prospectName1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(submissionResourceService.getResourceDisplayName(generalDetail2)).thenReturn(prospectName2);
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1"),
            tuple(generalDetail2.getId().toString(), "prospect2")
        );
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_updateProspect_whenDiscoverySubmissionPresent_noProspects() {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var generalDetail1 = createGeneralDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var generalDetail2 = createGeneralDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var searchTerm = "prospect";
    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    discoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService.getSubmissionDetailsForResource(
        any())).thenReturn(List.of(discoverySubmission, submissionDetail2));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(discoverySubmission));
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of((submissionDetail2)));
    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_DryHole_twoProspects() {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var generalDetail1 = createGeneralDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(submissionResourceService.getResourceDisplayName(generalDetail1)).thenReturn(prospectName1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    var prospectName2AndCoI = "prospect2 (15/10)";
    when(submissionResourceService.getResourceDisplayName(generalDetail2)).thenReturn(prospectName2AndCoI);
    when(resourceLinkService.getResourceLinksByProspect(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    when(resourceLinkService.getResourceLinksByProspect(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());

    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.POST_DRILL_DRY_HOLE);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1"),
            tuple(generalDetail2.getId().toString(), prospectName2AndCoI)
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getLatestProspectsByNameForSubmissionType_whenDeletedSubmission_whenDryWell_twoProspects(
      SubmissionType submissionType) {
    var prospectName1 = "prospect1";
    var prospectName2 = "prospect2";
    var generalDetail1 = createGeneralDetail(
        prospectName1, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        prospectName2, ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";
    var deletedSubmission = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    deletedSubmission.setStatus(SubmissionDetailStatus.DELETED);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail(), deletedSubmission));
    when(submissionDetailService.getSubmissionDetailsForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(generalDetail2.getSubmissionDetail(), deletedSubmission));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(submissionResourceService.getResourceDisplayName(generalDetail1)).thenReturn(prospectName1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(submissionResourceService.getResourceDisplayName(generalDetail2)).thenReturn("prospect2 (15/10)");
    when(resourceLinkService.getResourceLinksByProspect(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    when(resourceLinkService.getResourceLinksByProspect(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.POST_DRILL_DRY_HOLE);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1"),
            tuple(generalDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  @Test
  void getLatestProspectsByName_linkedResource_updateProspect_noProspect() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var searchTerm = "prospect";
    var resourceLink = new ResourceLink();
    resourceLink.setProspectResource(generalDetail1.getSubmissionDetail().getResourceId());
    resourceLink.setDiscoveryResource(generalDetail2.getSubmissionDetail().getResourceId());

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(resourceLinkService.getResourceLinksByProspect(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    when(submissionDetailService.getSubmissionDetailsForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(generalDetail2.getSubmissionDetail()));
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(
            searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestProspectsByName_linkedResource_createDiscovery_oneProspect() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var searchTerm = "prospect";
    var resourceLink = new ResourceLink();
    resourceLink.setProspectResource(generalDetail1.getSubmissionDetail().getResourceId());
    resourceLink.setDiscoveryResource(generalDetail2.getSubmissionDetail().getResourceId());

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(submissionResourceService.getResourceDisplayName(generalDetail1)).thenReturn("prospect1");
    when(resourceLinkService.getResourceLinksByProspect(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1")
        );
  }

  @Test
  void getLatestProspectsByName_onlyDraftProspects_prospectUpdate_noProspects() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.DRAFT, SubmissionType.PROSPECT_UPDATE);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    var searchTerm = "prospect";

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetail1.getSubmissionDetail().getResourceId(), generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.empty());

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(
            searchTerm, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestDiscoveriesByName_whenUpdateDiscovery_oneDiscoveryOneProspect() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var searchTerm = "prospect";
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(generalDetail2.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(generalDetail2.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(submissionResourceService.getResourceDisplayName(generalDetail2)).thenReturn("prospect2 (15/10)");

    var result = submissionQueryService
        .getLatestDiscoveriesByNameForSubmissionType(searchTerm, SubmissionType.UPDATE_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionDetailStatus.class)
  void getLatestProspectsByName_forProspectRelinquishment_whenOtherSubmissionStatus_assertReturnsOne(
      SubmissionDetailStatus submissionDetailStatus) {
    var prospect = new Resource();
    prospect.setType(ResourceType.PROSPECT);
    var generalDetailForResourceForRelinquishment = createGeneralDetail(
        "prospect3", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    generalDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(prospect);
    var generalDetailDryWellApproved = createGeneralDetail(
        "prospect4", ResourceType.PROSPECT, submissionDetailStatus, SubmissionType.POST_DRILL_DRY_HOLE);
    generalDetailDryWellApproved.getSubmissionDetail().setResourceId(prospect);
    var searchTerm = "prospect";

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(prospect));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(
            List.of(generalDetailDryWellApproved.getSubmissionDetail(),
                generalDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(resourceLinkService.getResourceLinksByProspect(prospect))
        .thenReturn(List.of());
    when(submissionDetailService.getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
    if (submissionDetailStatus == SubmissionDetailStatus.APPROVED
        || submissionDetailStatus == SubmissionDetailStatus.DELETED) {
      when(generalDetailsPersistenceService.getGeneralDetailOrThrow(
          generalDetailForResourceForRelinquishment.getSubmissionDetail()))
          .thenReturn(generalDetailForResourceForRelinquishment);
      when(submissionResourceService.getResourceDisplayName(generalDetailForResourceForRelinquishment)).thenReturn("prospect3");
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(
          generalDetailForResourceForRelinquishment.getOrganisationId()))
          .thenReturn(List.of(1,2));
      when(industryTeamService.isSubmitterForOrganisationGroups(
          any(), eq(List.of(1,2))))
          .thenReturn(true);

    }

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.RELINQUISHMENT_PROSPECT);

    if (submissionDetailStatus == SubmissionDetailStatus.APPROVED
        || submissionDetailStatus == SubmissionDetailStatus.DELETED) {
      assertThat(result)
          .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
          .containsExactly(
              tuple(generalDetailForResourceForRelinquishment.getId().toString(), "prospect3")
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
    var generalDetailForResourceForRelinquishment = createGeneralDetail(
        "prospect3", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    generalDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(prospect);
    var searchTerm = "prospect";

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetailForResourceForRelinquishment.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
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
    var oldGeneralDetail = createGeneralDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var generalDetailForResourceForRelinquishment = createGeneralDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    generalDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldGeneralDetail.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(submissionDetail, submissionDetail2));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(
        generalDetailForResourceForRelinquishment.getSubmissionDetail()))
        .thenReturn(generalDetailForResourceForRelinquishment);
    when(submissionResourceService.getResourceDisplayName(generalDetailForResourceForRelinquishment)).thenReturn("discovery");
    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetailForResourceForRelinquishment.getId().toString(), "discovery")
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getLatestDiscoveryByName_forDiscoveryRelinquishment_whenDeletedSubmission_assertReturnsOne(
      SubmissionType submissionType) {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var oldGeneralDetail = createGeneralDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var generalDetailForResourceForRelinquishment = createGeneralDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    var deletedSubmission = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    deletedSubmission.setStatus(SubmissionDetailStatus.DELETED);
    generalDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldGeneralDetail.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(submissionDetail, submissionDetail2, deletedSubmission));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(
        generalDetailForResourceForRelinquishment.getSubmissionDetail()))
        .thenReturn(generalDetailForResourceForRelinquishment);
    when(submissionResourceService.getResourceDisplayName(generalDetailForResourceForRelinquishment)).thenReturn("discovery");
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(
        generalDetailForResourceForRelinquishment.getOrganisationId()))
        .thenReturn(List.of(1,2));
    when(industryTeamService.isSubmitterForOrganisationGroups(
        any(), eq(List.of(1,2))))
        .thenReturn(true);


    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetailForResourceForRelinquishment.getId().toString(), "discovery")
        );
  }

  @Test
  void getLatestDiscoveryByName_forDiscoveryRelinquishment_whenDraftUpdatePresent_assertReturnsNothing() {
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    var oldGeneralDetail = createGeneralDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var generalDetailForResourceForRelinquishment = createGeneralDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    generalDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldGeneralDetail.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
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
    var oldGeneralDetail = createGeneralDetail(
        "discovery old", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var generalDetailForResourceForRelinquishment = createGeneralDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    generalDetailForResourceForRelinquishment.getSubmissionDetail().setResourceId(discovery);
    oldGeneralDetail.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryRelinquishment = SubmissionDetailTestUtils.createNewDiscoveryRelinquishmentSubmissionDetail();
    discoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
    discoveryRelinquishment.setStatus(status);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
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
    var generalDetails = createGeneralDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    generalDetails.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryUpdate = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    discoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
    discoveryUpdate.setStatus(status);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(discoverySubmission,
            discoveryUpdate,
            generalDetails.getSubmissionDetail()));

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
    var generalDetails = createGeneralDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.UPDATE_DISCOVERY);
    generalDetails.getSubmissionDetail().setResourceId(discovery);

    var searchTerm = "discovery";

    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var relinquishment = SubmissionDetailTestUtils.createNewDiscoveryRelinquishmentSubmissionDetail();
    relinquishment.setStatus(status);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(discovery));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(discoverySubmission,
            relinquishment,
            generalDetails.getSubmissionDetail()));

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
    var generalDetails = createGeneralDetail("prospect", ResourceType.PROSPECT,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    generalDetails.getSubmissionDetail().setResourceId(prospect);

    var searchTerm = "prospect";

    var relinquishment = SubmissionDetailTestUtils
        .createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT);
    relinquishment.setStatus(SubmissionDetailStatus.DRAFT);

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(generalDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(List.of(relinquishment, generalDetails.getSubmissionDetail()));

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
    var generalDetails = createGeneralDetail(prospectName, ResourceType.PROSPECT,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    generalDetails.getSubmissionDetail().setResourceId(prospect);
    generalDetails.setOrganisationId(1);
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
        .thenReturn(List.of(generalDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(generalDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(List.of(otherSubmission, generalDetails.getSubmissionDetail()));
    var resourceLink = new ResourceLink();
    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);
    resourceLink.setDiscoveryResource(discovery);
    when(resourceLinkService.getResourceLinksByProspect(generalDetails.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(deletedDiscoverySubmission));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetails.getSubmissionDetail()))
        .thenReturn(generalDetails);
    when(submissionResourceService.getResourceDisplayName(generalDetails)).thenReturn(prospectName);
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm,
            submissionType);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetails.getId().toString(), "prospect")
        );
  }

  @Test
  void getLatestProspectByName_forRelinqishment_whenOrganisationResponsibleNull_assertReturnsNone() {
    var prospect = new Resource();
    prospect.setType(ResourceType.PROSPECT);
    var generalDetails = createGeneralDetail("prospect", ResourceType.PROSPECT,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    generalDetails.getSubmissionDetail().setResourceId(prospect);
    generalDetails.setOrganisationId(null);
    var searchTerm = "prospect";

    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm))
        .thenReturn(List.of(generalDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(prospect))
        .thenReturn(Optional.of(generalDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(prospect))
        .thenReturn(List.of(generalDetails.getSubmissionDetail()));
    when(resourceLinkService.getResourceLinksByProspect(generalDetails.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of());
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetails.getSubmissionDetail()))
        .thenReturn(generalDetails);

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
    var generalDetails = createGeneralDetail("discovery", ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    generalDetails.getSubmissionDetail().setResourceId(discovery);
    generalDetails.setOrganisationId(1);
    var searchTerm = "discovery";

    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(false);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(generalDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(generalDetails.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetails.getSubmissionDetail()))
        .thenReturn(generalDetails);
//    when(industryTeamService.isSubmitterForOrganisationGroups(
//        any(), eq(List.of(1,2))))
//        .thenReturn(false);

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
    var generalDetails = createGeneralDetail(prospectName, ResourceType.DISCOVERY,
        SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_DISCOVERY);
    generalDetails.getSubmissionDetail().setResourceId(discovery);
    generalDetails.setOrganisationId(1);
    var searchTerm = "discovery";

    when(permissionService.hasPermission(any(), eq(Set.of(RolePermission.EXPLORATION))))
        .thenReturn(true);
    when(submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm))
        .thenReturn(List.of(generalDetails.getSubmissionDetail().getResourceId()));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetails.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(generalDetails.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetails.getSubmissionDetail()))
        .thenReturn(generalDetails);
    when(submissionResourceService.getResourceDisplayName(generalDetails)).thenReturn(prospectName);

    var result =
        submissionQueryService.getLatestDiscoveriesByNameForSubmissionType(searchTerm,
            SubmissionType.RELINQUISHMENT_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetails.getId().toString(), "discovery")
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

  private GeneralDetail createGeneralDetail(String prospectName,
                                            ResourceType resourceType,
                                            SubmissionDetailStatus submissionDetailStatus,
                                            SubmissionType submissionType) {
    var detail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    detail.setStatus(submissionDetailStatus);
    var resource = new Resource();
    resource.setType(resourceType);
    detail.setResourceId(resource);

    var generalDetail = new GeneralDetail();
    generalDetail.setId(UUID.randomUUID());
    generalDetail.setResourceName(prospectName);
    generalDetail.setSubmissionDetail(detail);
    generalDetail.setOrganisationId(1);
    return generalDetail;
  }
}