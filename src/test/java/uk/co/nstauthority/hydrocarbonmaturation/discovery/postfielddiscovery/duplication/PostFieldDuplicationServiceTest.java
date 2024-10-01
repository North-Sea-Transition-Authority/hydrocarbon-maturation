package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbonsDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class PostFieldDuplicationServiceTest {

  @Mock
  private PostFieldSectionDuplicationService postFieldSectionDuplicationService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private ReservoirHydrocarbonsDuplicationService reservoirHydrocarbonsDuplicationService;

  private PostFieldDuplicationService postFieldDuplicationService;

  @BeforeEach
  void setUp() {
    postFieldDuplicationService = new PostFieldDuplicationService(List.of(postFieldSectionDuplicationService),
        reservoirSubmissionDetailPersistenceService, reservoirHydrocarbonsDuplicationService);
  }

  @Test
  void duplicateSubmissionDetails() {
    var fieldSubmissionDetail = mock(SubmissionDetail.class);
    var discoverySubmissionDetail = mock(SubmissionDetail.class);

    var reservoir = new Reservoir();
    var fieldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils
        .createReservoirSubmissionDetail(reservoir, fieldSubmissionDetail);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils
        .createReservoirSubmissionDetail(reservoir, discoverySubmissionDetail);

    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(fieldSubmissionDetail))
        .thenReturn(List.of(fieldReservoirSubmissionDetail));
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(discoverySubmissionDetail))
        .thenReturn(List.of(discoveryReservoirSubmissionDetail));

    postFieldDuplicationService.duplicateSubmissionDetails(fieldSubmissionDetail, discoverySubmissionDetail);

    verify(postFieldSectionDuplicationService).duplicateSubmissionSection(fieldSubmissionDetail, discoverySubmissionDetail);
    verify(reservoirHydrocarbonsDuplicationService).duplicateReservoirSection(fieldReservoirSubmissionDetail, discoveryReservoirSubmissionDetail);
  }

}
