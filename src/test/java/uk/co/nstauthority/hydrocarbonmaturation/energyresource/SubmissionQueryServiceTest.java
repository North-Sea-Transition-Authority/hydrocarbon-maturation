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
    var generalDetail1 = createGeneralDetail("prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail("prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail1.getSubmissionDetail());
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail2.getSubmissionDetail());
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
  @EnumSource(value = SubmissionType.class, mode = EnumSource.Mode.EXCLUDE, names = {"CREATE_PROSPECT", "UPDATE_DISCOVERY"})
  void getLatestProspectsByNameForSubmissionType_whenDryHoleSubmissionPresent_noProspects(SubmissionType submissionType) {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));

    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(submissionDetail2);

    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, submissionType);

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, mode = EnumSource.Mode.EXCLUDE, names = {"PROSPECT_UPDATE", "CREATE_PROSPECT", "UPDATE_DISCOVERY"})
  void getLatestProspectsByNameForSubmissionType_whenDiscoverySubmissionPresent_twoProspects(SubmissionType submissionType) {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
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
    when(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail1.getSubmissionDetail());
    when(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail2.getSubmissionDetail());
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
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var submissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail2.setStatus(SubmissionDetailStatus.APPROVED);
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm))
        .thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService.getSubmissionDetailsForResource(
        any())).thenReturn(List.of(submissionDetail, submissionDetail2));
    when(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(submissionDetail2);
    var result = submissionQueryService
        .getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestProspectsByNameForSubmissionType_DryHole_twoProspects() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail1.getSubmissionDetail());
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail2.getSubmissionDetail());
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
  void getLatestProspectsByName_linkedResource_updateProspect_oneProspect() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";
    var resourceLink = new ResourceLink();
    resourceLink.setProspectResource(generalDetail1.getSubmissionDetail().getResourceId());
    resourceLink.setDiscoveryResource(generalDetail2.getSubmissionDetail().getResourceId());

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail1.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(resourceLinkService.getResourceLinksByProspect(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.PROSPECT_UPDATE);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1")
        );
  }

  @Test
  void getLatestProspectsByName_linkedResource_createDiscovery_oneProspect() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";
    var resourceLink = new ResourceLink();
    resourceLink.setProspectResource(generalDetail1.getSubmissionDetail().getResourceId());
    resourceLink.setDiscoveryResource(generalDetail2.getSubmissionDetail().getResourceId());

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail1.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail1.getSubmissionDetail())).thenReturn(generalDetail1);
    when(resourceLinkService.getResourceLinksByProspect(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(List.of(resourceLink));
    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.CREATE_DISCOVERY);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail1.getId().toString(), "prospect1")
        );
  }

  @Test
  void getLatestProspectsByName_onlyDraftProspects_prospectUpdate_noProspects() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.DRAFT);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";

    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail1.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail1.getSubmissionDetail());

    var result =
        submissionQueryService.getLatestProspectsByNameForSubmissionType(searchTerm, SubmissionType.CREATE_DISCOVERY);

    assertThat(result).isEmpty();
  }

  @Test
  void getLatestDiscoveriesByName_oneDiscoveryOneProspect() {
    var generalDetail1 = createGeneralDetail(
        "prospect1", ResourceType.PROSPECT, SubmissionDetailStatus.APPROVED);
    var generalDetail2 = createGeneralDetail(
        "prospect2", ResourceType.DISCOVERY, SubmissionDetailStatus.APPROVED);
    var searchTerm = "prospect";
    when(generalDetailsPersistenceService.getGeneralDetailsByName(searchTerm)).thenReturn(List.of(generalDetail1, generalDetail2));
    when(submissionDetailService
        .getLatestSubmissionDetailForResourceOrThrow(generalDetail2.getSubmissionDetail().getResourceId()))
        .thenReturn(generalDetail2.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(generalDetail2.getSubmissionDetail())).thenReturn(generalDetail2);
    when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail2.getSubmissionDetail()))
        .thenReturn(Optional.of("15/10"));

    var result = submissionQueryService.getLatestDiscoveriesByName(searchTerm);

    assertThat(result)
        .extracting(ResourceView::getSelectionId, ResourceView::getSelectionText)
        .containsExactly(
            tuple(generalDetail2.getId().toString(), "prospect2 (15/10)")
        );
  }

  private GeneralDetail createGeneralDetail(String prospectName,
                                            ResourceType resourceType,
                                            SubmissionDetailStatus submissionDetailStatus) {
    var detail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    detail.setStatus(submissionDetailStatus);
    var resource = new Resource();
    resource.setType(resourceType);
    detail.setResourceId(resource);

    var generalDetail = new GeneralDetail();
    generalDetail.setId(UUID.randomUUID());
    generalDetail.setProspectName(prospectName);
    generalDetail.setSubmissionDetail(detail);
    return generalDetail;
  }
}