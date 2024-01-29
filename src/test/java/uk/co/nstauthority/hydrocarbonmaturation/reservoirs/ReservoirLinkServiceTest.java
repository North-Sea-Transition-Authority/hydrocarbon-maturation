package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class ReservoirLinkServiceTest {

  @Mock
  private ReservoirLinkRepository reservoirLinkRepository;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private EntityManager entityManager;

  @Mock
  private SubmissionResourceService submissionResourceService;

  @InjectMocks
  private ReservoirLinkService reservoirLinkService;

  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @Test
  void constructReservoirLinks() {
    var reservoir1 = new Reservoir();
    var reservoir2 = new Reservoir();
    var discoveryReservoirs = List.of(reservoir1, reservoir2);

    assertThat(reservoirLinkService.constructReservoirLinks(reservoirSubmissionDetail, discoveryReservoirs))
        .extracting(
            ReservoirLink::getReservoirSubmissionDetail,
            ReservoirLink::getLinkedReservoir
        ).containsExactly(
            tuple(reservoirSubmissionDetail, reservoir1),
            tuple(reservoirSubmissionDetail, reservoir2)
        );
  }

  @Test
  void constructReservoirLinks_emptyList() {
    assertThat(reservoirLinkService.constructReservoirLinks(reservoirSubmissionDetail, List.of())).isEmpty();
  }

  @Test
  void saveReservoirLinks_verifyCalls() {
    var reservoirLinks = List.of(new ReservoirLink(), new ReservoirLink());
    reservoirLinkService.saveReservoirLinks(reservoirLinks);
    verify(reservoirLinkRepository, times(1)).saveAll(reservoirLinks);
  }

  @Test
  void deleteReservoirLinks_verifyCalls() {
    var reservoirLinks = List.of(new ReservoirLink(), new ReservoirLink());
    reservoirLinkService.deleteReservoirLinks(reservoirLinks);
    verify(reservoirLinkRepository, times(1)).deleteAll(reservoirLinks);
    verify(entityManager, times(1)).flush();
  }

  @Test
  void getReservoirLinksByReservoirSubmissionDetail_verifyCalls() {
    reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirLinkRepository, times(1)).findReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getReservoirViewFromReservoirSubmissionDetail() {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName("Resource name");
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(reservoirSubmissionDetail.getSubmissionDetail()))
        .thenReturn(resourceDetail);

    var reservoir = reservoirSubmissionDetail.getReservoir();
    var expectedReservoirViewName = "%s (%s)".formatted(reservoir.getName(), resourceDetail.getResourceName());
    assertThat(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .extracting(
            ReservoirView::getId,
            ReservoirView::getName
        ).containsExactly(
            reservoir.getId().toString(),
            expectedReservoirViewName
        );
  }

  @Test
  void getExtantReservoirLinks() {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    var linkedReservoirSubmissionDetail1 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var linkedReservoirSubmissionDetail2 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    linkedReservoirSubmissionDetail2.getSubmissionDetail().setStatus(SubmissionDetailStatus.DELETED);
    var linkedReservoirSubmissionDetail3 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var linkedReservoirSubmissionDetail4 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    linkedReservoirSubmissionDetail4.getSubmissionDetail().getSubmission().setWithdrawn(true);
    var reservoirLink1 = new ReservoirLink();
    var linkedReservoir1 = new Reservoir();
    reservoirLink1.setLinkedReservoir(linkedReservoir1);
    reservoirLink1.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail1);
    var reservoirLink2 = new ReservoirLink();
    var linkedReservoir2 = new Reservoir();
    reservoirLink2.setLinkedReservoir(linkedReservoir2);
    reservoirLink2.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail2);
    var reservoirLink3 = new ReservoirLink();
    var linkedReservoir3 = new Reservoir();
    reservoirLink3.setLinkedReservoir(linkedReservoir3);
    reservoirLink3.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail3);
    var reservoirLink4 = new ReservoirLink();
    var linkedReservoir4 = new Reservoir();
    reservoirLink4.setLinkedReservoir(linkedReservoir4);
    reservoirLink4.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail4);
    when(reservoirLinkRepository.findReservoirLinksByLinkedReservoir(reservoir))
        .thenReturn(List.of(reservoirLink1, reservoirLink2, reservoirLink3, reservoirLink4));

    assertThat(reservoirLinkService.getExtantReservoirLinks(reservoirSubmissionDetail))
        .containsExactly(reservoirLink1, reservoirLink3);
  }

  @Test
  void reservoirLinksExistForReservoir_extantReservoirsExist() {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    var linkedReservoirSubmissionDetail1 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var reservoirLink1 = new ReservoirLink();
    var linkedReservoir1 = new Reservoir();
    linkedReservoir1.setName("test1");
    reservoirLink1.setLinkedReservoir(linkedReservoir1);
    reservoirLink1.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail1);
    when(reservoirLinkRepository.findReservoirLinksByLinkedReservoir(reservoir))
        .thenReturn(List.of(reservoirLink1));

    assertTrue(reservoirLinkService.reservoirLinksExistForReservoir(reservoirSubmissionDetail));
  }

  @Test
  void reservoirLinksExistForReservoir_noReservoirsExist() {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    when(reservoirLinkRepository.findReservoirLinksByLinkedReservoir(reservoir)).thenReturn(Collections.emptyList());

    assertFalse(reservoirLinkService.reservoirLinksExistForReservoir(reservoirSubmissionDetail));
  }

  @Test
  void reservoirLinksExistForReservoir_onlyDeletedAndWithdrawSubmissionReservoirsExist() {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    var linkedReservoirSubmissionDetail1 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    linkedReservoirSubmissionDetail1.getSubmissionDetail().setStatus(SubmissionDetailStatus.DELETED);
    var linkedReservoirSubmissionDetail2 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    linkedReservoirSubmissionDetail2.getSubmissionDetail().getSubmission().setWithdrawn(true);
    var reservoirLink1 = new ReservoirLink();
    var linkedReservoir1 = new Reservoir();
    reservoirLink1.setLinkedReservoir(linkedReservoir1);
    reservoirLink1.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail1);
    var reservoirLink2 = new ReservoirLink();
    var linkedReservoir2 = new Reservoir();
    reservoirLink2.setLinkedReservoir(linkedReservoir2);
    reservoirLink2.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail2);
    when(reservoirLinkRepository.findReservoirLinksByLinkedReservoir(reservoir))
        .thenReturn(List.of(reservoirLink1, reservoirLink2));

    assertFalse(reservoirLinkService.reservoirLinksExistForReservoir(reservoirSubmissionDetail));
  }

  @Test
  void getAssociatedFieldNamesFromLinkedDiscovery() {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    var linkedReservoirSubmissionDetail1 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var linkedReservoirSubmissionDetail2 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var reservoirLink1 = new ReservoirLink();
    var linkedReservoir1 = new Reservoir();
    var linkedReservoirName1 = "test1";
    linkedReservoir1.setName(linkedReservoirName1);
    reservoirLink1.setLinkedReservoir(linkedReservoir1);
    reservoirLink1.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail1);
    var reservoirLink2 = new ReservoirLink();
    var linkedReservoir2 = new Reservoir();
    var linkedReservoirName2 = "test2";
    linkedReservoir2.setName(linkedReservoirName2);
    reservoirLink2.setLinkedReservoir(linkedReservoir2);
    reservoirLink2.setReservoirSubmissionDetail(linkedReservoirSubmissionDetail2);
    when(reservoirLinkRepository.findReservoirLinksByLinkedReservoir(reservoir))
        .thenReturn(List.of(reservoirLink1, reservoirLink2));
    when(submissionResourceService.getResourceNameOrDefault(linkedReservoirSubmissionDetail1.getSubmissionDetail()))
        .thenReturn(linkedReservoirName1);
    when(submissionResourceService.getResourceNameOrDefault(linkedReservoirSubmissionDetail2.getSubmissionDetail()))
        .thenReturn(linkedReservoirName2);

    assertThat(reservoirLinkService.getAssociatedFieldNamesFromLinkedDiscovery(reservoirSubmissionDetail))
        .containsExactly(linkedReservoirName1, linkedReservoirName2);
  }
}