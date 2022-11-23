package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

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
import org.junit.jupiter.api.Test;
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
class ReservoirPropertiesDuplicationServiceTest {
  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private ReservoirPropertiesDuplicationService reservoirPropertiesDuplicationService;

  @Test
  void duplicateSubmissionSection_ReservoirPropertyExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var reservoirProperty = mock(ReservoirProperty.class);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(oldSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, reservoirProperty,
        ReservoirProperty.class);
  }

  @Test
  void duplicateSubmissionSection_ReservoirPropertyDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ReservoirProperty.class), eq(ReservoirProperty.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"})
  void duplicateSubmissionSection_ProbabilityDistributionsExist_ProspectUpdate_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(oldSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_ProbabilityDistributionsExist_NonProspectUpdate_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstancesForNonProspectUpdate(oldSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"})
  void duplicateSubmissionSection_ProbabilityDistributionsDoNotExist_ProspectUpdate_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(oldSubmissionDetail)
        .thenReturn(Collections.emptyList());

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"PROSPECT_UPDATE"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_ProbabilityDistributionsDoNotExist_NonProspectUpdate_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstancesForNonProspectUpdate(oldSubmissionDetail)
        .thenReturn(Collections.emptyList());

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(SubmissionDetail oldSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForNonProspectUpdate(SubmissionDetail oldSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY
    )));
  }
}