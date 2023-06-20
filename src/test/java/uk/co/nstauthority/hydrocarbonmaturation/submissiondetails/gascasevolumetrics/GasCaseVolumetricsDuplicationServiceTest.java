package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricsDuplicationServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private GasCaseVolumetricsDuplicationService gasCaseVolumetricsDuplicationService;

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateSubmissionSection_ProbabilityDistributionsExist_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstances(oldSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    gasCaseVolumetricsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

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

    gasCaseVolumetricsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @MethodSource("allDiscoveryAndProspectSubmissionTypes")
  void duplicateSubmissionSection_DoesNotDuplicateToNonApplicableSubmissionType(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    gasCaseVolumetricsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateSubmissionSection_SubmissionDetailIsDiscoveryFromProspect_DoesNotDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    gasCaseVolumetricsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstances(SubmissionDetail oldSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION,
        ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
        ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
    )));
  }

  private static Stream<Arguments> allDiscoveryAndProspectSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> SubmissionDetailUtil.isDiscoverySubmission(submissionType) || SubmissionDetailUtil.isProspectSubmission(submissionType))
        .map(Arguments::of);
  }
}