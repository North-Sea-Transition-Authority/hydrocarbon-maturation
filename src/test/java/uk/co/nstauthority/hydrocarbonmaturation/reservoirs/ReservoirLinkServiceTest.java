package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
}