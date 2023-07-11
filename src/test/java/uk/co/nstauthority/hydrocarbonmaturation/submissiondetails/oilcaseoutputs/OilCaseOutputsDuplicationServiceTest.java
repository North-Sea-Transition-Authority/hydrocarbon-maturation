package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsDuplicationServiceTest {
  @Mock
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private OilCaseOutputsDuplicationService oilCaseOutputsDuplicationService;

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY_FROM_PROSPECT", "CREATE_FIELD", "CREATE_CSR"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_OilCaseOutputExists_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var oilCaseOutput = mock(OilCaseOutput.class);
    when(oilCaseOutputsPersistenceService.getOilCaseOutput(oldSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, oilCaseOutput,
        OilCaseOutput.class);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY_FROM_PROSPECT", "CREATE_FIELD", "CREATE_CSR"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_OilCaseOutputDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OilCaseOutput.class), eq(OilCaseOutput.class));
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateSubmissionSection_ProbabilityDistributionsExist_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstances(oldSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateSubmissionSection_ProbabilityDistributionsDoNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstances(oldSubmissionDetail)
        .thenReturn(Collections.emptyList());

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateSubmissionSection_DoesNotDuplicateToNonApplicableSubmissionType(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OilCaseOutput.class), eq(OilCaseOutput.class));
    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateSubmissionSection_SubmissionDetailIsDiscoveryFromProspect_DoesNotDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstances(SubmissionDetail oldSubmissionDetail) {
    var applicableInstances = Set.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
    );
    var resourceType = oldSubmissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.DISCOVERY) {
      applicableInstances = Set.of(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
      );
    }

    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, applicableInstances));
  }

  private static Stream<Arguments> allDiscoveryAndProspectSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> SubmissionDetailUtil.isDiscoverySubmission(submissionType) || SubmissionDetailUtil.isProspectSubmission(submissionType))
        .map(Arguments::of);
  }
}