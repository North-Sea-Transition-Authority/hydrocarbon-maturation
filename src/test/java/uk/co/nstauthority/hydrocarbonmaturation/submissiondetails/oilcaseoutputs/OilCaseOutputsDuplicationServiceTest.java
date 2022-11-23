package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
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
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"})
  void duplicateSubmissionSection_OilCaseOutputExists_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    var oilCaseOutput = mock(OilCaseOutput.class);
    when(oilCaseOutputsPersistenceService.getOilCaseOutput(oldSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, oilCaseOutput,
        OilCaseOutput.class);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"})
  void duplicateSubmissionSection_OilCaseOutputDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OilCaseOutput.class), eq(OilCaseOutput.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"})
  void duplicateSubmissionSection_ProbabilityDistributionsExist_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

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
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"})
  void duplicateSubmissionSection_ProbabilityDistributionsDoNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstances(oldSubmissionDetail)
        .thenReturn(Collections.emptyList());

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_DoesNotDuplicateToNonApplicableSubmissionType(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    oilCaseOutputsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OilCaseOutput.class), eq(OilCaseOutput.class));
    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstances(SubmissionDetail oldSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
    )));
  }
}