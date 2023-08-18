package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservoirQueryServiceTest {

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private ReservoirLinkService reservoirLinkService;

  @InjectMocks
  private ReservoirQueryService reservoirQueryService;

  @Test
  void getReservoirByNameForSubmissionDetail_filters() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    var fieldResource = submissionDetail.getSubmission().getResource();
    var field = new Field();
    field.setResource(fieldResource);
    when(fieldPersistenceService.getFieldByResourceOrThrow(fieldResource)).thenReturn(field);

    var firstDiscoveryResource = new Resource();
    var firstResourceLink = new ResourceLink();
    firstResourceLink.setDiscoveryResource(firstDiscoveryResource);
    var secondDiscoveryResource = new Resource();
    var secondResourceLink = new ResourceLink();
    secondResourceLink.setDiscoveryResource(secondDiscoveryResource);
    when(resourceLinkService.getResourceLinksByField(fieldResource)).thenReturn(List.of(firstResourceLink, secondResourceLink));

    var firstDiscoverySubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(firstDiscoveryResource))
        .thenReturn(Optional.of(firstDiscoverySubmissionDetail));
    var secondDiscoverySubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(secondDiscoveryResource))
        .thenReturn(Optional.of(secondDiscoverySubmissionDetail));

    var firstReservoir = new Reservoir();
    firstReservoir.setName("a");
    var firstDiscoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(firstReservoir);
    var secondReservoir = new Reservoir();
    secondReservoir.setName("b");
    var secondDiscoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(secondReservoir);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(firstDiscoverySubmissionDetail))
        .thenReturn(List.of(firstDiscoveryReservoirSubmissionDetail, secondDiscoveryReservoirSubmissionDetail));

    var thirdReservoir = new Reservoir();
    thirdReservoir.setName("A");
    var thirdDiscoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(thirdReservoir);
    var fourthReservoir = new Reservoir();
    fourthReservoir.setName("B");
    var fourthDiscoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(fourthReservoir);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(secondDiscoverySubmissionDetail))
        .thenReturn(List.of(thirdDiscoveryReservoirSubmissionDetail, fourthDiscoveryReservoirSubmissionDetail));

    var reservoirViewFromFirstReservoir = new ReservoirView(firstReservoir.getId(), firstReservoir.getName());
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(firstDiscoveryReservoirSubmissionDetail))
        .thenReturn(reservoirViewFromFirstReservoir);
    var reservoirViewFromThirdReservoir = new ReservoirView(thirdReservoir.getId(), thirdReservoir.getName());
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(thirdDiscoveryReservoirSubmissionDetail))
        .thenReturn(reservoirViewFromThirdReservoir);

    assertThat(reservoirQueryService.getReservoirByNameForSubmissionDetail("a", submissionDetail.getId().toString()))
        .containsExactly(reservoirViewFromFirstReservoir, reservoirViewFromThirdReservoir);
  }
}