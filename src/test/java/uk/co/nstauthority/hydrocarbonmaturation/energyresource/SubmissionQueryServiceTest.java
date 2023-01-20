package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionQueryServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @InjectMocks
  private SubmissionQueryService submissionQueryService;

  @Test
  void getLatestProspectsByNameForSubmissionType_ProspectUpdate_twoProspects() {
    var generalDetail1 = createGeneralDetail("prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var generalDetail2 = createGeneralDetail("prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail1.getSubmissionDetail()))
        .thenReturn(Optional.empty());
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail2.getSubmissionDetail()))
        .thenReturn(Optional.of("15/10"));

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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));

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
  @EnumSource(value = SubmissionType.class, mode = EnumSource.Mode.EXCLUDE, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT", "UPDATE_DISCOVERY", "RELINQUISHMENT_PROSPECT"})
  void getLatestProspectsByNameForSubmissionType_whenDiscoverySubmissionPresent_twoProspects(SubmissionType submissionType) {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));

    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);

    when(submissionDetailService.getSubmissionDetailsForResource(
        any())).thenReturn(List.of(submissionDetail, submissionDetail2));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
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
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var searchTerm = "prospect";
    var discoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    discoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));
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
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail1.getSubmissionDetail()))
        .thenReturn(Optional.empty());
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail2.getSubmissionDetail()))
        .thenReturn(Optional.of("15/10"));
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

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getLatestProspectsByNameForSubmissionType_whenDeletedSubmission_whenDryWell_twoProspects(
      SubmissionType submissionType) {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.CREATE_PROSPECT);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED, SubmissionType.PROSPECT_UPDATE);
    var searchTerm = "prospect";
    var deletedSubmission = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    deletedSubmission.setStatus(SubmissionDetailStatus.DELETED);

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));
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
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail1.getSubmissionDetail()))
        .thenReturn(Optional.empty());
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail2.getSubmissionDetail()))
        .thenReturn(Optional.of("15/10"));
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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));
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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestApprovedProspectSubmissionForResource(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail1.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));
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
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(Optional.of(generalDetail2.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(generalDetail2.getSubmissionDetail()));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail2.getSubmissionDetail()))
        .thenReturn(Optional.of("15/10"));

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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(
            generalDetailForResourceForRelinquishment, generalDetailDryWellApproved));
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
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(
          generalDetailForResourceForRelinquishment.getSubmissionDetail()))
          .thenReturn(Optional.empty());
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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(
            generalDetailForResourceForRelinquishment));
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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(
            generalDetailForResourceForRelinquishment, oldGeneralDetail));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(submissionDetail, submissionDetail2));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(
        generalDetailForResourceForRelinquishment.getSubmissionDetail()))
        .thenReturn(generalDetailForResourceForRelinquishment);

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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(
            generalDetailForResourceForRelinquishment, oldGeneralDetail));
    when(submissionDetailService
        .getLatestApprovedDiscoverySubmissionDetailForResource(discovery))
        .thenReturn(Optional.of(generalDetailForResourceForRelinquishment.getSubmissionDetail()));
    when(submissionDetailService.getSubmissionDetailsForResource(discovery))
        .thenReturn(List.of(submissionDetail, submissionDetail2, deletedSubmission));
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(
        generalDetailForResourceForRelinquishment.getSubmissionDetail()))
        .thenReturn(generalDetailForResourceForRelinquishment);

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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(
            generalDetailForResourceForRelinquishment, oldGeneralDetail));
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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(
            generalDetailForResourceForRelinquishment, oldGeneralDetail));
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

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(
            generalDetails));
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
    return generalDetail;
  }
}