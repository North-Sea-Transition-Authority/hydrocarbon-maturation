package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
class MixedCaseOutputsDuplicationServiceTest {
  @Mock
  private MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private MixedCaseOutputsDuplicationService mixedCaseOutputsDuplicationService;

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateReservoirSection_MixedCaseOutputExists_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var mixedCaseOutput = mock(MixedCaseOutput.class);
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(oldReservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));

    mixedCaseOutputsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, mixedCaseOutput,
        MixedCaseOutput.class);
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateReservoirSection_MixedCaseOutputDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(oldReservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    mixedCaseOutputsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(MixedCaseOutput.class), eq(MixedCaseOutput.class));
  }

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

    mixedCaseOutputsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
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

    mixedCaseOutputsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

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

    mixedCaseOutputsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(MixedCaseOutput.class), eq(MixedCaseOutput.class));
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

    mixedCaseOutputsDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstances(
      ReservoirSubmissionDetail oldReservoirSubmissionDetail) {
    var applicableInstances = Set.of(
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
        ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
        ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
    );
    var resourceType = oldReservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType();
    if (resourceType == ResourceType.DISCOVERY) {
      applicableInstances = Set.of(
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }

    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail, applicableInstances));
  }

  private static Stream<Arguments> allDiscoveryAndProspectSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> SubmissionDetailUtil.isDiscoverySubmission(submissionType) || SubmissionDetailUtil.isProspectSubmission(submissionType))
        .map(Arguments::of);
  }
}