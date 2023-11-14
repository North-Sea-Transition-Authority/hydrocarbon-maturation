package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.mockito.ArgumentCaptor;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotalsRepository;

@ExtendWith(MockitoExtension.class)
class FieldGasCaseVolumetricTotalsDuplicationServiceTest {

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Mock
  private GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository;

  @InjectMocks
  private FieldGasCaseVolumetricTotalsDuplicationService fieldGasCaseVolumetricTotalsDuplicationService;

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSection_TotalsExist_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var gasCaseVolumetricTotal = mock(GasCaseVolumetricTotal.class);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(gasCaseVolumetricTotal));

    fieldGasCaseVolumetricTotalsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    var argumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(argumentCaptor.capture(), eq(gasCaseVolumetricTotal),
        eq(CombinedCaseVolumetricTotal.class));
    assertThat(argumentCaptor.getValue()).isEqualTo(newSubmissionDetail);
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSection_TotalsDoNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var gasCaseVolumetricTotal = mock(GasCaseVolumetricTotal.class);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    fieldGasCaseVolumetricTotalsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, times(0))
        .duplicateChildAndReplaceParent(
            any(SubmissionDetail.class),
            eq(gasCaseVolumetricTotal),
            eq(CombinedCaseVolumetricTotal.class)
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSection_ProbabilityDistributionsExist_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstances(oldSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    fieldGasCaseVolumetricTotalsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    var argumentCaptor1 = ArgumentCaptor.forClass(SubmissionDetail.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(argumentCaptor1.capture(), eq(probabilityDistribution1),
        eq(ProbabilityDistribution.class));
    assertThat(argumentCaptor1.getValue()).isEqualTo(newSubmissionDetail);
    var argumentCaptor2 = ArgumentCaptor.forClass(SubmissionDetail.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(argumentCaptor2.capture(), eq(probabilityDistribution2),
        eq(ProbabilityDistribution.class));
    assertThat(argumentCaptor2.getValue()).isEqualTo(newSubmissionDetail);
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSection_ProbabilityDistributionsDoNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstances(oldSubmissionDetail)
        .thenReturn(Collections.emptyList());

    fieldGasCaseVolumetricTotalsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateReservoirSection_SubmissionDetailIsProspect_DoesNotDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.PROSPECT_UPDATE);

    fieldGasCaseVolumetricTotalsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstances(
      SubmissionDetail oldSubmissionDetail) {
    var applicableInstances = new java.util.HashSet<>(Set.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    ));
    if (!oldSubmissionDetail.isConceptSelectReportSubmission()) {
      applicableInstances.addAll(List.of(
          ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
          ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
      ));
    }

    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, applicableInstances));
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }
}