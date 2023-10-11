package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class MixedCaseVolumetricParametersDuplicationServiceTest {
  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private MixedCaseVolumetricParametersDuplicationService mixedCaseVolumetricParametersDuplicationService;

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateReservoirSection_ProbabilityDistributionsExist_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    mixedCaseVolumetricParametersDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    var argumentCaptor1 = ArgumentCaptor.forClass(ReservoirSubmissionDetail.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(argumentCaptor1.capture(), eq(probabilityDistribution1),
        eq(ProbabilityDistribution.class));
    assertThat(argumentCaptor1.getValue().getSubmissionDetail()).isEqualTo(newSubmissionDetail);
    var argumentCaptor2 = ArgumentCaptor.forClass(ReservoirSubmissionDetail.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(argumentCaptor2.capture(), eq(probabilityDistribution2),
        eq(ProbabilityDistribution.class));
    assertThat(argumentCaptor2.getValue().getSubmissionDetail()).isEqualTo(newSubmissionDetail);
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateReservoirSection_ProbabilityDistributionsDoNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    whenGetAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail)
        .thenReturn(Collections.emptyList());

    mixedCaseVolumetricParametersDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateReservoirSection_DoesNotDuplicateToNonApplicableSubmissionType(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    mixedCaseVolumetricParametersDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateReservoirSection_SubmissionDetailIsDiscoveryFromProspect_DoesNotDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    mixedCaseVolumetricParametersDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstances(
      ReservoirSubmissionDetail oldReservoirSubmissionDetail) {
    var resourceType = oldReservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      return when(
          probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail,
              Set.of(
                  ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
                  ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT,
                  ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
                  ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT,
                  ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
                  ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
                  ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
                  ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT
              )));
    } else if (resourceType == ResourceType.DISCOVERY) {
      return when(
          probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail,
              Set.of(
                  ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
                  ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_DISCOVERY,
                  ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
                  ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_DISCOVERY,
                  ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
                  ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
                  ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
                  ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_DISCOVERY
              )));
    }
    return when(
        probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(eq(oldReservoirSubmissionDetail),
            any()));
  }

  private static Stream<Arguments> allDiscoveryAndProspectSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> SubmissionDetailUtil.isDiscoverySubmission(submissionType) || SubmissionDetailUtil.isProspectSubmission(submissionType))
        .map(Arguments::of);
  }
}