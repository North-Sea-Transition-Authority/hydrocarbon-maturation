package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class PermeabilityDuplicationServiceTest {
  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  private PermeabilityDuplicationService permeabilityDuplicationService;

  @Test
  void duplicateReservoirSection_PermeabilityExists_DoesDuplicate() {
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    var permeability = mock(PermeabilityDetail.class);
    when(permeabilityPersistenceService.getPermeabilityDetail(oldReservoirSubmissionDetail))
        .thenReturn(Optional.of(permeability));

    permeabilityDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, permeability,
        PermeabilityDetail.class);
  }

  @Test
  void duplicateReservoirSection_PermeabilityExists_field_DoesDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_CSR);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_CSR);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var permeability = mock(PermeabilityDetail.class);
    when(permeabilityPersistenceService.getPermeabilityDetail(oldReservoirSubmissionDetail)).thenReturn(Optional.of(permeability));

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    permeabilityDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, permeability,
        PermeabilityDetail.class);

    var argumentCaptor1 = ArgumentCaptor.forClass(ReservoirSubmissionDetail.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(argumentCaptor1.capture(), eq(probabilityDistribution1),
        eq(ProbabilityDistribution.class));
    assertThat(argumentCaptor1.getValue().getSubmissionDetail()).isEqualTo(newSubmissionDetail);
    var argumentCaptor2 = ArgumentCaptor.forClass(ReservoirSubmissionDetail.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(argumentCaptor2.capture(), eq(probabilityDistribution2),
        eq(ProbabilityDistribution.class));
    assertThat(argumentCaptor2.getValue().getSubmissionDetail()).isEqualTo(newSubmissionDetail);
  }

  @Test
  void duplicateReservoirSection_PermeabilityDoesNotExist_DoesNotDuplicate() {
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    when(permeabilityPersistenceService.getPermeabilityDetail(oldReservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    permeabilityDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(PermeabilityDetail.class), eq(PermeabilityDetail.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstances(
      ReservoirSubmissionDetail oldReservoirSubmissionDetail) {
    return when(
        probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail,
            Set.of(
                ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY,
                ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY,
                ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY
            )));
  }
}